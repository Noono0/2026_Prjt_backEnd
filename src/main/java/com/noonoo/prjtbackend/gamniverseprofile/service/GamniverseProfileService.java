package com.noonoo.prjtbackend.gamniverseprofile.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileDto;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSaveRequest;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSearchCondition;

public interface GamniverseProfileService {
    PageResponse<GamniverseProfileDto> search(GamniverseProfileSearchCondition condition);

    GamniverseProfileDto detail(Long seq);

    int create(GamniverseProfileSaveRequest request);

    int update(GamniverseProfileSaveRequest request);

    int delete(Long seq);
}
