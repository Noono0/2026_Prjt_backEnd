package com.noonoo.prjtbackend.gamniverseprofile.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 숲 LIVE 판별(Playwright) 부하 완화용 설정. TTL은 "한 바퀴 돌 동안" 캐시가 유지되도록
 * {@code schedulerFixedDelayMs * ceil(프로필수 / maxResolvePerTick)} 보다 크게 두는 것을 권장합니다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.soop-live")
public class SoopLiveStatusProperties {

    /** LIVE 결과 캐시 TTL (밀리초). 짧을수록 Playwright 호출이 잦아집니다. */
    private long cacheTtlMs = 240_000L;

    /** 백그라운드 캐시 워밍 스케줄러 사용 여부. {@code false}면 API는 캐시가 없을 때 오프라인으로만 보입니다. */
    private boolean schedulerEnabled = true;

    private long schedulerInitialDelayMs = 15_000L;

    /** 스케줄러 주기. 한 틱당 {@link #maxResolvePerTick}건만 실제 갱신합니다. */
    private long schedulerFixedDelayMs = 12_000L;

    /** 스케줄 한 번에 Playwright로 새로 조회할 최대 링크 수(캐시 미스·만료분만). */
    private int maxResolvePerTick = 8;
}
