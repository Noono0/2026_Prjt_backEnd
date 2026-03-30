package com.noonoo.prjtbackend.blacklistreport.serviceImpl;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import com.noonoo.prjtbackend.blacklistreport.mapper.BlacklistReportCommentMapper;
import com.noonoo.prjtbackend.blacklistreport.mapper.BlacklistReportMapper;
import com.noonoo.prjtbackend.blacklistreport.service.BlacklistReportCommentService;
import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import com.noonoo.prjtbackend.board.support.BoardBlindSupport;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.member.MemberDisplayNames;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistReportCommentServiceImpl implements BlacklistReportCommentService {

    private static final Comparator<BoardCommentDto> BY_CREATE_DT =
            Comparator.comparing(BoardCommentDto::getCreateDt, Comparator.nullsLast(String::compareTo));

    private static final int MAX_EMOTICONS_PER_COMMENT = 3;
    private static final int MAX_EMOTICON_AND_IMAGES_TOTAL = 3;

    private final BlacklistReportCommentMapper blacklistReportCommentMapper;
    private final BlacklistReportMapper blacklistReportMapper;
    private final MemberMapper memberMapper;
    private final ContentFilterApplyService contentFilterApplyService;
    private final BoardBlindSupport boardBlindSupport;

    @Override
    public List<BoardCommentDto> findComments(Long blacklistReportSeq, String sort) {
        BlacklistReportDto post = blacklistReportMapper.findById(blacklistReportSeq);
        if (post == null || boardBlindSupport.isBlacklistReportBlind(post)) {
            return Collections.emptyList();
        }
        String s = normalizeSort(sort);
        Long mem = RequestContext.getLoginMemberSeq();
        List<BoardCommentDto> flat =
                blacklistReportCommentMapper.findBlacklistReportCommentsFlat(
                        blacklistReportSeq,
                        mem,
                        boardBlindSupport.getBlindReportThreshold(),
                        boardBlindSupport.getBlindCommentMaskedContentHtml());
        if (flat.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<BoardCommentDto>> childMap = new LinkedHashMap<>();
        List<BoardCommentDto> roots = new ArrayList<>();
        for (BoardCommentDto c : flat) {
            if (c.getParentBoardCommentSeq() == null) {
                roots.add(c);
            } else {
                childMap.computeIfAbsent(c.getParentBoardCommentSeq(), k -> new ArrayList<>()).add(c);
            }
        }
        for (List<BoardCommentDto> kids : childMap.values()) {
            kids.sort(BY_CREATE_DT);
        }
        switch (s) {
            case "oldest" -> roots.sort(BY_CREATE_DT);
            case "like" -> roots.sort(
                    Comparator.comparing((BoardCommentDto c) -> c.getLikeCount() != null ? c.getLikeCount() : 0L)
                            .reversed()
                            .thenComparing(BY_CREATE_DT.reversed()));
            default -> roots.sort(BY_CREATE_DT.reversed());
        }
        for (BoardCommentDto r : roots) {
            List<BoardCommentDto> children = childMap.getOrDefault(r.getBoardCommentSeq(), Collections.emptyList());
            r.setChildren(new ArrayList<>(children));
        }
        return roots;
    }

    @Override
    @Transactional
    public long createComment(BoardCommentSaveRequest req) {
        if (req.getBoardSeq() == null) {
            throw new IllegalArgumentException("게시글 정보가 없습니다.");
        }
        BlacklistReportDto post = blacklistReportMapper.findById(req.getBoardSeq());
        if (post == null) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
        if (boardBlindSupport.isBlacklistReportBlind(post)) {
            throw new IllegalArgumentException("블라인드 처리된 게시글입니다.");
        }
        if (!isBoardYnAllowed(post.getCommentAllowedYn())) {
            throw new IllegalArgumentException("이 게시글은 댓글 작성이 허용되지 않았습니다.");
        }
        if (req.getParentBoardCommentSeq() != null && !isBoardYnAllowed(post.getReplyAllowedYn())) {
            throw new IllegalArgumentException("이 게시글은 답글 작성이 허용되지 않았습니다.");
        }

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

        validateCommentBody(req.getContent(), req.getEmoticonSeq1(), req.getEmoticonSeq2(), req.getEmoticonSeq3());
        validateEmoticonSlots(loginMemberSeq, req.getEmoticonSeq1(), req.getEmoticonSeq2(), req.getEmoticonSeq3());
        validateEmoticonAndImageTotal(req.getContent(), req.getEmoticonSeq1(), req.getEmoticonSeq2(), req.getEmoticonSeq3());
        req.setContent(contentFilterApplyService.applyField("댓글", req.getContent()));

        if (req.getParentBoardCommentSeq() != null) {
            BoardCommentDto parent = blacklistReportCommentMapper.findBlacklistReportCommentById(req.getParentBoardCommentSeq());
            if (parent == null || !Objects.equals(parent.getBoardSeq(), req.getBoardSeq())) {
                throw new IllegalArgumentException("원 댓글을 찾을 수 없습니다.");
            }
            if (boardBlindSupport.isCommentBlind(parent)) {
                throw new IllegalArgumentException("블라인드 처리된 댓글에는 답글을 달 수 없습니다.");
            }
            if (parent.getParentBoardCommentSeq() != null) {
                throw new IllegalArgumentException("답글에는 다시 답글을 달 수 없습니다.");
            }
        }

        String writerName = null;
        if (StringUtils.hasText(loginMemberId)) {
            MemberDto loginMember = memberMapper.findLoginMember(loginMemberId);
            if (loginMember != null) {
                writerName = MemberDisplayNames.fromMember(loginMember);
            }
        }
        if (!StringUtils.hasText(writerName)) {
            writerName = StringUtils.hasText(loginMemberId) ? loginMemberId : "회원";
        }

        req.setWriterMemberSeq(loginMemberSeq);
        req.setWriterName(writerName);
        req.setCreateId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        req.setCreateIp(RequestContext.getClientIp());

        blacklistReportCommentMapper.insertBlacklistReportComment(req);
        blacklistReportMapper.increaseBlacklistReportCommentCount(req.getBoardSeq());

        return req.getBoardCommentSeq() != null ? req.getBoardCommentSeq() : 0L;
    }

    @Override
    @Transactional
    public int likeComment(Long blacklistReportSeq, Long commentSeq) {
        Long memberSeq = requireMemberSeq();
        if (isBlacklistReportBlindBySeq(blacklistReportSeq)) {
            return 0;
        }
        BoardCommentDto c = blacklistReportCommentMapper.findBlacklistReportCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), blacklistReportSeq)) {
            return 0;
        }
        if (boardBlindSupport.isCommentBlind(c)) {
            return 0;
        }
        String existing = blacklistReportCommentMapper.findVoteType(commentSeq, memberSeq);
        if (existing == null) {
            blacklistReportCommentMapper.insertVote(commentSeq, memberSeq, "L");
            blacklistReportCommentMapper.adjustLikeDislike(commentSeq, 1, 0);
            return 1;
        }
        if ("L".equals(existing)) {
            return 0;
        }
        blacklistReportCommentMapper.updateVoteType(commentSeq, memberSeq, "L");
        blacklistReportCommentMapper.adjustLikeDislike(commentSeq, 1, -1);
        return 1;
    }

    @Override
    @Transactional
    public int dislikeComment(Long blacklistReportSeq, Long commentSeq) {
        Long memberSeq = requireMemberSeq();
        if (isBlacklistReportBlindBySeq(blacklistReportSeq)) {
            return 0;
        }
        BoardCommentDto c = blacklistReportCommentMapper.findBlacklistReportCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), blacklistReportSeq)) {
            return 0;
        }
        if (boardBlindSupport.isCommentBlind(c)) {
            return 0;
        }
        String existing = blacklistReportCommentMapper.findVoteType(commentSeq, memberSeq);
        if (existing == null) {
            blacklistReportCommentMapper.insertVote(commentSeq, memberSeq, "D");
            blacklistReportCommentMapper.adjustLikeDislike(commentSeq, 0, 1);
            return 1;
        }
        if ("D".equals(existing)) {
            return 0;
        }
        blacklistReportCommentMapper.updateVoteType(commentSeq, memberSeq, "D");
        blacklistReportCommentMapper.adjustLikeDislike(commentSeq, -1, 1);
        return 1;
    }

    @Override
    @Transactional
    public int reportComment(Long blacklistReportSeq, Long commentSeq) {
        Long memberSeq = requireMemberSeq();
        if (isBlacklistReportBlindBySeq(blacklistReportSeq)) {
            return 0;
        }
        BoardCommentDto before = blacklistReportCommentMapper.findBlacklistReportCommentById(commentSeq);
        if (before == null || !Objects.equals(before.getBoardSeq(), blacklistReportSeq)) {
            return 0;
        }
        if (boardBlindSupport.isCommentBlind(before)) {
            return 0;
        }
        int inserted = blacklistReportCommentMapper.insertReportIgnore(commentSeq, memberSeq);
        if (inserted > 0) {
            blacklistReportCommentMapper.increaseReportCount(commentSeq);
        }
        return inserted;
    }

    @Override
    @Transactional
    public int updateComment(Long blacklistReportSeq, Long commentSeq, BoardCommentUpdateRequest body) {
        if (body == null) {
            throw new IllegalArgumentException("수정 내용이 없습니다.");
        }
        Long loginMemberSeq = requireMemberSeq();
        if (isBlacklistReportBlindBySeq(blacklistReportSeq)) {
            throw new IllegalArgumentException("블라인드 처리된 게시글입니다.");
        }
        BoardCommentDto c = blacklistReportCommentMapper.findBlacklistReportCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), blacklistReportSeq)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        if (boardBlindSupport.isCommentBlind(c)) {
            throw new IllegalArgumentException("블라인드 처리된 댓글은 수정할 수 없습니다.");
        }
        if (!Objects.equals(c.getWriterMemberSeq(), loginMemberSeq)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }
        validateCommentBody(body.getContent(), body.getEmoticonSeq1(), body.getEmoticonSeq2(), body.getEmoticonSeq3());
        validateEmoticonSlots(loginMemberSeq, body.getEmoticonSeq1(), body.getEmoticonSeq2(), body.getEmoticonSeq3());
        validateEmoticonAndImageTotal(body.getContent(), body.getEmoticonSeq1(), body.getEmoticonSeq2(), body.getEmoticonSeq3());
        body.setContent(contentFilterApplyService.applyField("댓글", body.getContent()));
        return blacklistReportCommentMapper.updateBlacklistReportComment(blacklistReportSeq, commentSeq, loginMemberSeq, body);
    }

    @Override
    @Transactional
    public int deleteComment(Long blacklistReportSeq, Long commentSeq) {
        Long loginMemberSeq = requireMemberSeq();
        if (isBlacklistReportBlindBySeq(blacklistReportSeq)) {
            throw new IllegalArgumentException("블라인드 처리된 게시글입니다.");
        }
        BoardCommentDto c = blacklistReportCommentMapper.findBlacklistReportCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), blacklistReportSeq)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        if (!Objects.equals(c.getWriterMemberSeq(), loginMemberSeq)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
        if (c.getParentBoardCommentSeq() == null) {
            int n = blacklistReportCommentMapper.softDeleteBlacklistReportCommentThread(blacklistReportSeq, commentSeq);
            if (n > 0) {
                blacklistReportMapper.adjustBlacklistReportCommentCount(blacklistReportSeq, -n);
            }
            return n;
        }
        int n = blacklistReportCommentMapper.softDeleteBlacklistReportCommentRow(blacklistReportSeq, commentSeq);
        if (n > 0) {
            blacklistReportMapper.adjustBlacklistReportCommentCount(blacklistReportSeq, -1);
        }
        return n;
    }

    private boolean isBlacklistReportBlindBySeq(Long blacklistReportSeq) {
        if (blacklistReportSeq == null) {
            return true;
        }
        BlacklistReportDto b = blacklistReportMapper.findById(blacklistReportSeq);
        return b == null || boardBlindSupport.isBlacklistReportBlind(b);
    }

    private static String normalizeSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return "latest";
        }
        return switch (sort.trim().toLowerCase(Locale.ROOT)) {
            case "oldest", "like", "latest" -> sort.trim().toLowerCase(Locale.ROOT);
            default -> "latest";
        };
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

    private void validateCommentBody(String content, Long e1, Long e2, Long e3) {
        boolean emo = e1 != null || e2 != null || e3 != null;
        if (emo) {
            return;
        }
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        String lower = content.toLowerCase(Locale.ROOT);
        if (lower.contains("<img")) {
            return;
        }
        String plain = content.replaceAll("(?s)<[^>]+>", "").replace("&nbsp;", " ").trim();
        if (!StringUtils.hasText(plain)) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
    }

    private void validateEmoticonSlots(Long memberSeq, Long e1, Long e2, Long e3) {
        List<Long> raw = new ArrayList<>();
        if (e1 != null) {
            raw.add(e1);
        }
        if (e2 != null) {
            raw.add(e2);
        }
        if (e3 != null) {
            raw.add(e3);
        }
        Set<Long> distinct = new LinkedHashSet<>(raw);
        if (distinct.size() != raw.size()) {
            throw new IllegalArgumentException("같은 이모티콘을 중복해 넣을 수 없습니다.");
        }
        if (raw.size() > MAX_EMOTICONS_PER_COMMENT) {
            throw new IllegalArgumentException("이모티콘은 최대 " + MAX_EMOTICONS_PER_COMMENT + "개까지 넣을 수 있습니다.");
        }
        if (distinct.isEmpty()) {
            return;
        }
        List<Long> ids = new ArrayList<>(distinct);
        int owned = blacklistReportCommentMapper.countEmoticonsForMember(memberSeq, ids);
        if (owned != ids.size()) {
            throw new IllegalArgumentException("본인이 등록한 이모티콘만 사용할 수 있습니다.");
        }
    }

    private static int countImgTags(String html) {
        if (!StringUtils.hasText(html)) {
            return 0;
        }
        String lower = html.toLowerCase(Locale.ROOT);
        int count = 0;
        int idx = 0;
        while (idx < lower.length()) {
            int found = lower.indexOf("<img", idx);
            if (found < 0) {
                break;
            }
            count++;
            idx = found + 4;
        }
        return count;
    }

    private void validateEmoticonAndImageTotal(String content, Long e1, Long e2, Long e3) {
        int emo = 0;
        if (e1 != null) {
            emo++;
        }
        if (e2 != null) {
            emo++;
        }
        if (e3 != null) {
            emo++;
        }
        int imgs = countImgTags(content);
        if (emo + imgs > MAX_EMOTICON_AND_IMAGES_TOTAL) {
            throw new IllegalArgumentException(
                    "이모티콘과 이미지는 합쳐 최대 " + MAX_EMOTICON_AND_IMAGES_TOTAL + "개까지 첨부할 수 있습니다.");
        }
    }

    private static boolean isBoardYnAllowed(String yn) {
        if (!StringUtils.hasText(yn)) {
            return true;
        }
        return "Y".equalsIgnoreCase(yn.trim());
    }
}
