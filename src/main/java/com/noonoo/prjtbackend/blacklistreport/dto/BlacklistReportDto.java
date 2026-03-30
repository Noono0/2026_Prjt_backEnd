package com.noonoo.prjtbackend.blacklistreport.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BlacklistReportDto {
    private Long blacklistReportSeq;
    private String blacklistTargetId;
    private String title;
    private String content;
    private Long writerMemberSeq;
    /** 회원 로그인 아이디 (목록·작성자 메뉴) */
    private String writerMemberId;
    private String writerProfileImageUrl;
    private String writerName;
    private String categoryCode;
    private String categoryName;
    private Long viewCount;
    private Long likeCount;
    private Long dislikeCount;
    private Long commentCount;
    private Long reportCount;
    private String commentAllowedYn;
    private String replyAllowedYn;
    private String createDt;
    private String modifyDt;
}
