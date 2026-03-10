package com.noonoo.prjtbackend.menu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 메뉴 테이블
 * 예: MEMBER, ORDER, PRODUCT
 */
@Entity
@Table(name = "MENU")
@Getter
@Setter
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_ID")
    private Long menuId;

    /**
     * 메뉴 코드
     * 권한 문자열 생성 시 기준이 됨
     * 예: MEMBER, ORDER
     */
    @Column(name = "MENU_CODE", nullable = false, unique = true, length = 50)
    private String menuCode;

    /**
     * 메뉴명
     */
    @Column(name = "MENU_NAME", nullable = false, length = 100)
    private String menuName;

    /**
     * 프론트 경로
     * 예: /members, /orders
     */
    @Column(name = "MENU_PATH", length = 200)
    private String menuPath;

    /**
     * 상위 메뉴 ID
     * 대메뉴/소메뉴 구조 필요 시 사용
     */
    @Column(name = "PARENT_MENU_ID")
    private Long parentMenuId;

    /**
     * 메뉴 정렬 순서
     */
    @Column(name = "SORT_ORDER")
    private Integer sortOrder = 0;

    /**
     * 사용 여부
     */
    @Column(name = "USE_YN", nullable = false, length = 1)
    private String useYn = "Y";

    @Column(name = "CRT_DT")
    private LocalDateTime crtDt;

    @Column(name = "UPD_DT")
    private LocalDateTime updDt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.crtDt = now;
        this.updDt = now;
        if (this.useYn == null) {
            this.useYn = "Y";
        }
        if (this.sortOrder == null) {
            this.sortOrder = 0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updDt = LocalDateTime.now();
    }
}
