package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.member.dto.PointPolicyRowDto;
import com.noonoo.prjtbackend.member.mapper.PointPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * point_policy_setting 조회 (지급 로직용). 비활성·NULL 시 기본값(fallback) 사용.
 */
@Service
@RequiredArgsConstructor
public class PointPolicyResolver {

    private final PointPolicyMapper pointPolicyMapper;

    public PointPolicyRowDto row(String policyKey) {
        return pointPolicyMapper.selectByPolicyKey(policyKey);
    }

    /** 행이 없으면 활성으로 간주(폴백 적용). */
    public boolean isEnabled(String policyKey) {
        PointPolicyRowDto r = pointPolicyMapper.selectByPolicyKey(policyKey);
        if (r == null) {
            return true;
        }
        return "Y".equalsIgnoreCase(r.getUseYn());
    }

    /**
     * 행이 없으면 코드 기본값(fallback)을 사용합니다. 행이 있으나 비활성이면 0입니다.
     */
    public long reward(String policyKey, long fallback) {
        PointPolicyRowDto r = pointPolicyMapper.selectByPolicyKey(policyKey);
        if (r == null) {
            return fallback;
        }
        if (!"Y".equalsIgnoreCase(r.getUseYn())) {
            return 0L;
        }
        if (r.getRewardPoints() == null) {
            return fallback;
        }
        return r.getRewardPoints();
    }

    public int cap(String policyKey, int fallback) {
        PointPolicyRowDto r = pointPolicyMapper.selectByPolicyKey(policyKey);
        if (r == null) {
            return fallback;
        }
        if (!"Y".equalsIgnoreCase(r.getUseYn())) {
            return 0;
        }
        if (r.getCapInt() == null) {
            return fallback;
        }
        return r.getCapInt();
    }

    public int threshold(String policyKey, int fallback) {
        PointPolicyRowDto r = pointPolicyMapper.selectByPolicyKey(policyKey);
        if (r == null) {
            return fallback;
        }
        if (!"Y".equalsIgnoreCase(r.getUseYn())) {
            return 0;
        }
        if (r.getThresholdInt() == null) {
            return fallback;
        }
        return r.getThresholdInt();
    }
}
