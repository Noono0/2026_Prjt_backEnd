package com.noonoo.prjtbackend.auth.controller;

import com.noonoo.prjtbackend.auth.dto.LoginRequest;
import com.noonoo.prjtbackend.auth.dto.PasswordResetCompleteDto;
import com.noonoo.prjtbackend.auth.dto.PasswordResetRequestDto;
import com.noonoo.prjtbackend.auth.dto.PasswordResetVerifyDto;
import com.noonoo.prjtbackend.auth.dto.PasswordResetVerifyResponseDto;
import com.noonoo.prjtbackend.auth.dto.OauthEstablishSessionRequest;
import com.noonoo.prjtbackend.auth.dto.OauthMemberSyncRequest;
import com.noonoo.prjtbackend.auth.dto.OauthMemberSyncResponse;
import com.noonoo.prjtbackend.auth.dto.TokenResponse;
import com.noonoo.prjtbackend.auth.service.OauthMemberSyncService;
import com.noonoo.prjtbackend.auth.service.PasswordResetService;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.security.CustomUserDetails;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final PasswordResetService passwordResetService;
    private final OauthMemberSyncService oauthMemberSyncService;
    private final UserDetailsService userDetailsService;

    @Value("${app.oauth-sync.secret:}")
    private String oauthSyncSecret;

    private static final String PW_RESET_SENT_MSG =
            "요청이 접수되었습니다. 가입 시 등록한 이메일로 인증코드가 발송되었습니다. 메일이 오지 않으면 스팸함을 확인해 주세요.";

    /**
     * 로그인: Spring Security {@link SecurityContextHolder} 에 인증을 올려
     * 세션 기반으로 이후 API의 {@code @PreAuthorize} / DB 권한이 동작합니다.
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMemberId(), request.getMemberPwd())
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        session.setAttribute(LOGIN_MEMBER_SEQ, principal.getMemberSeq());
        session.setAttribute(LOGIN_MEMBER_ID, principal.getUsername());

        String loginIp = RequestContext.getClientIp();
        if (!StringUtils.hasText(loginIp)) {
            loginIp = "127.0.0.1";
        }
        memberMapper.updateMemberLastLogin(principal.getMemberSeq(), loginIp);

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

    /**
     * NextAuth OAuth 로그인 직후 서버(BFF)에서만 호출 — {@code X-OAuth-Sync-Secret} 와 {@code OAUTH_SYNC_SECRET} 일치 필요.
     * 신규 시 member 행 + MEMBER_ROLE(USER) + 가입 포인트, 기존 시 프로필/동기화 시각 갱신.
     */
    @PostMapping("/oauth/sync")
    public ApiResponse<OauthMemberSyncResponse> oauthSync(
            @RequestHeader(value = "X-OAuth-Sync-Secret", required = false) String secret,
            @Valid @RequestBody OauthMemberSyncRequest body,
            HttpServletRequest request
    ) {
        if (!StringUtils.hasText(oauthSyncSecret)) {
            return ApiResponse.fail("OAUTH_SYNC_DISABLED", "서버에 OAUTH_SYNC_SECRET 이 설정되어 있지 않습니다.");
        }
        if (!StringUtils.hasText(secret) || !oauthSyncSecret.equals(secret)) {
            return ApiResponse.fail("UNAUTHORIZED", "동기화 비밀이 올바르지 않습니다.");
        }
        try {
            OauthMemberSyncResponse data = oauthMemberSyncService.sync(body, request);
            return ApiResponse.ok("회원 동기화 완료", data);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("BAD_REQUEST", e.getMessage());
        } catch (IllegalStateException e) {
            return ApiResponse.fail("SERVER_ERROR", e.getMessage());
        }
    }

    /**
     * NextAuth 세션과 동일한 회원으로 Spring HttpSession + SecurityContext 를 맞춤.
     * 브라우저는 Next 의 프록시(`/api/auth/spring-sync`)만 호출하고, Secret 은 서버 간 공유.
     */
    @PostMapping("/oauth/establish-session")
    public ApiResponse<TokenResponse> oauthEstablishSession(
            @RequestHeader(value = "X-OAuth-Sync-Secret", required = false) String secret,
            @Valid @RequestBody OauthEstablishSessionRequest body,
            HttpSession httpSession
    ) {
        if (!StringUtils.hasText(oauthSyncSecret) || !oauthSyncSecret.equals(secret)) {
            return ApiResponse.fail("UNAUTHORIZED", "동기화 비밀이 올바르지 않습니다.");
        }
        MemberDto m = memberMapper.findMemberById(body.getMemberSeq());
        if (m == null
                || m.getMemberId() == null
                || !m.getMemberId().equals(body.getMemberId())) {
            return ApiResponse.fail("BAD_REQUEST", "회원 정보가 일치하지 않습니다.");
        }
        String status = m.getStatusCode() != null ? m.getStatusCode() : "ACTIVE";
        if (!"ACTIVE".equalsIgnoreCase(status)) {
            return ApiResponse.fail("FORBIDDEN", "로그인할 수 없는 계정 상태입니다.");
        }

        UserDetails ud = userDetailsService.loadUserByUsername(m.getMemberId());
        Authentication auth =
                new UsernamePasswordAuthenticationToken(ud, ud.getPassword(), ud.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        httpSession.setAttribute(LOGIN_MEMBER_SEQ, m.getMemberSeq());
        httpSession.setAttribute(LOGIN_MEMBER_ID, m.getMemberId());

        String oauthLoginIp = RequestContext.getClientIp();
        if (!StringUtils.hasText(oauthLoginIp)) {
            oauthLoginIp = "127.0.0.1";
        }
        memberMapper.updateMemberLastLogin(m.getMemberSeq(), oauthLoginIp);

        String profileUrl = m.getProfileImageUrl();
        String nickname = m.getNickname();
        String memberName = m.getMemberName();
        return ApiResponse.ok(
                "세션이 연결되었습니다.",
                new TokenResponse(
                        m.getMemberSeq(),
                        m.getMemberId(),
                        "session",
                        "session",
                        profileUrl,
                        nickname,
                        memberName));
    }

    /** 비밀번호 찾기 1단계 — 아이디·이메일 일치 시 등록 이메일로 6자리 코드 발송 (수신 도메인 무관: naver, gmail, kakao 등) */
    @PostMapping("/password-reset/request")
    public ApiResponse<Void> passwordResetRequest(@RequestBody PasswordResetRequestDto body, HttpServletRequest request) {
        passwordResetService.requestCode(body, request);
        return ApiResponse.ok(PW_RESET_SENT_MSG, null);
    }

    @PostMapping("/password-reset/verify")
    public ApiResponse<PasswordResetVerifyResponseDto> passwordResetVerify(@RequestBody PasswordResetVerifyDto body) {
        PasswordResetVerifyResponseDto data = passwordResetService.verifyCode(body);
        return ApiResponse.ok("인증되었습니다. 새 비밀번호를 입력해 주세요.", data);
    }

    @PostMapping("/password-reset/complete")
    public ApiResponse<Void> passwordResetComplete(
            @RequestBody PasswordResetCompleteDto body,
            HttpServletRequest request
    ) {
        passwordResetService.completeReset(body, request);
        return ApiResponse.ok("비밀번호가 변경되었습니다. 새 비밀번호로 로그인해 주세요.", null);
    }
}
