package com.noonoo.prjtbackend.gamniverseprofile.service;

import com.noonoo.prjtbackend.gamniverseprofile.config.SoopLiveStatusProperties;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileDto;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSearchCondition;
import com.noonoo.prjtbackend.gamniverseprofile.mapper.GamniverseProfileMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class SoopLiveStatusScheduler {

    private final GamniverseProfileMapper gamniverseProfileMapper;
    private final SoopLiveStatusResolver soopLiveStatusResolver;
    private final SoopLiveStatusProperties soopLiveStatusProperties;

    /**
     * 캐시 만료분만 {@link SoopLiveStatusProperties#getMaxResolvePerTick()}건까지 Playwright로 갱신합니다.
     * 주기·TTL은 application.yml 의 {@code app.soop-live.*} 로 조정하세요.
     */
    @Scheduled(
            initialDelayString = "${app.soop-live.scheduler-initial-delay-ms:15000}",
            fixedDelayString = "${app.soop-live.scheduler-fixed-delay-ms:12000}")
    public void refreshSoopLiveStatusCache() {
        if (!soopLiveStatusProperties.isSchedulerEnabled()) {
            return;
        }
        try {
            GamniverseProfileSearchCondition condition = new GamniverseProfileSearchCondition();
            condition.setPage(1);
            condition.setSize(500);
            List<GamniverseProfileDto> profiles = gamniverseProfileMapper.selectList(condition);
            if (profiles == null || profiles.isEmpty()) {
                log.info("[LIVE-SCHEDULER] no profile to check");
                return;
            }

            int withLink = 0;
            int refreshed = 0;
            int budget = Math.max(1, soopLiveStatusProperties.getMaxResolvePerTick());
            for (GamniverseProfileDto profile : profiles) {
                if (!StringUtils.hasText(profile.getSoopBroadcastLink())) {
                    continue;
                }
                withLink++;
                if (budget <= 0) {
                    continue;
                }
                if (soopLiveStatusResolver.isCacheFresh(profile.getSoopBroadcastLink())) {
                    continue;
                }
                soopLiveStatusResolver.resolve(profile.getSoopBroadcastLink());
                refreshed++;
                budget--;
            }
            log.info(
                    "[LIVE-SCHEDULER] playwrightRefreshed={} soopLinks={} profiles={}",
                    refreshed,
                    withLink,
                    profiles.size());
        } catch (Exception e) {
            log.warn("[LIVE-SCHEDULER] refresh failed: {}", e.toString());
        }
    }
}
