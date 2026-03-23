package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class MemberSaveRequest {
    private Long memberSeq;
    private String birthYmd;
    private String createDt;
    private String createId;
    private String createIp;
    private String email;
    private String gender;
    private String memberId;
    private String memberName;
    private String memberPwd;
    private String modifyDt;
    private String modifyId;
    private String modifyIp;
    private String phone;
    private String gradeCode;
    private String statusCode;
    /** 저장 시 MEMBER_ROLE 전체 교체 또는 신규 시 부여할 코드 목록 */
    private List<String> roleCodes = new ArrayList<>();
}
