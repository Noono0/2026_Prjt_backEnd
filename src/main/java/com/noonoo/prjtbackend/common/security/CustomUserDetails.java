package com.noonoo.prjtbackend.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserDetails extends User {

    private final Long memberSeq;
    /** 공통코드 MEMBER_GRADE — Security가 아닌 비즈니스용으로 보관 */
    private final String gradeCode;
    /** 공통코드 MEMBER_STATUS */
    private final String statusCode;
    /** MEMBER_ROLE 기반 시스템 ROLE 코드 목록 */
    private final List<String> roleCodes;

    public CustomUserDetails(
            Long memberSeq,
            String username,
            String password,
            String gradeCode,
            String statusCode,
            List<String> roleCodes,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.memberSeq = memberSeq;
        this.gradeCode = gradeCode != null ? gradeCode : "NORMAL";
        this.statusCode = statusCode != null ? statusCode : "ACTIVE";
        this.roleCodes = roleCodes != null ? Collections.unmodifiableList(roleCodes) : List.of();
    }
}
