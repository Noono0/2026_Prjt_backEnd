package com.noonoo.prjtbackend.contentfilter.serviceImpl;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordDto;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSaveRequest;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSearchCondition;
import com.noonoo.prjtbackend.contentfilter.mapper.ContentFilterWordMapper;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContentFilterWordServiceImpl implements ContentFilterWordService {

    private static final Set<String> CATEGORIES = Set.of("PROFANITY", "AD");

    private final ContentFilterWordMapper contentFilterWordMapper;
    private final ContentFilterApplyService contentFilterApplyService;

    @Override
    public PageResponse<ContentFilterWordDto> search(ContentFilterWordSearchCondition condition) {
        long total = contentFilterWordMapper.selectListCnt(condition);
        List<ContentFilterWordDto> items = contentFilterWordMapper.selectList(condition);
        return PagingUtils.toPageResponse(condition, total, items);
    }

    @Override
    public ContentFilterWordDto detail(Long seq) {
        return contentFilterWordMapper.selectById(seq);
    }

    @Override
    @Transactional
    public int create(ContentFilterWordSaveRequest request) {
        validate(request, null);
        if (!StringUtils.hasText(request.getUseYn())) {
            request.setUseYn("Y");
        }
        if (request.getSortOrder() == null) {
            request.setSortOrder(0);
        }
        int n = contentFilterWordMapper.insertWord(request);
        if (n > 0) {
            contentFilterApplyService.refreshCache();
        }
        return n;
    }

    @Override
    @Transactional
    public int update(ContentFilterWordSaveRequest request) {
        if (request.getContentFilterWordSeq() == null) {
            throw new IllegalArgumentException("일련번호가 필요합니다.");
        }
        validate(request, request.getContentFilterWordSeq());
        int n = contentFilterWordMapper.updateWord(request);
        if (n > 0) {
            contentFilterApplyService.refreshCache();
        }
        return n;
    }

    @Override
    @Transactional
    public int delete(Long seq) {
        int n = contentFilterWordMapper.softDelete(seq);
        if (n > 0) {
            contentFilterApplyService.refreshCache();
        }
        return n;
    }

    private void validate(ContentFilterWordSaveRequest request, Long excludeSeq) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 없습니다.");
        }
        String cat = request.getCategory();
        if (!StringUtils.hasText(cat) || !CATEGORIES.contains(cat.trim().toUpperCase(Locale.ROOT))) {
            throw new IllegalArgumentException("구분은 PROFANITY(비속어) 또는 AD(광고)만 가능합니다.");
        }
        request.setCategory(cat.trim().toUpperCase(Locale.ROOT));
        String kw = request.getKeyword();
        if (!StringUtils.hasText(kw) || kw.trim().length() > 200) {
            throw new IllegalArgumentException("키워드는 1~200자로 입력해주세요.");
        }
        request.setKeyword(kw.trim());
        if (contentFilterWordMapper.countByCategoryAndKeywordIgnoreCase(
                request.getCategory(), request.getKeyword(), excludeSeq) > 0) {
            throw new IllegalArgumentException("같은 구분에 동일한 키워드가 이미 있습니다.");
        }
    }
}
