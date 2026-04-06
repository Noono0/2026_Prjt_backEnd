package com.noonoo.prjtbackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * NextAuth(서버)에서 호출하는 회원 동기화 요청.
 */
@Getter
@Setter
@ToString
public class OauthMemberSyncRequest {

    /** GOOGLE | NAVER | KAKAO */
    @NotBlank
    private String provider;

    /** 제공자 계정 고유 ID (Google sub, Kakao id 등) */
    @NotBlank
    private String subject;

    private String email;
    private String memberName;
    private String nickname;
    /** OAuth 프로필 이미지 URL (https) */
    private String profileImageUrl;
}
