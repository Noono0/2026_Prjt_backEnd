package com.noonoo.prjtbackend.common.ratelimit;

/**
 * IP 기반 제한 규칙 — {@link IpRateLimitFilter} 와 설정 키가 1:1
 */
public enum RateLimitRule {
    LOGIN,
    PASSWORD_RESET_REQUEST,
    PASSWORD_RESET_VERIFY,
    PASSWORD_RESET_COMPLETE,
    OAUTH_SYNC,
    OAUTH_ESTABLISH,
    HEARTBEAT
}
