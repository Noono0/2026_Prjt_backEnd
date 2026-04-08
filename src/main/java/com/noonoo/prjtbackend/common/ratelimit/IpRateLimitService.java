package com.noonoo.prjtbackend.common.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IpRateLimitService {

    private final RateLimitProperties properties;

    /** 규칙별 버킷 생성기 — 동일 규칙은 동일 refill 정책 */
    private final ConcurrentHashMap<String, BucketFactory> factories = new ConcurrentHashMap<>();

    private final Cache<String, Bucket> bucketCache = Caffeine.newBuilder()
            .maximumSize(200_000)
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build();

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    /**
     * @return 소비 성공 여부
     */
    public boolean tryConsume(RateLimitRule rule, String clientIp) {
        if (!properties.isEnabled()) {
            return true;
        }
        String key = rule.name() + ":" + clientIp;
        Bucket bucket = bucketCache.get(key, k -> newBucket(rule));
        return bucket.tryConsume(1);
    }

    /**
     * 429 응답 시 Retry-After(초) 계산용 — 비활성화면 소비된 것으로 간주
     */
    public ConsumptionProbe tryConsumeAndProbe(RateLimitRule rule, String clientIp) {
        if (!properties.isEnabled()) {
            return ConsumptionProbe.consumed(1L, 1L);
        }
        String key = rule.name() + ":" + clientIp;
        Bucket bucket = bucketCache.get(key, k -> newBucket(rule));
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    private Bucket newBucket(RateLimitRule rule) {
        BucketFactory factory = factories.computeIfAbsent(rule.name(), n -> createFactory(rule));
        return Bucket.builder().addLimit(factory.bandwidth()).build();
    }

    private BucketFactory createFactory(RateLimitRule rule) {
        return switch (rule) {
            case LOGIN -> () -> Bandwidth.builder()
                    .capacity(properties.getLoginPerMinute())
                    .refillGreedy(properties.getLoginPerMinute(), Duration.ofMinutes(1))
                    .build();
            case PASSWORD_RESET_REQUEST -> () -> Bandwidth.builder()
                    .capacity(properties.getPasswordResetRequestPerHour())
                    .refillGreedy(properties.getPasswordResetRequestPerHour(), Duration.ofHours(1))
                    .build();
            case PASSWORD_RESET_VERIFY -> () -> Bandwidth.builder()
                    .capacity(properties.getPasswordResetVerifyPerMinute())
                    .refillGreedy(properties.getPasswordResetVerifyPerMinute(), Duration.ofMinutes(1))
                    .build();
            case PASSWORD_RESET_COMPLETE -> () -> Bandwidth.builder()
                    .capacity(properties.getPasswordResetCompletePerMinute())
                    .refillGreedy(properties.getPasswordResetCompletePerMinute(), Duration.ofMinutes(1))
                    .build();
            case OAUTH_SYNC -> () -> Bandwidth.builder()
                    .capacity(properties.getOauthSyncPerMinute())
                    .refillGreedy(properties.getOauthSyncPerMinute(), Duration.ofMinutes(1))
                    .build();
            case OAUTH_ESTABLISH -> () -> Bandwidth.builder()
                    .capacity(properties.getOauthEstablishPerMinute())
                    .refillGreedy(properties.getOauthEstablishPerMinute(), Duration.ofMinutes(1))
                    .build();
            case HEARTBEAT -> () -> Bandwidth.builder()
                    .capacity(properties.getHeartbeatPerMinute())
                    .refillGreedy(properties.getHeartbeatPerMinute(), Duration.ofMinutes(1))
                    .build();
        };
    }

    @FunctionalInterface
    private interface BucketFactory {
        Bandwidth bandwidth();
    }
}
