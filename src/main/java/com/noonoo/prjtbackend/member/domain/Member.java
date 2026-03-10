package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.noonoo.prjtbackend.role.domain.Role;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@ToString
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_seq")
    private Long memberSeq;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "member_pwd")
    private String memberPwd;

    @Column(name = "birth_ymd")
    private String birthYmd;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "email")
    private String email;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_id")
    private String createId;

    @Column(name = "create_ip")
    private String createIp;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_id")
    private String modifyId;

    @Column(name = "modify_ip")
    private String modifyIp;

    @Column(name = "status")
    private String status;

    @Column(name = "role_code")
    private String roleCode;
}
