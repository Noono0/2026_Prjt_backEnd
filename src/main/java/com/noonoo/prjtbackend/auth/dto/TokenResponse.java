package com.noonoo.prjtbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private Long memberSeq;
    private String memberId;
    private String accessToken;
    private String refreshToken;
    /** 회원 프로필 이미지 URL (세션 로그인·/me 응답용) */
    private String profileImageUrl;
    /** 닉네임(게시·UI 표시 우선) */
    private String nickname;
    /** 실명(회원명) */
    private String memberName;
}
