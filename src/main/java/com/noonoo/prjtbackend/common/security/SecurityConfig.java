package com.noonoo.prjtbackend.common.security;

import com.noonoo.prjtbackend.common.ratelimit.IpRateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <ul>
 *     <li>{@code app.security.permit-all=true} (기본): 필터 체인 전체 허용 + {@link SecurityExpressions}로 메서드 권한도 통과 (로컬 개발)</li>
 *     <li>{@code false}: 로그인({@code /api/auth/login}) 후 세션 + DB 권한({@code MEMBER_READ} 등) 검사</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;
    private final IpRateLimitFilter ipRateLimitFilter;

    @Value("${app.security.permit-all:true}")
    private boolean permitAll;

    /** 쉼표로 여러 값 — 예: {@code http://13.124.250.113:3001,https://admin.example.com} */
    @Value("${app.cors.extra-allowed-origins:}")
    private String extraAllowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .addFilterBefore(ipRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler)
                );

        if (permitAll) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/menus/sidebar").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/site-popups/active").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/site-popups/public/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/files/view/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()
                    .requestMatchers("/api/analytics/**").permitAll()
                    // Actuator health(및 하위 프로브) — 경로 문자열보다 EndpointRequest 가 서블릿 매칭과 맞는 경우가 많음
                    .requestMatchers(EndpointRequest.to("health")).permitAll()
                    .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                    .requestMatchers(HttpMethod.HEAD, "/actuator/health", "/actuator/health/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    // 로컬·스테이징: OpenAPI JSON·Swagger UI·Scalar(/scalar/**). 운영 prod에서는 springdoc 비활성.
                    .requestMatchers(
                            "/v3/api-docs",
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/scalar/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            );
        }

        http
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        var patterns = new ArrayList<>(List.of(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001",
                "https://gamcompany.kr",
                "https://www.gamcompany.kr",
                "https://admin.gamcompany.kr"
        ));
        if (extraAllowedOrigins != null && !extraAllowedOrigins.isBlank()) {
            Arrays.stream(extraAllowedOrigins.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(patterns::add);
        }
        configuration.setAllowedOriginPatterns(patterns);
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
