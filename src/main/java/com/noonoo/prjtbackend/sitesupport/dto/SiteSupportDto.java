package com.noonoo.prjtbackend.sitesupport.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SiteSupportDto {
    private Long supportSeq;
    /** AD, SPONSOR, HELPER */
    private String categoryCode;
    private String title;
    private String content;
    private String linkUrl;
    private Integer sortOrder;
    private String showYn;
    private String createDt;
    private String modifyDt;
    private String createId;
    private String modifyId;
    private String createIp;
    private String modifyIp;
}
