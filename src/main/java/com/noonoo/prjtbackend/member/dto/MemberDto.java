package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class MemberDto {
    private Long memberSeq;
    private String memberId;
    private String memberPwd;
    private String memberName;
    /** 화면 표시용 닉네임(게시자 이름 등) */
    private String nickname;
    private String birthYmd;
    private String email;
    /** 조회용 URL — 파일 연동 시 `/api/files/view/{seq}`, 레거시는 기존 컬럼 값 */
    private String profileImageUrl;
    /** attach_file.file_seq */
    private Long profileImageFileSeq;
    private String gender;
    private String phone;
    private String region;
    /** 공통코드 MEMBER_GRADE */
    private String gradeCode;
    private String gradeName;
    /** 공통코드 MEMBER_STATUS */
    private String statusCode;
    private String statusName;
    /** 시스템 권한(ROLE) 코드 목록 — MEMBER_ROLE */
    private List<String> roleCodes = new ArrayList<>();
    /** 목록/표시용 (콤마 구분 등) */
    private String roleCodesDisplay;
    private String createDt;
    private String modifyDt;
    private String lastLoginAt;
    /** 스트리머·컴퍼니 확장 프로필 (없으면 null) */
    private MemberStreamerProfileDto streamerProfile;
}
