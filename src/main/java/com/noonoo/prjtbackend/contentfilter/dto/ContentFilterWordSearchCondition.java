package com.noonoo.prjtbackend.contentfilter.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ContentFilterWordSearchCondition extends PageRequest {
    /** 검색어(키워드 부분일치) */
    private String keyword;
    /** PROFANITY | AD | 전체 */
    private String category;

    public ContentFilterWordSearchCondition() {
        setSortBy("contentFilterWordSeq");
        setSortDir("desc");
    }
}
