package com.noonoo.prjtbackend.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Long memberId;
    private final String roleCode;

    public CustomUserDetails(
            Long memberId,
            String username,
            String memberPwd,
            String roleCode,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, memberPwd, authorities);
        this.memberId = memberId;
        this.roleCode = roleCode;
    }
}
