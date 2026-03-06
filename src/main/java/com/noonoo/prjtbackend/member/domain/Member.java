package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "admin_member")
public class Member {
    @Id
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "email")
    private String email;

    @Column(name = "role_code")
    private String roleCode;
}
