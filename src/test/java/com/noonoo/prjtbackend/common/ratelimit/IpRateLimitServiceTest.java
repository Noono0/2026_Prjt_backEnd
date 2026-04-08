package com.noonoo.prjtbackend.common.ratelimit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpRateLimitServiceTest {

    @Test
    void loginBucketExhaustsAfterLimit() {
        RateLimitProperties p = new RateLimitProperties();
        p.setEnabled(true);
        p.setLoginPerMinute(3);
        IpRateLimitService svc = new IpRateLimitService(p);
        String ip = "203.0.113.10";
        assertThat(svc.tryConsume(RateLimitRule.LOGIN, ip)).isTrue();
        assertThat(svc.tryConsume(RateLimitRule.LOGIN, ip)).isTrue();
        assertThat(svc.tryConsume(RateLimitRule.LOGIN, ip)).isTrue();
        assertThat(svc.tryConsume(RateLimitRule.LOGIN, ip)).isFalse();
    }

    @Test
    void whenDisabledAlwaysAllows() {
        RateLimitProperties p = new RateLimitProperties();
        p.setEnabled(false);
        p.setLoginPerMinute(1);
        IpRateLimitService svc = new IpRateLimitService(p);
        String ip = "203.0.113.11";
        assertThat(svc.tryConsume(RateLimitRule.LOGIN, ip)).isTrue();
        assertThat(svc.tryConsume(RateLimitRule.LOGIN, ip)).isTrue();
    }
}
