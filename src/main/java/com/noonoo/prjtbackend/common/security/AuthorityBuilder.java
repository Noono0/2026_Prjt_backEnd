package com.noonoo.prjtbackend.common.security;

import com.noonoo.prjtbackend.role.dto.RoleMenuPermissionDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class AuthorityBuilder {

    public List<SimpleGrantedAuthority> buildAuthorities(List<RoleMenuPermissionDto> permissions) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (RoleMenuPermissionDto p : permissions) {
            String menuCode = p.getMenuCode();

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

    /** 동일 메뉴 권한이 여러 ROLE에서 오면 중복 제거 */
    public List<SimpleGrantedAuthority> buildAuthoritiesDeduped(List<RoleMenuPermissionDto> permissions) {
        List<SimpleGrantedAuthority> raw = buildAuthorities(permissions);
        Set<String> seen = new LinkedHashSet<>();
        List<SimpleGrantedAuthority> out = new ArrayList<>();
        for (SimpleGrantedAuthority a : raw) {
            if (seen.add(a.getAuthority())) {
                out.add(a);
            }
        }
        return out;
    }

    /**
     * Spring Security hasRole() 등에서 사용할 시스템 역할.
     * 예: ROLE_ADMIN, ROLE_USER
     */
    public List<SimpleGrantedAuthority> buildSystemRoleAuthorities(List<String> roleCodes) {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        if (roleCodes == null) {
            return list;
        }
        for (String code : roleCodes) {
            if (code != null && !code.isBlank()) {
                list.add(new SimpleGrantedAuthority("ROLE_" + code.trim()));
            }
        }
        return list;
    }

    public List<SimpleGrantedAuthority> mergeAuthorities(
            List<SimpleGrantedAuthority> menuAuthorities,
            List<SimpleGrantedAuthority> roleAuthorities
    ) {
        Set<String> seen = new LinkedHashSet<>();
        List<SimpleGrantedAuthority> out = new ArrayList<>();
        for (SimpleGrantedAuthority a : menuAuthorities) {
            if (seen.add(a.getAuthority())) {
                out.add(a);
            }
        }
        for (SimpleGrantedAuthority a : roleAuthorities) {
            if (seen.add(a.getAuthority())) {
                out.add(a);
            }
        }
        return out;
    }
}
