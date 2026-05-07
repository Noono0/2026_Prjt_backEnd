package com.noonoo.prjtbackend.gamniverseprofile.service;

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

    // 로컬 테스트 기준: 5초마다 숲 방송링크 상태 캐시 갱신
    @Scheduled(initialDelay = 3000L, fixedDelay = 5000L)
    public void refreshSoopLiveStatusCache() {
        try {
            GamniverseProfileSearchCondition condition = new GamniverseProfileSearchCondition();
            condition.setPage(1);
            condition.setSize(500);
            List<GamniverseProfileDto> profiles = gamniverseProfileMapper.selectList(condition);
            if (profiles == null || profiles.isEmpty()) {
                log.info("[LIVE-SCHEDULER] no profile to check");
                return;
            }

            int checked = 0;
            for (GamniverseProfileDto profile : profiles) {
                if (!StringUtils.hasText(profile.getSoopBroadcastLink())) {
                    continue;
                }
                soopLiveStatusResolver.resolve(profile.getSoopBroadcastLink());
                checked++;
            }
            log.info("[LIVE-SCHEDULER] refreshed {} / {} profiles", checked, profiles.size());
        } catch (Exception e) {
            log.warn("[LIVE-SCHEDULER] refresh failed: {}", e.toString());
        }
    }
}
