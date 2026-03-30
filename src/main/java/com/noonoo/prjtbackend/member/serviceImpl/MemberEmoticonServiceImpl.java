package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberEmoticonDto;
import com.noonoo.prjtbackend.member.dto.MemberEmoticonSaveRequest;
import com.noonoo.prjtbackend.member.mapper.MemberEmoticonMapper;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.MemberEmoticonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberEmoticonServiceImpl implements MemberEmoticonService {

    private static final int MAX_EMOTICONS_PER_MEMBER = 3;

    private final MemberEmoticonMapper memberEmoticonMapper;
    private final MemberMapper memberMapper;

    @Override
    public List<MemberEmoticonDto> findMyEmoticons() {
        Long memberSeq = requireMemberSeq();
        return memberEmoticonMapper.findByMemberSeq(memberSeq);
    }

    @Override
    @Transactional
    public int addMyEmoticon(MemberEmoticonSaveRequest request) {
        Long memberSeq = requireMemberSeq();
        if (request == null || !StringUtils.hasText(request.getImageUrl())) {
            throw new IllegalArgumentException("이미지 URL이 필요합니다.");
        }
        int cnt = memberEmoticonMapper.countByMemberSeq(memberSeq);
        if (cnt >= MAX_EMOTICONS_PER_MEMBER) {
            throw new IllegalArgumentException("이모티콘은 최대 " + MAX_EMOTICONS_PER_MEMBER + "개까지 등록할 수 있습니다.");
        }
        return memberEmoticonMapper.insert(memberSeq, request.getImageUrl().trim(), cnt);
    }

    @Override
    @Transactional
    public int deleteMyEmoticon(Long memberEmoticonSeq) {
        Long memberSeq = requireMemberSeq();
        return memberEmoticonMapper.deleteBySeqAndMember(memberEmoticonSeq, memberSeq);
    }

    private Long requireMemberSeq() {
        String loginMemberId = RequestContext.getLoginMemberId();
        Long loginMemberSeq = RequestContext.getLoginMemberSeq();
        if (loginMemberSeq == null && StringUtils.hasText(loginMemberId)) {
            MemberDto m = memberMapper.findLoginMember(loginMemberId);
            if (m != null) {
                loginMemberSeq = m.getMemberSeq();
            }
        }
        if (loginMemberSeq == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return loginMemberSeq;
    }
}
