package com.noonoo.prjtbackend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetCompleteDto {
    private String resetToken;
    private String newPassword;
}
