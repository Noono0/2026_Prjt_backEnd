package com.noonoo.prjtbackend.auth.serviceImpl;

import com.noonoo.prjtbackend.auth.dto.LoginRequest;
import com.noonoo.prjtbackend.auth.dto.TokenResponse;
import com.noonoo.prjtbackend.auth.service.AuthService;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse login(LoginRequest request) {
        MemberDto member = memberMapper.findLoginMember(request.getMemberId());

        if (member == null) {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }

        if (!passwordEncoder.matches(request.getMemberPwd(), member.getMemberPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return new TokenResponse(
                member.getMemberSeq(),
                member.getMemberId(),
                "sample-access-token",
                "sample-refresh-token",
                member.getProfileImageUrl(),
                member.getNickname(),
                member.getMemberName()
        );
    }
}