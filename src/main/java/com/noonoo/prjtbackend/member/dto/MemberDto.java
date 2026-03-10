package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {
    private Long memberId;
    private String loginId;
    private String memberPwd;
    private String memberName;
    private String email;
    private String roleCode;

}
