package com.noonoo.prjtbackend.noticeBoard.serviceImpl;

import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.member.MemberDisplayNames;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardDto;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSaveRequest;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSearchCondition;
import com.noonoo.prjtbackend.noticeBoard.mapper.NoticeBoardMapper;
import com.noonoo.prjtbackend.noticeBoard.service.NoticeBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeBoardServiceImpl implements NoticeBoardService {

    private final NoticeBoardMapper noticeBoardMapper;
    private final MemberMapper memberMapper;
    private final ContentFilterApplyService contentFilterApplyService;

    @Override
    public List<OptionDto> findNoticeCategoryOptions() {
        return Collections.unmodifiableList(noticeBoardMapper.findNoticeCategoryOptions());
    }

    @Override
    public PageResponse<NoticeBoardDto> findNoticeBoards(NoticeBoardSearchCondition condition) {
        long totalCount = noticeBoardMapper.findNoticeBoardsCnt(condition);
        List<NoticeBoardDto> items = noticeBoardMapper.findNoticeBoards(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public NoticeBoardDto findNoticeBoardDetail(Long noticeBoardSeq) {
        return noticeBoardMapper.findNoticeBoardById(noticeBoardSeq);
    }

    @Override
    public List<NoticeBoardDto> findNoticeBoardsPinnedOnFreeBoard() {
        return Collections.unmodifiableList(noticeBoardMapper.findNoticeBoardsPinnedOnFreeBoard());
    }

    @Override
    @Transactional
    public int createNoticeBoard(NoticeBoardSaveRequest condition) {
        log.info("=======> /api/notice-boards/createNoticeBoard serviceimpl param={}", condition);

        String loginMemberId = RequestContext.getLoginMemberId();
        Long loginMemberSeq = RequestContext.getLoginMemberSeq();
        String clientIp = RequestContext.getClientIp();

        String writerName = null;
        if (StringUtils.hasText(loginMemberId)) {
            MemberDto loginMember = memberMapper.findLoginMember(loginMemberId);
            if (loginMember != null) {
                if (loginMemberSeq == null) {
                    loginMemberSeq = loginMember.getMemberSeq();
                }
                writerName = MemberDisplayNames.fromMember(loginMember);
            }
        }

        condition.setWriterMemberSeq(loginMemberSeq);
        condition.setWriterName(StringUtils.hasText(writerName) ? writerName : loginMemberId);
        condition.setCreateId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setCreateIp(clientIp);
        condition.setModifyIp(clientIp);

        if (!StringUtils.hasText(condition.getShowYn())) {
            condition.setShowYn("Y");
        }
        if (!StringUtils.hasText(condition.getHighlightYn())) {
            condition.setHighlightYn("N");
        }

        if (condition.getViewCount() == null) condition.setViewCount(0L);
        if (condition.getLikeCount() == null) condition.setLikeCount(0L);
        if (condition.getDislikeCount() == null) condition.setDislikeCount(0L);
        if (condition.getCommentCount() == null) condition.setCommentCount(0L);
        if (condition.getCommentLikeCount() == null) condition.setCommentLikeCount(0L);
        if (condition.getCommentReportCount() == null) condition.setCommentReportCount(0L);
        if (condition.getReportCount() == null) condition.setReportCount(0L);

        normalizeNoticeBoardFlags(condition);
        condition.setTitle(contentFilterApplyService.applyField("제목", condition.getTitle()));
        condition.setContent(contentFilterApplyService.applyField("내용", condition.getContent()));

        return noticeBoardMapper.insertNoticeBoard(condition);
    }

    @Override
    @Transactional
    public int updateNoticeBoard(NoticeBoardSaveRequest condition) {
        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setModifyIp(clientIp);

        normalizeNoticeBoardFlags(condition);
        condition.setTitle(contentFilterApplyService.applyField("제목", condition.getTitle()));
        condition.setContent(contentFilterApplyService.applyField("내용", condition.getContent()));

        return noticeBoardMapper.updateNoticeBoard(condition);
    }

    private void normalizeNoticeBoardFlags(NoticeBoardSaveRequest condition) {
        if (!StringUtils.hasText(condition.getCommentAllowedYn())) {
            condition.setCommentAllowedYn("Y");
        } else {
            String c = condition.getCommentAllowedYn().trim().toUpperCase(Locale.ROOT);
            condition.setCommentAllowedYn("Y".equals(c) ? "Y" : "N");
        }
        if (!StringUtils.hasText(condition.getReplyAllowedYn())) {
            condition.setReplyAllowedYn("Y");
        } else {
            String r = condition.getReplyAllowedYn().trim().toUpperCase(Locale.ROOT);
            condition.setReplyAllowedYn("Y".equals(r) ? "Y" : "N");
        }
        if ("N".equals(condition.getCommentAllowedYn())) {
            condition.setReplyAllowedYn("N");
        }
        if (!StringUtils.hasText(condition.getPinOnFreeBoardYn())) {
            condition.setPinOnFreeBoardYn("N");
        } else {
            String p = condition.getPinOnFreeBoardYn().trim().toUpperCase(Locale.ROOT);
            condition.setPinOnFreeBoardYn("Y".equals(p) ? "Y" : "N");
        }
    }

    @Override
    @Transactional
    public int deleteNoticeBoard(Long noticeBoardSeq) {
        return noticeBoardMapper.deleteNoticeBoard(noticeBoardSeq);
    }

    @Override
    @Transactional
    public int increaseViewCount(Long noticeBoardSeq) {
        return noticeBoardMapper.increaseNoticeBoardViewCount(noticeBoardSeq);
    }

    @Override
    @Transactional
    public int likeNoticeBoard(Long noticeBoardSeq) {
        return increaseWithActionLog("NOTICE_BOARD", "POST", noticeBoardSeq, "LIKE",
                () -> noticeBoardMapper.increaseNoticeBoardLikeCount(noticeBoardSeq));
    }

    @Override
    @Transactional
    public int dislikeNoticeBoard(Long noticeBoardSeq) {
        return increaseWithActionLog("NOTICE_BOARD", "POST", noticeBoardSeq, "DISLIKE",
                () -> noticeBoardMapper.increaseNoticeBoardDislikeCount(noticeBoardSeq));
    }

    @Override
    @Transactional
    public int reportNoticeBoard(Long noticeBoardSeq) {
        return increaseWithActionLog("NOTICE_BOARD", "POST", noticeBoardSeq, "REPORT",
                () -> noticeBoardMapper.increaseNoticeBoardReportCount(noticeBoardSeq));
    }

    private int increaseWithActionLog(String boardKind,
                                      String targetKind,
                                      Long targetSeq,
                                      String actionType,
                                      CounterUpdater counterUpdater) {
        Long memberSeq = RequestContext.getLoginMemberSeq();
        String memberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        int inserted = noticeBoardMapper.insertNoticeBoardActionLog(
                boardKind,
                targetKind,
                targetSeq,
                actionType,
                memberSeq,
                memberId,
                clientIp
        );

        if (inserted <= 0) {
            return 0;
        }
        return counterUpdater.update();
    }

    @FunctionalInterface
    private interface CounterUpdater {
        int update();
    }
}
