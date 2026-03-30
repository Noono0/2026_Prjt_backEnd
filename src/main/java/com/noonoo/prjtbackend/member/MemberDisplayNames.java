package com.noonoo.prjtbackend.member;

import com.noonoo.prjtbackend.member.dto.MemberDto;
import org.springframework.util.StringUtils;

/** 화면·작성자 표시: 닉네임 우선, 없으면 회원명. */
public final class MemberDisplayNames {

    private MemberDisplayNames() {}

    public static String fromMember(MemberDto m) {
        if (m == null) {
            return null;
        }
        if (StringUtils.hasText(m.getNickname())) {
            return m.getNickname().trim();
        }
        if (StringUtils.hasText(m.getMemberName())) {
            return m.getMemberName().trim();
        }
        return null;
    }
}
