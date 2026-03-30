package com.noonoo.prjtbackend.noticeBoard.serviceImpl;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.member.MemberDisplayNames;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.WalletPointGrantService;
import com.noonoo.prjtbackend.board.support.BoardBlindSupport;
import com.noonoo.prjtbackend.noticeBoard.mapper.NoticeBoardCommentMapper;
import com.noonoo.prjtbackend.noticeBoard.mapper.NoticeBoardMapper;
import com.noonoo.prjtbackend.noticeBoard.service.NoticeBoardCommentService;
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
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeBoardCommentServiceImpl implements NoticeBoardCommentService {

    private static final Comparator<BoardCommentDto> BY_CREATE_DT =
            Comparator.comparing(BoardCommentDto::getCreateDt, Comparator.nullsLast(String::compareTo));

    private static final int MAX_EMOTICONS_PER_COMMENT = 3;
    private static final int MAX_EMOTICON_AND_IMAGES_TOTAL = 3;

    private final NoticeBoardCommentMapper noticeBoardCommentMapper;
    private final NoticeBoardMapper noticeBoardMapper;
    private final MemberMapper memberMapper;
    private final ContentFilterApplyService contentFilterApplyService;
    private final WalletPointGrantService walletPointGrantService;
    private final BoardBlindSupport boardBlindSupport;

    @Override
    public List<BoardCommentDto> findComments(Long noticeBoardSeq, String sort) {
        String s = normalizeSort(sort);
        Long mem = RequestContext.getLoginMemberSeq();
        List<BoardCommentDto> flat =
                noticeBoardCommentMapper.findNoticeBoardCommentsFlat(
                        noticeBoardSeq,
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
        NoticeBoardDto post = noticeBoardMapper.findNoticeBoardById(req.getBoardSeq());
        if (post == null) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
        if (!isNoticeBoardYnAllowed(post.getCommentAllowedYn())) {
            throw new IllegalArgumentException("이 게시글은 댓글 작성이 허용되지 않았습니다.");
        }
        if (req.getParentBoardCommentSeq() != null && !isNoticeBoardYnAllowed(post.getReplyAllowedYn())) {
            throw new IllegalArgumentException("이 게시글은 답글 작성이 허용되지 않았습니다.");
        }

        String loginMemberId = RequestContext.getLoginMemberId();
        Long loginMemberSeq = RequestContext.getLoginMemberSeq();
        if (loginMemberSeq == null && !StringUtils.hasText(loginMemberId)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
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
            BoardCommentDto parent = noticeBoardCommentMapper.findNoticeBoardCommentById(req.getParentBoardCommentSeq());
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

        long noticeBoardSeq = req.getBoardSeq();
        int priorCount = noticeBoardCommentMapper.countCommentsByNoticeAndMember(noticeBoardSeq, loginMemberSeq);

        noticeBoardCommentMapper.insertNoticeBoardComment(req);
        noticeBoardMapper.increaseNoticeBoardCommentCount(req.getBoardSeq());

        try {
            walletPointGrantService.grantNoticeComment(loginMemberSeq, noticeBoardSeq, priorCount);
        } catch (Exception ignored) {
            // 댓글 등록은 유지
        }

        return req.getBoardCommentSeq() != null ? req.getBoardCommentSeq() : 0L;
    }

    @Override
    @Transactional
    public int likeComment(Long noticeBoardSeq, Long commentSeq) {
        Long memberSeq = requireMemberSeq();
        BoardCommentDto c = noticeBoardCommentMapper.findNoticeBoardCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), noticeBoardSeq)) {
            return 0;
        }
        if (boardBlindSupport.isCommentBlind(c)) {
            return 0;
        }
        String existing = noticeBoardCommentMapper.findVoteType(commentSeq, memberSeq);
        if (existing == null) {
            noticeBoardCommentMapper.insertVote(commentSeq, memberSeq, "L");
            noticeBoardCommentMapper.adjustLikeDislike(commentSeq, 1, 0);
            return 1;
        }
        if ("L".equals(existing)) {
            return 0;
        }
        noticeBoardCommentMapper.updateVoteType(commentSeq, memberSeq, "L");
        noticeBoardCommentMapper.adjustLikeDislike(commentSeq, 1, -1);
        return 1;
    }

    @Override
    @Transactional
    public int dislikeComment(Long noticeBoardSeq, Long commentSeq) {
        Long memberSeq = requireMemberSeq();
        BoardCommentDto c = noticeBoardCommentMapper.findNoticeBoardCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), noticeBoardSeq)) {
            return 0;
        }
        if (boardBlindSupport.isCommentBlind(c)) {
            return 0;
        }
        String existing = noticeBoardCommentMapper.findVoteType(commentSeq, memberSeq);
        if (existing == null) {
            noticeBoardCommentMapper.insertVote(commentSeq, memberSeq, "D");
            noticeBoardCommentMapper.adjustLikeDislike(commentSeq, 0, 1);
            return 1;
        }
        if ("D".equals(existing)) {
            return 0;
        }
        noticeBoardCommentMapper.updateVoteType(commentSeq, memberSeq, "D");
        noticeBoardCommentMapper.adjustLikeDislike(commentSeq, -1, 1);
        return 1;
    }

    @Override
    @Transactional
    public int reportComment(Long noticeBoardSeq, Long commentSeq) {
        Long memberSeq = requireMemberSeq();
        BoardCommentDto before = noticeBoardCommentMapper.findNoticeBoardCommentById(commentSeq);
        if (before == null || !Objects.equals(before.getBoardSeq(), noticeBoardSeq)) {
            return 0;
        }
        if (boardBlindSupport.isCommentBlind(before)) {
            return 0;
        }
        long rcBefore = before.getReportCount() != null ? before.getReportCount() : 0L;
        int inserted = noticeBoardCommentMapper.insertReportIgnore(commentSeq, memberSeq);
        if (inserted > 0) {
            noticeBoardCommentMapper.increaseReportCount(commentSeq);
            BoardCommentDto after = noticeBoardCommentMapper.findNoticeBoardCommentById(commentSeq);
            if (after != null) {
                long rcAfter = after.getReportCount() != null ? after.getReportCount() : 0L;
                if (rcBefore < boardBlindSupport.getBlindReportThreshold()
                        && rcAfter >= boardBlindSupport.getBlindReportThreshold()) {
                    Long writer = after.getWriterMemberSeq();
                    if (writer != null && writer > 0) {
                        try {
                            walletPointGrantService.deductCommentRewardOnBlind(writer, commentSeq);
                        } catch (Exception e) {
                            log.warn("블라인드 댓글 포인트 회수 실패 commentSeq={}: {}", commentSeq, e.toString());
                        }
                    }
                }
            }
        }
        return inserted;
    }

    @Override
    @Transactional
    public int updateComment(Long noticeBoardSeq, Long commentSeq, BoardCommentUpdateRequest body) {
        if (body == null) {
            throw new IllegalArgumentException("수정 내용이 없습니다.");
        }
        Long loginMemberSeq = requireMemberSeq();
        BoardCommentDto c = noticeBoardCommentMapper.findNoticeBoardCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), noticeBoardSeq)) {
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
        return noticeBoardCommentMapper.updateNoticeBoardComment(noticeBoardSeq, commentSeq, loginMemberSeq, body);
    }

    @Override
    @Transactional
    public int deleteComment(Long noticeBoardSeq, Long commentSeq) {
        Long loginMemberSeq = requireMemberSeq();
        BoardCommentDto c = noticeBoardCommentMapper.findNoticeBoardCommentById(commentSeq);
        if (c == null || !Objects.equals(c.getBoardSeq(), noticeBoardSeq)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        if (!Objects.equals(c.getWriterMemberSeq(), loginMemberSeq)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
        if (c.getParentBoardCommentSeq() == null) {
            int n = noticeBoardCommentMapper.softDeleteNoticeBoardCommentThread(noticeBoardSeq, commentSeq);
            if (n > 0) {
                noticeBoardMapper.adjustNoticeBoardCommentCount(noticeBoardSeq, -n);
            }
            return n;
        }
        int n = noticeBoardCommentMapper.softDeleteNoticeBoardCommentRow(noticeBoardSeq, commentSeq);
        if (n > 0) {
            noticeBoardMapper.adjustNoticeBoardCommentCount(noticeBoardSeq, -1);
        }
        return n;
    }

    private static String normalizeSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return "latest";
        }
        return switch (sort.trim().toLowerCase()) {
            case "oldest", "like", "latest" -> sort.trim().toLowerCase();
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
        String lower = content.toLowerCase();
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
        int owned = noticeBoardCommentMapper.countEmoticonsForMember(memberSeq, ids);
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

    private static boolean isNoticeBoardYnAllowed(String yn) {
        if (!StringUtils.hasText(yn)) {
            return true;
        }
        return "Y".equalsIgnoreCase(yn.trim());
    }
}
