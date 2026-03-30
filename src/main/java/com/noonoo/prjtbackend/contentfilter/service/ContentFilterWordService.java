package com.noonoo.prjtbackend.contentfilter.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordDto;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSaveRequest;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSearchCondition;

public interface ContentFilterWordService {

    PageResponse<ContentFilterWordDto> search(ContentFilterWordSearchCondition condition);

    ContentFilterWordDto detail(Long seq);

    int create(ContentFilterWordSaveRequest request);

    int update(ContentFilterWordSaveRequest request);

    int delete(Long seq);
}
