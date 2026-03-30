package com.noonoo.prjtbackend.member.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.member.dto.PointRankingEntryDto;
import com.noonoo.prjtbackend.member.service.PointRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members/point-ranking")
@RequiredArgsConstructor
public class PointRankingController {

    private final PointRankingService pointRankingService;

    @GetMapping
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<List<PointRankingEntryDto>> ranking(
            @RequestParam(defaultValue = "DAY") String period
    ) {
        return ApiResponse.ok(pointRankingService.ranking(period));
    }
}
