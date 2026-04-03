package com.noonoo.prjtbackend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
