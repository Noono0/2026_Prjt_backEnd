package com.noonoo.prjtbackend.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * {@code @PreAuthorize} 에서 사용하는 SpEL 헬퍼.
 * <ul>
 *     <li>{@code app.security.permit-all=true} (기본): 로컬 개발 시 권한 검사 생략</li>
 *     <li>{@code false}: DB 기반 GrantedAuthority ({@code MENU_READ} 등) 검사</li>
 * </ul>
 */
@Component("securityExpressions")
public class SecurityExpressions {

    @Value("${app.security.permit-all:true}")
    private boolean permitAll;

    public boolean isPermitAllOrHasAuthority(String authority) {
        if (permitAll) {
            return true;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (authority.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public boolean canRead(String menuCode) {
        return isPermitAllOrHasAuthority(menuCode + "_READ");
    }

    public boolean canCreate(String menuCode) {
        return isPermitAllOrHasAuthority(menuCode + "_CREATE");
    }

    public boolean canUpdate(String menuCode) {
        return isPermitAllOrHasAuthority(menuCode + "_UPDATE");
    }

    public boolean canDelete(String menuCode) {
        return isPermitAllOrHasAuthority(menuCode + "_DELETE");
    }

    /** 샘플 API 등: 로그인만 되면 허용 (permit-all 모드에서는 항상 true) */
    public boolean isAuthenticatedOrPermitAll() {
        if (permitAll) {
            return true;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated();
    }
}
