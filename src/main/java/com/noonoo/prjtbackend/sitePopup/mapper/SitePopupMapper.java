package com.noonoo.prjtbackend.sitePopup.mapper;

import com.noonoo.prjtbackend.sitePopup.dto.SitePopupDto;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSaveRequest;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SitePopupMapper {

    List<SitePopupDto> findSitePopups(SitePopupSearchCondition condition);

    long findSitePopupsCnt(SitePopupSearchCondition condition);

    SitePopupDto findSitePopupById(Long sitePopupSeq);

    List<SitePopupDto> findSitePopupsForSiteLoad();

    SitePopupDto findSitePopupPublic(Long sitePopupSeq);

    int insertSitePopup(SitePopupSaveRequest request);

    int updateSitePopup(SitePopupSaveRequest request);

    int deleteSitePopup(Long sitePopupSeq);
}
