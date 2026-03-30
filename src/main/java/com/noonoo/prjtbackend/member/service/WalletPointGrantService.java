package com.noonoo.prjtbackend.member.service;

/**
 * 게시판·댓글·가입 등 활동 포인트 지급 (내부 서비스).
 */
public interface WalletPointGrantService {

    void grantSignup(long memberSeq);

    /** 자유게시판 글 등록 성공 시 (카테고리 무관, app.wallet.free-board-post-points 만큼) */
    void grantFreeBoardPost(long memberSeq, long boardSeq, String categoryCode);

    /** 신고 누적으로 블라인드 처리된 게시글에 대해 글 작성 적립 포인트 1회 회수 */
    void deductFreeBoardPostOnBlind(long memberSeq, long boardSeq);

    /** 신고 누적으로 블라인드 처리된 댓글에 대해 댓글 적립분과 동일 규칙으로 1회 회수(금액은 글 블라인드와 동일 설정 사용) */
    void deductCommentRewardOnBlind(long memberSeq, long commentSeq);

    void grantBoardComment(long memberSeq, long boardSeq, int commentCountBeforeThisInsert);

    void grantNoticeComment(long memberSeq, long noticeBoardSeq, int commentCountBeforeThisInsert);

    /** 시스템·정책 적립 (원장 reason_code 자유) — 성공 시 true */
    boolean creditPoints(long memberSeq, String reasonCode, String summary, long points);

    /** 포인트 차감 (원장 point_delta 음수) — 잔액 부족 시 false */
    boolean debitPoints(long memberSeq, String reasonCode, String summary, long points);
}
