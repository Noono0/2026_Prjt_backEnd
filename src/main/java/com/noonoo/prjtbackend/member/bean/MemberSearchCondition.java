package com.noonoo.prjtbackend.member.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchCondition {
    private String loginId;
    private String memberName;
    private String roleCode;
    private String member_pwd;
}
