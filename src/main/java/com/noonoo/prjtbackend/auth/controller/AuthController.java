package com.noonoo.prjtbackend.auth.controller;

import com.noonoo.prjtbackend.auth.dto.LoginRequest;
import com.noonoo.prjtbackend.auth.dto.TokenResponse;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.security.CustomUserDetails;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    public static final String LOGIN_MEMBER_SEQ = "LOGIN_MEMBER_SEQ";
    public static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";

    private final AuthenticationManager authenticationManager;
    private final MemberMapper memberMapper;

    /**
     * 로그인: Spring Security {@link SecurityContextHolder} 에 인증을 올려
     * 세션 기반으로 이후 API의 {@code @PreAuthorize} / DB 권한이 동작합니다.
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMemberId(), request.getMemberPwd())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        session.setAttribute(LOGIN_MEMBER_SEQ, principal.getMemberSeq());
        session.setAttribute(LOGIN_MEMBER_ID, principal.getUsername());

        MemberDto member = memberMapper.findLoginMember(principal.getUsername());
        String profileUrl = member != null ? member.getProfileImageUrl() : null;
        String nickname = member != null ? member.getNickname() : null;
        String memberName = member != null ? member.getMemberName() : null;
        TokenResponse response = new TokenResponse(
                principal.getMemberSeq(),
                principal.getUsername(),
                "session",
                "session",
                profileUrl,
                nickname,
                memberName
        );
        return ApiResponse.ok(response);
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ApiResponse.ok("로그아웃 완료");
    }

    @GetMapping("/me")
    public ApiResponse<TokenResponse> me(HttpSession session) {
        Object memberSeqObj = session.getAttribute(LOGIN_MEMBER_SEQ);
        Object memberIdObj = session.getAttribute(LOGIN_MEMBER_ID);
        if (!(memberIdObj instanceof String memberId)) {
            return ApiResponse.fail("AUTH_REQUIRED", "로그인이 필요합니다.");
        }
        Long memberSeq = null;
        if (memberSeqObj instanceof Number n) {
            memberSeq = n.longValue();
        }
        MemberDto member = memberMapper.findLoginMember(memberId);
        String profileUrl = member != null ? member.getProfileImageUrl() : null;
        String nickname = member != null ? member.getNickname() : null;
        String memberName = member != null ? member.getMemberName() : null;
        return ApiResponse.ok(
                new TokenResponse(memberSeq, memberId, "session", "session", profileUrl, nickname, memberName));
    }
}
