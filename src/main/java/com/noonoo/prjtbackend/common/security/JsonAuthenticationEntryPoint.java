package com.noonoo.prjtbackend.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증되지 않은 요청 → JSON 401 (ApiResponse)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String cookieHeader = request.getHeader("Cookie");
        boolean hasCookieHeader = cookieHeader != null && !cookieHeader.isBlank();
        boolean hasAuthorizationHeader = request.getHeader("Authorization") != null
                && !request.getHeader("Authorization").isBlank();
        log.warn(
                "인증 필요(401 JSON): method={} uri={} query={} remoteAddr={} hasCookieHeader={} hasAuthorizationHeader={} exception={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                hasCookieHeader,
                hasAuthorizationHeader,
                authException != null ? authException.getMessage() : null
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.fail("UNAUTHORIZED", "로그인이 필요합니다.");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
