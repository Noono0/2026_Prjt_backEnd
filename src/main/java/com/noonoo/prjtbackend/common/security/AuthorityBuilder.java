package com.noonoo.prjtbackend.common.security;

import com.noonoo.prjtbackend.role.dto.RoleMenuPermissionDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthorityBuilder {

    public List<SimpleGrantedAuthority> buildAuthorities(List<RoleMenuPermissionDto> permissions) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (RoleMenuPermissionDto p : permissions) {
            String menuCode = p.getMenuCode();

            // READ 권한이 있으면 메뉴 진입 가능하다고 간주
            if ("Y".equalsIgnoreCase(p.getCanRead())) {
                authorities.add(new SimpleGrantedAuthority(menuCode + "_READ"));
            }
            if ("Y".equalsIgnoreCase(p.getCanCreate())) {
                authorities.add(new SimpleGrantedAuthority(menuCode + "_CREATE"));
            }
            if ("Y".equalsIgnoreCase(p.getCanUpdate())) {
                authorities.add(new SimpleGrantedAuthority(menuCode + "_UPDATE"));
            }
            if ("Y".equalsIgnoreCase(p.getCanDelete())) {
                authorities.add(new SimpleGrantedAuthority(menuCode + "_DELETE"));
            }
        }

        return authorities;
    }
}