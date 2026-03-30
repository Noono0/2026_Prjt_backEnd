package com.noonoo.prjtbackend.blacklistreport.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BlacklistReportSaveRequest {
    private Long blacklistReportSeq;
    private String blacklistTargetId;
    private String title;
    private String content;

    private String categoryCode;
    private String commentAllowedYn;
    private String replyAllowedYn;

    private Long writerMemberSeq;
    private String writerName;

    private Long viewCount;
    private Long likeCount;
    private Long dislikeCount;
    private Long commentCount;
    private Long reportCount;

    private String createId;
    private String createIp;
    private String modifyId;
    private String modifyIp;
}
