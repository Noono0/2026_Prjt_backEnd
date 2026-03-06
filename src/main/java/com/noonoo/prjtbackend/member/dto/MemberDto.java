package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    private Long memberId;
    private String loginId;
    private String memberName;
    private String email;
    private String roleCode;
}
