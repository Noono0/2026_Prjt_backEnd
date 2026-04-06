package com.noonoo.prjtbackend.auth.oauth;

import java.util.Locale;

/**
 * member.oauth_provider 및 NextAuth provider 문자열과의 매핑.
 */
public enum MemberOAuthProvider {
    GOOGLE("g_"),
    NAVER("n_"),
    KAKAO("k_");

    private final String memberIdPrefix;

    MemberOAuthProvider(String memberIdPrefix) {
        this.memberIdPrefix = memberIdPrefix;
    }

    public String getMemberIdPrefix() {
        return memberIdPrefix;
    }

    public static MemberOAuthProvider fromApiCode(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("provider 가 필요합니다.");
        }
        String p = raw.trim().toUpperCase(Locale.ROOT);
        try {
            return MemberOAuthProvider.valueOf(p);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + raw);
        }
    }
}
