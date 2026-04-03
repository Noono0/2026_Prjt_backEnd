package com.noonoo.prjtbackend.analytics.controller;

import com.noonoo.prjtbackend.analytics.dto.VisitorHeartbeatRequest;
import com.noonoo.prjtbackend.analytics.dto.VisitorOverviewDto;
import com.noonoo.prjtbackend.analytics.service.VisitorAnalyticsService;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class VisitorAnalyticsController {

    private final VisitorAnalyticsService visitorAnalyticsService;

    @PostMapping("/heartbeat")
    public ApiResponse<Void> heartbeat(@RequestBody VisitorHeartbeatRequest request, HttpServletRequest httpRequest) {
        String userAgent = httpRequest.getHeader("User-Agent");
        visitorAnalyticsService.heartbeat(request == null ? null : request.getVisitorKey(), userAgent);
        return ApiResponse.ok("heartbeat 저장 완료", null);
    }

    @GetMapping("/overview")
    public ApiResponse<VisitorOverviewDto> overview(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "12") int weeks,
            @RequestParam(defaultValue = "12") int months
    ) {
        return ApiResponse.ok(visitorAnalyticsService.overview(days, weeks, months));
    }
}
