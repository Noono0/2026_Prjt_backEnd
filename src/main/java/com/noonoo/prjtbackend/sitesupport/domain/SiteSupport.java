package com.noonoo.prjtbackend.sitesupport.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 사용자 서포트(광고·협찬·도움) 카드 — 스키마는 JPA ddl-auto 로 관리 */
@Getter
@Setter
@Entity
@ToString
@Table(name = "site_support")
public class SiteSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "support_seq")
    private Long supportSeq;

    /** AD, SPONSOR, HELPER */
    @Column(name = "category_code", length = 20, nullable = false)
    private String categoryCode;

    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "link_url", length = 2048)
    private String linkUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "show_yn", length = 1, nullable = false)
    private String showYn;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

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
