package com.noonoo.prjtbackend.auth.controller;

import com.noonoo.prjtbackend.auth.dto.LoginRequest;
import com.noonoo.prjtbackend.auth.dto.TokenResponse;
import com.noonoo.prjtbackend.auth.service.AuthService;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}
