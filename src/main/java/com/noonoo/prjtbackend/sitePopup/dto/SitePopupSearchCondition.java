package com.noonoo.prjtbackend.sitePopup.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SitePopupSearchCondition extends PageRequest {
    private String title;
    private String keyword;
    private String showYn;

    public SitePopupSearchCondition() {
        setSortBy("sitePopupSeq");
        setSortDir("desc");
    }
}
