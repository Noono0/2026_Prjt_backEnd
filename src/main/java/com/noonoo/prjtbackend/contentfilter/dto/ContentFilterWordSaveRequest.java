package com.noonoo.prjtbackend.contentfilter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentFilterWordSaveRequest {
    private Long contentFilterWordSeq;
    /** PROFANITY | AD */
    private String category;
    private String keyword;
    private String useYn;
    private Integer sortOrder;
    private String remark;
}
