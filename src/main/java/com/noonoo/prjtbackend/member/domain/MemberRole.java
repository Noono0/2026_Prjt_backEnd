package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원별 시스템 권한(ROLE) 다중 부여.
 * ROLE_CODE는 공통코드 그룹 MEMBER_ROLE 및 ROLE 테이블과 정합성 유지.
 */
@Getter
@Setter
@Entity
@Table(
        name = "MEMBER_ROLE",
        uniqueConstraints = @UniqueConstraint(name = "UK_MEMBER_ROLE", columnNames = {"MEMBER_SEQ", "ROLE_CODE"})
)
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ROLE_SEQ")
    private Long memberRoleSeq;

    @Column(name = "MEMBER_SEQ", nullable = false)
    private Long memberSeq;

    @Column(name = "ROLE_CODE", nullable = false, length = 50)
    private String roleCode;

    @Column(name = "CRT_DT")
    private LocalDateTime crtDt;

    @Column(name = "CRT_ID", length = 50)
    private String crtId;

    @Column(name = "CRT_IP", length = 45)
    private String crtIp;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.crtDt == null) {
            this.crtDt = now;
        }
    }
}
