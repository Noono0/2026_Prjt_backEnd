package com.noonoo.prjtbackend.common.interceptor;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.security.CustomUserDetails;
import com.noonoo.prjtbackend.common.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    public static final String LOGIN_MEMBER_SEQ = "LOGIN_MEMBER_SEQ";
    public static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = IpUtil.getClientIp(request);
        RequestContext.setClientIp(clientIp);

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object loginMemberSeq = session.getAttribute(LOGIN_MEMBER_SEQ);
            Object loginMemberId = session.getAttribute(LOGIN_MEMBER_ID);

            if (loginMemberSeq instanceof Number n) {
                RequestContext.setLoginMemberSeq(n.longValue());
            }

            if (loginMemberId instanceof String memberId) {
                RequestContext.setLoginMemberId(memberId);
            }
        }

        fillFromSecurityIfMissing();

        return true;
    }

    /**
     * 세션 속성(LOGIN_MEMBER_*)만으로는 비어 있는 경우가 있음(프록시·세션 복원 차이 등).
     * Spring Security 가 이미 로그인 사용자를 알고 있으면 RequestContext 를 맞춘다.
     */
    private static void fillFromSecurityIfMissing() {
        if (RequestContext.getLoginMemberSeq() != null && StringUtils.hasText(RequestContext.getLoginMemberId())) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return;
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails u)) {
            return;
        }
        if (RequestContext.getLoginMemberSeq() == null && u.getMemberSeq() != null) {
            RequestContext.setLoginMemberSeq(u.getMemberSeq());
        }
        if (!StringUtils.hasText(RequestContext.getLoginMemberId())) {
            RequestContext.setLoginMemberId(u.getUsername());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestContext.clear();
    }
}
