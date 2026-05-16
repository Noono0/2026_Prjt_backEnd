package com.noonoo.prjtbackend.gamniverseprofile.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.gamniverseprofile.dto.SoopLiveStatusBatchRequest;
import com.noonoo.prjtbackend.gamniverseprofile.dto.SoopLiveStatusBatchResponse;
import com.noonoo.prjtbackend.gamniverseprofile.service.SoopLiveStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/soop-live")
@RequiredArgsConstructor
public class SoopLiveStatusController {

    private final SoopLiveStatusService soopLiveStatusService;

    /**
     * 대시보드 등에서 보이는 숲 링크만 묶어 조회합니다. 캐시 TTL({@code app.soop-live.cache-ttl-ms}) 동안
     * 재사용되며, 요청이 없으면 백그라운드 조회는 없습니다.
     */
    @PostMapping("/status")
    public ApiResponse<SoopLiveStatusBatchResponse> status(@RequestBody SoopLiveStatusBatchRequest request) {
        return ApiResponse.ok("LIVE 상태 조회 완료", soopLiveStatusService.resolveBatch(request.getLinks()));
    }
}
