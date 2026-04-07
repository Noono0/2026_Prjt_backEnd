package com.noonoo.prjtbackend.sitePopup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "site_popup")
public class SitePopup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_popup_seq")
    private Long sitePopupSeq;

    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "show_yn", length = 1, nullable = false)
    private String showYn;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

    @Column(name = "popup_type", length = 20, nullable = false)
    private String popupType;

    @Column(name = "popup_width", nullable = false)
    private Integer popupWidth;

    @Column(name = "popup_height", nullable = false)
    private Integer popupHeight;

    @Column(name = "popup_pos_x")
    private Integer popupPosX;

    @Column(name = "popup_pos_y")
    private Integer popupPosY;

    @Column(name = "popup_start_dt")
    private LocalDateTime popupStartDt;

    @Column(name = "popup_end_dt")
    private LocalDateTime popupEndDt;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_id", length = 50)
    private String createId;

    @Column(name = "create_ip", length = 45)
    private String createIp;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_id", length = 50)
    private String modifyId;

    @Column(name = "modify_ip", length = 45)
    private String modifyIp;
}
