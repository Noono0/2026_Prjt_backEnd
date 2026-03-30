package com.noonoo.prjtbackend.blacklistreport.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BlacklistReportSearchCondition extends PageRequest {

    /** 제목·내용 검색 */
    private String keyword;
    /** 블랙리스트 아이디 부분 일치 */
    private String blacklistTargetId;

    /** 작성일 기간 (포함), 형식 yyyy-MM-dd */
    private String createDtFrom;
    /** 작성일 기간 (포함), 형식 yyyy-MM-dd */
    private String createDtTo;

    /** 목록 검색: 조회 칩(A0006)의 code_value 또는 일반 필터 */
    private String categoryCode;

    /** 추천 수 이상 (인기 탭 등에서 설정) */
    private Integer minLikeCount;

    /** MyBatis 목록 블라인드 처리용 */
    private Integer blindReportThreshold;
    private String blindListTitle;

    public BlacklistReportSearchCondition() {
        setSortBy("blacklistReportSeq");
        setSortDir("desc");
    }
}
