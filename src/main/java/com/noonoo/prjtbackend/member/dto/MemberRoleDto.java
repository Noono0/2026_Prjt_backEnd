package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRoleDto {
    private Long memberRoleSeq;
    private Long memberSeq;
    private String roleCode;
    private String roleName;
}
