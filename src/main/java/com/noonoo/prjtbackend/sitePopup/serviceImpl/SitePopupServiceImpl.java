package com.noonoo.prjtbackend.sitePopup.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupDto;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSaveRequest;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSearchCondition;
import com.noonoo.prjtbackend.sitePopup.mapper.SitePopupMapper;
import com.noonoo.prjtbackend.sitePopup.service.SitePopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SitePopupServiceImpl implements SitePopupService {

    private final SitePopupMapper sitePopupMapper;
    private final ContentFilterApplyService contentFilterApplyService;

    @Override
    public PageResponse<SitePopupDto> findSitePopups(SitePopupSearchCondition condition) {
        long total = sitePopupMapper.findSitePopupsCnt(condition);
        List<SitePopupDto> items = sitePopupMapper.findSitePopups(condition);
        return PagingUtils.toPageResponse(condition, total, items);
    }

    @Override
    public SitePopupDto findSitePopupDetail(Long sitePopupSeq) {
        return sitePopupMapper.findSitePopupById(sitePopupSeq);
    }

    @Override
    public List<SitePopupDto> findSitePopupsForSiteLoad() {
        return Collections.unmodifiableList(sitePopupMapper.findSitePopupsForSiteLoad());
    }

    @Override
    public SitePopupDto findSitePopupPublic(Long sitePopupSeq) {
        return sitePopupMapper.findSitePopupPublic(sitePopupSeq);
    }

    @Override
    @Transactional
    public int createSitePopup(SitePopupSaveRequest request) {
        String loginMemberId = maskLoginId();
        String clientIp = RequestContext.getClientIp();

        request.setCreateId(loginMemberId);
        request.setModifyId(loginMemberId);
        request.setCreateIp(clientIp);
        request.setModifyIp(clientIp);

        if (!StringUtils.hasText(request.getShowYn())) {
            request.setShowYn("Y");
        } else {
            String s = request.getShowYn().trim().toUpperCase(Locale.ROOT);
            request.setShowYn("Y".equals(s) ? "Y" : "N");
        }

        request.setTitle(contentFilterApplyService.applyField("제목", request.getTitle()));
        request.setContent(contentFilterApplyService.applyField("내용", request.getContent()));
        normalizePopupFields(request);

        return sitePopupMapper.insertSitePopup(request);
    }

    @Override
    @Transactional
    public int updateSitePopup(SitePopupSaveRequest request) {
        request.setModifyId(maskLoginId());
        request.setModifyIp(RequestContext.getClientIp());

        if (StringUtils.hasText(request.getShowYn())) {
            String s = request.getShowYn().trim().toUpperCase(Locale.ROOT);
            request.setShowYn("Y".equals(s) ? "Y" : "N");
        }

        request.setTitle(contentFilterApplyService.applyField("제목", request.getTitle()));
        request.setContent(contentFilterApplyService.applyField("내용", request.getContent()));
        normalizePopupFields(request);

        return sitePopupMapper.updateSitePopup(request);
    }

    @Override
    @Transactional
    public int deleteSitePopup(Long sitePopupSeq) {
        return sitePopupMapper.deleteSitePopup(sitePopupSeq);
    }

    private static String maskLoginId() {
        String loginMemberId = RequestContext.getLoginMemberId();
        return StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM";
    }

    private void normalizePopupFields(SitePopupSaveRequest request) {
        if (!StringUtils.hasText(request.getPopupType())) {
            request.setPopupType("MODAL");
        } else {
            String t = request.getPopupType().trim().toUpperCase(Locale.ROOT);
            request.setPopupType("WINDOW".equals(t) ? "WINDOW" : "MODAL");
        }
        int w = request.getPopupWidth() != null ? request.getPopupWidth() : 600;
        int h = request.getPopupHeight() != null ? request.getPopupHeight() : 600;
        request.setPopupWidth(Math.min(2000, Math.max(200, w)));
        request.setPopupHeight(Math.min(2000, Math.max(200, h)));
        request.setPopupStartDt(trimPopupDateTimeToNull(request.getPopupStartDt()));
        request.setPopupEndDt(trimPopupDateTimeToNull(request.getPopupEndDt()));
        if (request.getSortOrder() == null) {
            request.setSortOrder(0);
        }
    }

    private static String trimPopupDateTimeToNull(String v) {
        if (!StringUtils.hasText(v)) {
            return null;
        }
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }
}
