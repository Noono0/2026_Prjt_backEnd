package com.noonoo.prjtbackend.board.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardDto {
    private Long boardSeq;
    private String categoryCode;
    private String categoryName;
    private String title;
    private String content;
    private Long writerMemberSeq;
    /** 회원 로그인 ID (member.member_id) */
    private String writerMemberId;
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
    /** Y/N, null 이면 허용으로 간주 */
    private String commentAllowedYn;
    /** Y/N, null 이면 허용으로 간주 */
    private String replyAllowedYn;
    private String createDt;
    private String modifyDt;
}
