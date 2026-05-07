package com.noonoo.prjtbackend.analytics.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorOverviewDto {
    private long onlineCount;
    private int heartbeatTtlSeconds;
    private List<VisitorCountPointDto> daily;
    private List<VisitorCountPointDto> weekly;
    private List<VisitorCountPointDto> monthly;
}
