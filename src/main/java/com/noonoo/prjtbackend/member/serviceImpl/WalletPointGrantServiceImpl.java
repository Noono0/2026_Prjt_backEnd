package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.member.dto.MemberWalletBalance;
import com.noonoo.prjtbackend.member.dto.MemberWalletLedgerDto;
import com.noonoo.prjtbackend.member.mapper.MemberPointCommentExtraMapper;
import com.noonoo.prjtbackend.member.mapper.MemberWalletMapper;
import com.noonoo.prjtbackend.member.service.PointPolicyResolver;
import com.noonoo.prjtbackend.member.service.WalletPointGrantService;
import com.noonoo.prjtbackend.member.wallet.PointPolicyKeys;
import com.noonoo.prjtbackend.member.wallet.WalletPointRules;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletPointGrantServiceImpl implements WalletPointGrantService {

    private final MemberWalletMapper memberWalletMapper;
    private final MemberPointCommentExtraMapper memberPointCommentExtraMapper;
    private final PointPolicyResolver pointPolicyResolver;

    @Value("${app.wallet.free-board-post-points:100}")
    private long freeBoardPostPoints;

    private static final String POST_BOARD = "BOARD";
    private static final String POST_NOTICE = "NOTICE";

    private void ensureWallet(long memberSeq) {
        MemberWalletBalance w = memberWalletMapper.selectWalletByMemberSeq(memberSeq);
        if (w == null) {
            memberWalletMapper.insertWallet(memberSeq);
        }
    }

    private void credit(long memberSeq, String reasonCode, String summary, long points) {
        creditPoints(memberSeq, reasonCode, summary, points);
    }

    @Override
    public boolean creditPoints(long memberSeq, String reasonCode, String summary, long points) {
        if (memberSeq <= 0 || points <= 0) {
            return false;
        }
        ensureWallet(memberSeq);
        int n = memberWalletMapper.addPoints(memberSeq, points);
        if (n <= 0) {
            log.warn("포인트 적립 실패 memberSeq={} reason={}", memberSeq, reasonCode);
            return false;
        }
        MemberWalletLedgerDto row = new MemberWalletLedgerDto();
        row.setMemberSeq(memberSeq);
        row.setReasonCode(reasonCode);
        row.setSummary(summary);
        row.setPointDelta(points);
        row.setIronDelta(0);
        row.setSilverDelta(0);
        row.setGoldDelta(0);
        row.setDiamondDelta(0);
        row.setCreateId("SYSTEM");
        memberWalletMapper.insertLedger(row);
        return true;
    }

    @Override
    @Transactional
    public boolean debitPoints(long memberSeq, String reasonCode, String summary, long points) {
        if (memberSeq <= 0 || points <= 0) {
            return false;
        }
        ensureWallet(memberSeq);
        int n = memberWalletMapper.subtractPointsIfEnough(memberSeq, points);
        if (n <= 0) {
            log.warn("포인트 차감 실패(잔액 부족 등) memberSeq={} amount={} reason={}", memberSeq, points, reasonCode);
            return false;
        }
        MemberWalletLedgerDto row = new MemberWalletLedgerDto();
        row.setMemberSeq(memberSeq);
        row.setReasonCode(reasonCode);
        row.setSummary(summary);
        row.setPointDelta(-points);
        row.setIronDelta(0);
        row.setSilverDelta(0);
        row.setGoldDelta(0);
        row.setDiamondDelta(0);
        row.setCreateId("SYSTEM");
        memberWalletMapper.insertLedger(row);
        return true;
    }

    @Override
    @Transactional
    public void deductFreeBoardPostOnBlind(long memberSeq, long boardSeq) {
        if (memberSeq <= 0 || boardSeq <= 0 || freeBoardPostPoints <= 0) {
            return;
        }
        boolean ok =
                debitPoints(
                        memberSeq,
                        WalletPointRules.REASON_FREE_BOARD_POST_BLIND,
                        String.format("다수 신고 블라인드 (boardSeq=%d)", boardSeq),
                        freeBoardPostPoints);
        log.info(
                "블라인드 글 작성 포인트 회수 memberSeq={} boardSeq={} points={} ok={}",
                memberSeq,
                boardSeq,
                freeBoardPostPoints,
                ok);
    }

    @Override
    @Transactional
    public void deductCommentRewardOnBlind(long memberSeq, long commentSeq) {
        if (memberSeq <= 0 || commentSeq <= 0 || freeBoardPostPoints <= 0) {
            return;
        }
        boolean ok =
                debitPoints(
                        memberSeq,
                        WalletPointRules.REASON_COMMENT_BLIND,
                        String.format("다수 신고 댓글 블라인드 (commentSeq=%d)", commentSeq),
                        freeBoardPostPoints);
        log.info(
                "블라인드 댓글 포인트 회수 memberSeq={} commentSeq={} points={} ok={}",
                memberSeq,
                commentSeq,
                freeBoardPostPoints,
                ok);
    }

    @Override
    @Transactional
    public void grantSignup(long memberSeq) {
        long pts = pointPolicyResolver.reward(PointPolicyKeys.SIGNUP, WalletPointRules.SIGNUP_BONUS);
        if (pts <= 0) {
            return;
        }
        credit(memberSeq, WalletPointRules.REASON_SIGNUP, "회원가입 축하 포인트", pts);
    }

    @Override
    @Transactional
    public void grantFreeBoardPost(long memberSeq, long boardSeq, String categoryCode) {
        if (memberSeq <= 0 || boardSeq <= 0) {
            return;
        }
        long pts = freeBoardPostPoints;
        if (pts <= 0) {
            log.info("자유게시판 글 작성 포인트 스킵: app.wallet.free-board-post-points=0 memberSeq={} boardSeq={}", memberSeq, boardSeq);
            return;
        }
        boolean ok =
                creditPoints(
                        memberSeq,
                        WalletPointRules.REASON_FREE_BOARD_POST,
                        String.format("자유게시판 글 작성 (boardSeq=%d)", boardSeq),
                        pts);
        log.info(
                "자유게시판 글 작성 포인트 지급 memberSeq={} boardSeq={} categoryCode={} points={} ledgerOk={}",
                memberSeq,
                boardSeq,
                categoryCode,
                pts,
                ok);
    }

    @Override
    @Transactional
    public void grantBoardComment(long memberSeq, long boardSeq, int commentCountBeforeThisInsert) {
        if (memberSeq <= 0 || boardSeq <= 0) {
            return;
        }
        if (commentCountBeforeThisInsert <= 0) {
            long pts = pointPolicyResolver.reward(
                    PointPolicyKeys.BOARD_COMMENT_FIRST, WalletPointRules.COMMENT_FIRST_ON_POST);
            if (pts <= 0) {
                return;
            }
            credit(
                    memberSeq,
                    WalletPointRules.REASON_BOARD_COMMENT_FIRST,
                    String.format("자유게시판 댓글 최초 작성 (boardSeq=%d)", boardSeq),
                    pts
            );
            return;
        }
        int cap = pointPolicyResolver.cap(
                PointPolicyKeys.BOARD_COMMENT_EXTRA, (int) WalletPointRules.COMMENT_EXTRA_CAP_PER_POST);
        if (cap <= 0) {
            return;
        }
        Integer extra = memberPointCommentExtraMapper.selectExtraPointsEarned(memberSeq, POST_BOARD, boardSeq);
        int earned = extra != null ? extra : 0;
        if (earned >= cap) {
            return;
        }
        long room = cap - earned;
        long grant = Math.min(room, cap);
        if (grant <= 0) {
            return;
        }
        memberPointCommentExtraMapper.upsertExtraPoints(memberSeq, POST_BOARD, boardSeq, (int) grant);
        credit(
                memberSeq,
                WalletPointRules.REASON_BOARD_COMMENT_EXTRA,
                String.format("자유게시판 댓글 추가 적립 (boardSeq=%d)", boardSeq),
                grant
        );
    }

    @Override
    @Transactional
    public void grantNoticeComment(long memberSeq, long noticeBoardSeq, int commentCountBeforeThisInsert) {
        if (memberSeq <= 0 || noticeBoardSeq <= 0) {
            return;
        }
        if (commentCountBeforeThisInsert <= 0) {
            long pts = pointPolicyResolver.reward(
                    PointPolicyKeys.NOTICE_COMMENT_FIRST, WalletPointRules.COMMENT_FIRST_ON_POST);
            if (pts <= 0) {
                return;
            }
            credit(
                    memberSeq,
                    WalletPointRules.REASON_NOTICE_COMMENT_FIRST,
                    String.format("공지 댓글 최초 작성 (noticeSeq=%d)", noticeBoardSeq),
                    pts
            );
            return;
        }
        int cap = pointPolicyResolver.cap(
                PointPolicyKeys.NOTICE_COMMENT_EXTRA, (int) WalletPointRules.COMMENT_EXTRA_CAP_PER_POST);
        if (cap <= 0) {
            return;
        }
        Integer extra = memberPointCommentExtraMapper.selectExtraPointsEarned(memberSeq, POST_NOTICE, noticeBoardSeq);
        int earned = extra != null ? extra : 0;
        if (earned >= cap) {
            return;
        }
        long room = cap - earned;
        long grant = Math.min(room, cap);
        if (grant <= 0) {
            return;
        }
        memberPointCommentExtraMapper.upsertExtraPoints(memberSeq, POST_NOTICE, noticeBoardSeq, (int) grant);
        credit(
                memberSeq,
                WalletPointRules.REASON_NOTICE_COMMENT_EXTRA,
                String.format("공지 댓글 추가 적립 (noticeSeq=%d)", noticeBoardSeq),
                grant
        );
    }
}
