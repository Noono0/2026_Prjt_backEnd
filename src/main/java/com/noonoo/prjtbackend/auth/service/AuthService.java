package com.noonoo.prjtbackend.auth.service;

import com.noonoo.prjtbackend.auth.dto.LoginRequest;
import com.noonoo.prjtbackend.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
}
