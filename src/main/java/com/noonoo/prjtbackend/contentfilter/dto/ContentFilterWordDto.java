package com.noonoo.prjtbackend.contentfilter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentFilterWordDto {
    private Long contentFilterWordSeq;
    /** PROFANITY | AD */
    private String category;
    private String keyword;
    private String useYn;
    private Integer sortOrder;
    private String remark;
    private String createDt;
    private String modifyDt;
}
