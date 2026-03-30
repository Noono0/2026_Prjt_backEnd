package com.noonoo.prjtbackend.board.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BoardSearchCondition extends PageRequest {
    private String categoryCode;
    private String title;
    private String writerName;
    private String keyword;
    private String showYn;
    private String highlightYn;

    /**
     * 서버 전용: 인기글 탭(공통코드 A00017의 code_value와 동일한 categoryCode 요청 시 설정).
     * {@code IFNULL(like_count,0) >= minLikeCount}
     */
    private Integer minLikeCount;

    /** 서버 전용: 목록 SQL에서 신고 수 임계 이상일 때 제목·내용 치환 */
    private Integer blindReportThreshold;
    private String blindListTitle;

    public BoardSearchCondition() {
        setSortBy("boardSeq");
        setSortDir("desc");
    }
}
