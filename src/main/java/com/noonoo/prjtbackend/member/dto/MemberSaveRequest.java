package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSaveRequest {
    private String loginId;
    private String name;
    private String birthYmd;
    private String gender;
    private String phone;
    private String email;
    private String status;
    private Long roleId;
}
