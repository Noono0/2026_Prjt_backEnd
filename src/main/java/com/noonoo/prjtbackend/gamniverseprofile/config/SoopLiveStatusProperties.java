package com.noonoo.prjtbackend.gamniverseprofile.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.soop-live")
public class SoopLiveStatusProperties {

    /** LIVE 배치 API 사용 여부. {@code false}면 빈 결과만 반환합니다. */
    private boolean apiEnabled = true;

    /** LIVE 결과 캐시 TTL (밀리초). 요청이 있을 때만 갱신됩니다. */
    private long cacheTtlMs = 60_000L;

    /** 백그라운드 Playwright 스케줄러 (권장: {@code false}) */
    private boolean schedulerEnabled = false;

    private long schedulerInitialDelayMs = 15_000L;

    private long schedulerFixedDelayMs = 12_000L;

    private int maxResolvePerTick = 8;

    /** POST /api/soop-live/status 한 번에 허용할 최대 링크 수 */
    private int maxBatchSize = 25;
}
