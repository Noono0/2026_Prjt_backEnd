package com.noonoo.prjtbackend.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println(">>> SecurityConfig loaded");
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 로그인/권한조회는 열어둠
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/roles").permitAll()

                        // 회원 API 권한 제어
//                        .requestMatchers(HttpMethod.GET, "/api/members/**").hasAuthority("MEMBER_READ")
//                        .requestMatchers(HttpMethod.POST, "/api/members").hasAuthority("MEMBER_CREATE")
//                        .requestMatchers(HttpMethod.PUT, "/api/members/**").hasAuthority("MEMBER_UPDATE")
//                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasAuthority("MEMBER_DELETE")
                        .requestMatchers(HttpMethod.GET, "/api/members/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/members/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}