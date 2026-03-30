package com.noonoo.prjtbackend.common.security;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.interceptor.RequestContextInterceptor;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * RequestContext(세션)와 SecurityContextHolder 를 합쳐 회원 seq/id 를 구합니다.
 * 인터셉터보다 먼저 실행되거나 RequestContext 가 비어 있어도 HttpSession 에서 직접 읽습니다.
 */
@Service
@RequiredArgsConstructor
public class CurrentMemberService {

    private final MemberMapper memberMapper;

    public Optional<AuthenticatedMember> resolve() {
        Long seq = RequestContext.getLoginMemberSeq();
        String id = RequestContext.getLoginMemberId();

        mergeFromHttpSession(seq, id);
        seq = RequestContext.getLoginMemberSeq();
        id = RequestContext.getLoginMemberId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails u) {
            if (seq == null || seq <= 0) {
                seq = u.getMemberSeq();
            }
            if (!StringUtils.hasText(id)) {
                id = u.getUsername();
            }
        }

        if ((seq == null || seq <= 0) && StringUtils.hasText(id)) {
            MemberDto m = memberMapper.findLoginMember(id);
            if (m != null && m.getMemberSeq() != null) {
                seq = m.getMemberSeq();
            }
        }

        if (seq == null || seq <= 0) {
            return Optional.empty();
        }
        return Optional.of(new AuthenticatedMember(seq, StringUtils.hasText(id) ? id : null));
    }

    /** RequestContext 가 비어 있을 때만 세션 속성으로 보강(로그인 API 가 넣은 LOGIN_MEMBER_*). */
    private static void mergeFromHttpSession(Long seq, String id) {
        boolean needSeq = seq == null || seq <= 0;
        boolean needId = !StringUtils.hasText(id);
        if (!needSeq && !needId) {
            return;
        }
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        HttpSession session = attrs.getRequest().getSession(false);
        if (session == null) {
            return;
        }
        if (needSeq) {
            Object o = session.getAttribute(RequestContextInterceptor.LOGIN_MEMBER_SEQ);
            if (o instanceof Number n) {
                RequestContext.setLoginMemberSeq(n.longValue());
            } else if (o != null) {
                try {
                    RequestContext.setLoginMemberSeq(Long.parseLong(o.toString().trim()));
                } catch (NumberFormatException ignored) {
                    // ignore
                }
            }
        }
        if (needId) {
            Object o = session.getAttribute(RequestContextInterceptor.LOGIN_MEMBER_ID);
            if (o instanceof String s && StringUtils.hasText(s)) {
                RequestContext.setLoginMemberId(s);
            }
        }
    }
}
