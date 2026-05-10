package com.noonoo.prjtbackend.sitesupport.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportDto;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSaveRequest;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSearchCondition;
import com.noonoo.prjtbackend.sitesupport.mapper.SiteSupportMapper;
import com.noonoo.prjtbackend.sitesupport.service.SiteSupportService;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SiteSupportServiceImpl implements SiteSupportService {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("AD", "SPONSOR", "HELPER");

    private final SiteSupportMapper siteSupportMapper;
    private final ContentFilterApplyService contentFilterApplyService;

    @Override
    public PageResponse<SiteSupportDto> search(SiteSupportSearchCondition condition) {
        long total = siteSupportMapper.searchListCnt(condition);
        List<SiteSupportDto> items = siteSupportMapper.searchList(condition);
        return PagingUtils.toPageResponse(condition, total, items);
    }

    @Override
    public SiteSupportDto detail(Long supportSeq) {
        return siteSupportMapper.findDetail(supportSeq);
    }

    @Override
    public List<SiteSupportDto> findActiveForSite() {
        return siteSupportMapper.findActiveForSite();
    }

    @Override
    @Transactional
    public int create(SiteSupportSaveRequest request) {
        String loginId = maskLoginId();
        String clientIp = RequestContext.getClientIp();
        request.setCreateId(loginId);
        request.setModifyId(loginId);
        request.setCreateIp(clientIp);
        request.setModifyIp(clientIp);

        normalizeCategory(request);
        if (!StringUtils.hasText(request.getShowYn())) {
            request.setShowYn("Y");
        } else {
            request.setShowYn(normalizeYn(request.getShowYn()));
        }
        if (request.getSortOrder() == null) {
            request.setSortOrder(0);
        }
        request.setTitle(contentFilterApplyService.applyField("제목", request.getTitle()));
        request.setContent(contentFilterApplyService.applyField("내용", request.getContent()));
        request.setLinkUrl(trimToNull(request.getLinkUrl()));

        return siteSupportMapper.insert(request);
    }

    @Override
    @Transactional
    public int update(SiteSupportSaveRequest request) {
        request.setModifyId(maskLoginId());
        request.setModifyIp(RequestContext.getClientIp());

        normalizeCategory(request);
        if (StringUtils.hasText(request.getShowYn())) {
            request.setShowYn(normalizeYn(request.getShowYn()));
        }
        if (request.getSortOrder() == null) {
            request.setSortOrder(0);
        }
        request.setTitle(contentFilterApplyService.applyField("제목", request.getTitle()));
        request.setContent(contentFilterApplyService.applyField("내용", request.getContent()));
        request.setLinkUrl(trimToNull(request.getLinkUrl()));

        return siteSupportMapper.update(request);
    }

    @Override
    @Transactional
    public int delete(Long supportSeq) {
        SiteSupportSaveRequest req = new SiteSupportSaveRequest();
        req.setSupportSeq(supportSeq);
        req.setModifyId(maskLoginId());
        req.setModifyIp(RequestContext.getClientIp());
        return siteSupportMapper.softDelete(req);
    }

    private void normalizeCategory(SiteSupportSaveRequest request) {
        if (!StringUtils.hasText(request.getCategoryCode())) {
            throw new IllegalArgumentException("분류를 선택해주세요.");
        }
        String c = request.getCategoryCode().trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_CATEGORIES.contains(c)) {
            throw new IllegalArgumentException("분류 코드가 올바르지 않습니다.");
        }
        request.setCategoryCode(c);
    }

    private static String normalizeYn(String raw) {
        String s = raw.trim().toUpperCase(Locale.ROOT);
        return "Y".equals(s) ? "Y" : "N";
    }

    private static String maskLoginId() {
        String loginMemberId = RequestContext.getLoginMemberId();
        return StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM";
    }

    private static String trimToNull(String v) {
        if (!StringUtils.hasText(v)) {
            return null;
        }
        String t = v.trim();
        if (t.length() > 2048) {
            return t.substring(0, 2048);
        }
        return t;
    }
}
