package com.noonoo.prjtbackend.auth.serviceImpl;

import com.noonoo.prjtbackend.auth.dto.LoginRequest;
import com.noonoo.prjtbackend.auth.dto.TokenResponse;
import com.noonoo.prjtbackend.auth.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public TokenResponse login(LoginRequest request) {
        return new TokenResponse("sample-access-token", "sample-refresh-token");
    }
}
