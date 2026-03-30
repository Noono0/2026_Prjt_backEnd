package com.noonoo.prjtbackend.sitePopup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SitePopupDto {
    private Long sitePopupSeq;
    private String title;
    private String content;
    private String showYn;
    /** 공개 API 응답에서 항상 Y (클라이언트 팝업 일정 보조 필터용) */
    private String popupYn;
    private String popupType;
    private Integer popupWidth;
    private Integer popupHeight;
    private Integer popupPosX;
    private Integer popupPosY;
    private String popupStartDt;
    private String popupEndDt;
    private Integer sortOrder;
    private String createDt;
    private String modifyDt;
}
