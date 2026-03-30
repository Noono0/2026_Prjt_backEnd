package com.noonoo.prjtbackend.sitePopup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SitePopupSaveRequest {
    private Long sitePopupSeq;
    private String title;
    private String content;
    private String showYn;
    private String popupType;
    private Integer popupWidth;
    private Integer popupHeight;
    private Integer popupPosX;
    private Integer popupPosY;
    private String popupStartDt;
    private String popupEndDt;
    private Integer sortOrder;
    private String createId;
    private String createIp;
    private String modifyId;
    private String modifyIp;
}
