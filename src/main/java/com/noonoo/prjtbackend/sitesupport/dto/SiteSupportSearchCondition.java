package com.noonoo.prjtbackend.sitesupport.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SiteSupportSearchCondition extends PageRequest {
    private String keyword;
    private String categoryCode;
    private String showYn;

    public SiteSupportSearchCondition() {
        setSortBy("supportSeq");
        setSortDir("desc");
    }
}
