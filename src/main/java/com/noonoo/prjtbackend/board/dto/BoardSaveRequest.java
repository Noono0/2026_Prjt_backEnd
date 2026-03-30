package com.noonoo.prjtbackend.board.dto;

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
    private String content;

    private Long writerMemberSeq;
    private String writerName;

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
