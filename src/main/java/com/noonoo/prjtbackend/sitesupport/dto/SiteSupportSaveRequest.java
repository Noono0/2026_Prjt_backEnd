package com.noonoo.prjtbackend.sitesupport.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SiteSupportSaveRequest {
    private Long supportSeq;
    private String categoryCode;
    private String title;
    private String content;
    private String linkUrl;
    private Integer sortOrder;
    private String showYn;
    private String createId;
    private String createIp;
    private String modifyId;
    private String modifyIp;
}
