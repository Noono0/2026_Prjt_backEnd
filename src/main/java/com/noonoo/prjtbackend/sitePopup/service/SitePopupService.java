package com.noonoo.prjtbackend.sitePopup.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupDto;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSaveRequest;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSearchCondition;

import java.util.List;

public interface SitePopupService {

    PageResponse<SitePopupDto> findSitePopups(SitePopupSearchCondition condition);

    SitePopupDto findSitePopupDetail(Long sitePopupSeq);

    List<SitePopupDto> findSitePopupsForSiteLoad();

    SitePopupDto findSitePopupPublic(Long sitePopupSeq);

    int createSitePopup(SitePopupSaveRequest request);

    int updateSitePopup(SitePopupSaveRequest request);

    int deleteSitePopup(Long sitePopupSeq);
}
