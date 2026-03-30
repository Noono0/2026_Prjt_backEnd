package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * 회원 기본 정보. 시스템 권한(ROLE)은 {@link MemberRole} 참고.
 * 등급/상태는 공통코드 MEMBER_GRADE, MEMBER_STATUS 로 관리.
 */
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

    /** 게시·댓글·선물 등에 노출되는 닉네임(없으면 회원명으로 대체 표시) */
    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_image_url", columnDefinition = "LONGTEXT")
    private String profileImageUrl;

    /** {@code attach_file.file_seq} — 프로필 이미지는 첨부 메타 + 디스크 파일로 관리 */
    @Column(name = "profile_image_file_seq")
    private Long profileImageFileSeq;

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

    /** 공통코드 MEMBER_GRADE (비즈니스 등급, Security 아님) */
    @Column(name = "grade_code", length = 50)
    private String gradeCode = "NORMAL";

    /** 공통코드 MEMBER_STATUS (계정 상태) */
    @Column(name = "status_code", length = 50)
    private String statusCode = "ACTIVE";
}
