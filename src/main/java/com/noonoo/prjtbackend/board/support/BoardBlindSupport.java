package com.noonoo.prjtbackend.board.support;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSearchCondition;
import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.board.dto.BoardSearchCondition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BoardBlindSupport {

    @Value("${app.board.blind-report-threshold:10}")
    private int blindReportThreshold;

    @Value("${app.board.blind-list-title:다수의 신고로 블라인드 처리된 게시글 입니다.}")
    private String blindListTitle;

    /** 목록·댓글 영역에 노출할 HTML (신고 누적 시 본문 치환) */
    @Value("${app.board.blind-comment-content:<p>다수의 신고로 블라인드 처리된 댓글입니다.</p>}")
    private String blindCommentContentHtml;

    public boolean isBlind(BoardDto b) {
        if (b == null) {
            return false;
        }
        return isBlindByReportCount(b.getReportCount());
    }

    /** 블랙리스트 제보 등 신고 수만으로 블라인드 판단 */
    public boolean isBlacklistReportBlind(BlacklistReportDto b) {
        if (b == null) {
            return false;
        }
        return isBlindByReportCount(b.getReportCount());
    }

    public boolean isBlindByReportCount(Long reportCount) {
        long rc = reportCount != null ? reportCount : 0L;
        return rc >= blindReportThreshold;
    }

    /** 목록 조회 전에만 호출: 신고 수 기준으로 제목·본문 치환용 파라미터 */
    public void applyBlindParams(BoardSearchCondition condition) {
        if (condition == null) {
            return;
        }
        condition.setBlindReportThreshold(blindReportThreshold);
        condition.setBlindListTitle(blindListTitle);
    }

    public void applyBlacklistReportBlindParams(BlacklistReportSearchCondition condition) {
        if (condition == null) {
            return;
        }
        condition.setBlindReportThreshold(blindReportThreshold);
        condition.setBlindListTitle(blindListTitle);
    }

    public int getBlindReportThreshold() {
        return blindReportThreshold;
    }

    /** 자유게시판·공지 댓글/답글 신고 누적 블라인드 */
    public boolean isCommentBlind(BoardCommentDto c) {
        if (c == null) {
            return false;
        }
        long rc = c.getReportCount() != null ? c.getReportCount() : 0L;
        return rc >= blindReportThreshold;
    }

    /** MyBatis 댓글 목록 쿼리용 치환 본문 */
    public String getBlindCommentMaskedContentHtml() {
        return blindCommentContentHtml;
    }
}
