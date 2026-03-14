package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.noonoo.prjtbackend.role.domain.Role;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

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
    @NotBlank(message = "아이디는 필수입니다.")
    private String memberId;

    @Column(name = "member_pwd")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String memberPwd;

    @Column(name = "birth_ymd")
    private String birthYmd;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "member_name")
    @NotBlank(message = "성명은 필수입니다.")
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
    @NotBlank(message = "권한은 필수입니다.")
    private String roleCode;
}
