package com.noonoo.prjtbackend.board.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardSaveRequest {
    private Long boardSeq;
    private String categoryCode;
    private String title;
    /** 등록·수정 시 태그 배열(JSON). 수정 시 null 이면 tag_list 유지. */
    private List<String> tags;
    /** MyBatis INSERT/UPDATE용 — service에서 tags로부터 설정 */
    private String tagList;
    private String content;

    private Long writerMemberSeq;
    private String writerName;
    private String secretYn;
    private String secretPassword;
    private String anonymousYn;

    private Long viewCount;
    private Long likeCount;
    private Long dislikeCount;
    private Long commentCount;
    private Long commentLikeCount;
    private Long commentReportCount;
    private Long reportCount;

    private String showYn;
    private String highlightYn;
    private String commentAllowedYn;
    private String replyAllowedYn;

    private String createDt;
    private String createId;
    private String createIp;

    private String modifyDt;
    private String modifyId;
    private String modifyIp;
}
