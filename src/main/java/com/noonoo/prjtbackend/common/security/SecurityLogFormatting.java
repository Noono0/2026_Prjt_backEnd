package com.noonoo.prjtbackend.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * 권한 거부·인가 실패 로그를 사람이 읽기 쉽게 출력할 때 사용.
 */
public final class SecurityLogFormatting {

    private SecurityLogFormatting() {
    }

    /** principal이 {@link CustomUserDetails}일 때 회원 역할 코드 한 줄. */
    public static String roleCodesLineFromPrincipal(Object principal) {
        if (principal instanceof CustomUserDetails cud) {
            return roleCodesLine(cud);
        }
        if (principal == null) {
            return "(principal=null)";
        }
        return "(CustomUserDetails 아님, 타입=" + principal.getClass().getSimpleName() + ")";
    }

    private static String roleCodesLine(CustomUserDetails cud) {
        List<String> codes = cud.getRoleCodes();
        if (codes == null || codes.isEmpty()) {
            return "(역할 코드 없음)";
        }
        return String.join(", ", codes);
    }

    /**
     * 현재 Authentication 의 권한을 정렬 후 줄바꿈(항목당 {@code perLine}개)으로 이어 붙인다.
     */
    public static String sortedAuthoritiesMultiline(Authentication auth, int perLine) {
        if (auth == null) {
            return "(인증 정보 없음)";
        }
        List<String> list = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();
        return joinWrapping(list, perLine);
    }

    static String joinWrapping(List<String> items, int perLine) {
        if (items.isEmpty()) {
            return "(권한 없음)";
        }
        if (perLine <= 0) {
            perLine = 5;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("총 ").append(items.size()).append("개 — ");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
                if (i % perLine == 0) {
                    sb.append("\n      ");
                }
            }
            sb.append(items.get(i));
        }
        return sb.toString();
    }
}
