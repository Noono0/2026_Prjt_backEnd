package com.noonoo.prjtbackend.noticeBoard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeBoardDto {
    private Long noticeBoardSeq;
    private String categoryCode;
    private String categoryName;
    private String title;
    private String content;
    private Long writerMemberSeq;
    private String writerName;
    private String writerProfileImageUrl;
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
    /** Y면 자유게시판 목록 상단에 고정 노출 */
    private String pinOnFreeBoardYn;
    private String createDt;
    private String modifyDt;
}
