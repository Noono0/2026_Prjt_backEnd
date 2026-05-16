package com.noonoo.prjtbackend.gamniverseprofile.service;

import com.noonoo.prjtbackend.gamniverseprofile.config.SoopLiveStatusProperties;
import com.noonoo.prjtbackend.gamniverseprofile.dto.SoopLiveStatusBatchResponse;
import com.noonoo.prjtbackend.gamniverseprofile.dto.SoopLiveStatusItemDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SoopLiveStatusService {

    private final SoopLiveStatusResolver soopLiveStatusResolver;
    private final SoopLiveStatusProperties soopLiveStatusProperties;

    public SoopLiveStatusBatchResponse resolveBatch(List<String> links) {
        if (!soopLiveStatusProperties.isApiEnabled()) {
            return new SoopLiveStatusBatchResponse(new LinkedHashMap<>());
        }
        int max = Math.max(1, soopLiveStatusProperties.getMaxBatchSize());
        Map<String, SoopLiveStatusItemDto> statuses = new LinkedHashMap<>();
        if (links == null || links.isEmpty()) {
            return new SoopLiveStatusBatchResponse(statuses);
        }
        int count = 0;
        for (String link : links) {
            if (!StringUtils.hasText(link)) {
                continue;
            }
            if (count >= max) {
                break;
            }
            String normalized = SoopLiveStatusResolver.normalizeLink(link);
            if (statuses.containsKey(normalized)) {
                continue;
            }
            SoopLiveStatusResolver.LiveStatus status = soopLiveStatusResolver.resolve(normalized);
            statuses.put(
                    normalized,
                    new SoopLiveStatusItemDto(status.isLive(), status.liveRoomId()));
            count++;
        }
        return new SoopLiveStatusBatchResponse(statuses);
    }
}
