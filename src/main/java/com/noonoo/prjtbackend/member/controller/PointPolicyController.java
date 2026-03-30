package com.noonoo.prjtbackend.member.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.member.dto.PointPolicyRowDto;
import com.noonoo.prjtbackend.member.service.PointPolicyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/point-policy")
@RequiredArgsConstructor
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @GetMapping
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.POINT_POLICY + "')")
    public ApiResponse<List<PointPolicyRowDto>> list() {
        return ApiResponse.ok(pointPolicyService.listAllPolicies());
    }

    @PutMapping
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.POINT_POLICY + "')")
    public ApiResponse<String> save(@RequestBody List<PointPolicyRowDto> rows) {
        pointPolicyService.saveAllPolicies(rows);
        return ApiResponse.ok("저장되었습니다.", "OK");
    }
}
