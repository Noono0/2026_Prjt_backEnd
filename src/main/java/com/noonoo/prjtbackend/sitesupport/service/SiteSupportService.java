package com.noonoo.prjtbackend.sitesupport.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportDto;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSaveRequest;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSearchCondition;
import java.util.List;

public interface SiteSupportService {

    PageResponse<SiteSupportDto> search(SiteSupportSearchCondition condition);

    SiteSupportDto detail(Long supportSeq);

    List<SiteSupportDto> findActiveForSite();

    int create(SiteSupportSaveRequest request);

    int update(SiteSupportSaveRequest request);

    int delete(Long supportSeq);
}
