package com.noonoo.prjtbackend.role.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 권한 그룹 테이블
 * 예: ADMIN, CS, MD
 */
@Entity
@Table(name = "ROLE")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    private Long roleId;

    /**
     * 권한 코드
     * 예: ADMIN, CS
     */
    @Column(name = "ROLE_CODE", nullable = false, unique = true, length = 50)
    private String roleCode;

    /**
     * 권한명
     * 예: 관리자, 고객센터
     */
    @Column(name = "ROLE_NAME", nullable = false, length = 100)
    private String roleName;

    /**
     * 사용 여부
     * Y / N
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
    }

    @PreUpdate
    public void onUpdate() {
        this.updDt = LocalDateTime.now();
    }
}