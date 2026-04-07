package com.noonoo.prjtbackend.role.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.noonoo.prjtbackend.menu.domain.Menu;

import java.time.LocalDateTime;

/**
 * 권한별 메뉴 CRUD 권한 매핑 테이블
 */
@Entity
@Table(
        name = "role_menu",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_ROLE_MENU", columnNames = {"ROLE_ID", "MENU_ID"})
        }
)
@Getter
@Setter
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_MENU_ID")
    private Long roleMenuId;

    /**
     * 어떤 권한(Role)에 대한 설정인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    /**
     * 어떤 메뉴(Menu)에 대한 설정인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID", nullable = false)
    private Menu menu;

    /**
     * 조회 권한
     */
    @Column(name = "CAN_READ", nullable = false, length = 1)
    private String canRead = "N";

    /**
     * 등록 권한
     */
    @Column(name = "CAN_CREATE", nullable = false, length = 1)
    private String canCreate = "N";

    /**
     * 수정 권한
     */
    @Column(name = "CAN_UPDATE", nullable = false, length = 1)
    private String canUpdate = "N";

    /**
     * 삭제 권한
     */
    @Column(name = "CAN_DELETE", nullable = false, length = 1)
    private String canDelete = "N";

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

        if (this.canRead == null) this.canRead = "N";
        if (this.canCreate == null) this.canCreate = "N";
        if (this.canUpdate == null) this.canUpdate = "N";
        if (this.canDelete == null) this.canDelete = "N";
        if (this.useYn == null) this.useYn = "Y";
    }

    @PreUpdate
    public void onUpdate() {
        this.updDt = LocalDateTime.now();
    }
}
