package com.noonoo.prjtbackend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDto {
    private String memberId;
    private String email;
}
