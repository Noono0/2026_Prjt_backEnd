package com.noonoo.prjtbackend.common.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.util.IpUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class IpRateLimitFilter extends OncePerRequestFilter {

    private final IpRateLimitService ipRateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!ipRateLimitService.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        RateLimitRule rule = resolveRule(request);
        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String ip = IpUtil.getClientIp(request);
        var probe = ipRateLimitService.tryConsumeAndProbe(rule, ip);
        if (!probe.isConsumed()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            long retrySec = Math.max(1L, probe.getNanosToWaitForRefill() / 1_000_000_000L);
            response.setHeader("Retry-After", String.valueOf(retrySec));
            String body = objectMapper.writeValueAsString(
                    ApiResponse.fail("RATE_LIMITED", "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요.")
            );
            response.getWriter().write(body);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private RateLimitRule resolveRule(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        String uri = request.getRequestURI();
        return switch (uri) {
            case "/api/auth/login" -> RateLimitRule.LOGIN;
            case "/api/auth/password-reset/request" -> RateLimitRule.PASSWORD_RESET_REQUEST;
            case "/api/auth/password-reset/verify" -> RateLimitRule.PASSWORD_RESET_VERIFY;
            case "/api/auth/password-reset/complete" -> RateLimitRule.PASSWORD_RESET_COMPLETE;
            case "/api/auth/oauth/sync" -> RateLimitRule.OAUTH_SYNC;
            case "/api/auth/oauth/establish-session" -> RateLimitRule.OAUTH_ESTABLISH;
            case "/api/analytics/heartbeat" -> RateLimitRule.HEARTBEAT;
            default -> null;
        };
    }
}
