package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberSaveRequest {
    private Long memberSeq;     // PK
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
    private String roleCode;
    private String status;
}
