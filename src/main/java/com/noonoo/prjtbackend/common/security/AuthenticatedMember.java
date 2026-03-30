package com.noonoo.prjtbackend.common.security;

/**
 * 현재 요청의 로그인 회원 (세션 + Spring Security 통합).
 */
public record AuthenticatedMember(Long memberSeq, String memberId) {}
