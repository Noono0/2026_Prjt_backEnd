package com.noonoo.prjtbackend.gamniverseprofile.serviceImpl;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileDto;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSaveRequest;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSearchCondition;
import com.noonoo.prjtbackend.gamniverseprofile.mapper.GamniverseProfileMapper;
import com.noonoo.prjtbackend.gamniverseprofile.service.GamniverseProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GamniverseProfileServiceImpl implements GamniverseProfileService {

    private final GamniverseProfileMapper gamniverseProfileMapper;
    @Override
    public PageResponse<GamniverseProfileDto> search(GamniverseProfileSearchCondition condition) {
        long total = gamniverseProfileMapper.selectListCnt(condition);
        List<GamniverseProfileDto> items = gamniverseProfileMapper.selectList(condition);
        return PagingUtils.toPageResponse(condition, total, items);
    }

    @Override
    public GamniverseProfileDto detail(Long seq) {
        return gamniverseProfileMapper.selectById(seq);
    }

    @Override
    @Transactional
    public int create(GamniverseProfileSaveRequest request) {
        normalize(request);
        return gamniverseProfileMapper.insertProfile(request);
    }

    @Override
    @Transactional
    public int update(GamniverseProfileSaveRequest request) {
        if (request.getGamniverseProfileSeq() == null) {
            throw new IllegalArgumentException("일련번호가 필요합니다.");
        }
        normalize(request);
        return gamniverseProfileMapper.updateProfile(request);
    }

    @Override
    @Transactional
    public int delete(Long seq) {
        return gamniverseProfileMapper.softDelete(seq);
    }

    private static void normalize(GamniverseProfileSaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 없습니다.");
        }
        if (!StringUtils.hasText(request.getProfileName())) {
            throw new IllegalArgumentException("프로필명은 필수입니다.");
        }
        request.setProfileName(request.getProfileName().trim());
        if (!StringUtils.hasText(request.getRankCode())) {
            request.setRankCode("IRON");
        } else {
            request.setRankCode(request.getRankCode().trim().toUpperCase());
        }
        request.setAffiliationCode(trimToEmpty(request.getAffiliationCode()));
        request.setBroadcastLink(trimToEmpty(request.getBroadcastLink()));
        request.setSoopBroadcastLink(trimToEmpty(request.getSoopBroadcastLink()));
        request.setInstagramUrl(trimToEmpty(request.getInstagramUrl()));
        request.setYoutubeUrl(trimToEmpty(request.getYoutubeUrl()));
        request.setCafeLink(trimToEmpty(request.getCafeLink()));
        request.setProfileRowsJson(trimToEmpty(request.getProfileRowsJson()));
        if (request.getSortOrder() == null) {
            request.setSortOrder(0);
        }
        if (!StringUtils.hasText(request.getUseYn())) {
            request.setUseYn("Y");
        }
    }

    private static String trimToEmpty(String value) {
        if (!StringUtils.hasText(value)) return "";
        return value.trim();
    }

}
