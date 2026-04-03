package com.noonoo.prjtbackend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetVerifyDto {
    private String memberId;
    private String email;
    /** 6자리 숫자 */
    private String code;
}
