package com.noonoo.prjtbackend.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    /** 회원 PK */
    private final Long memberSeq;
    private final String roleCode;

    public CustomUserDetails(
            Long memberSeq,
            String username,
            String password,
            String roleCode,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.memberSeq = memberSeq;
        this.roleCode = roleCode;
    }
}
