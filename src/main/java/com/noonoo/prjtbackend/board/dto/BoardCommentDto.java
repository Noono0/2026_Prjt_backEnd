package com.noonoo.prjtbackend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCommentDto {

    private Long boardCommentSeq;
    private Long boardSeq;
    private Long parentBoardCommentSeq;
    private Long writerMemberSeq;
    private String writerName;
    /** 회원 로그인 아이디 (표시용) */
    private String writerMemberId;
    private String writerProfileImageUrl;
    private String content;
    private Long emoticonSeq1;
    private Long emoticonSeq2;
    private Long emoticonSeq3;
    private String emoticonImageUrl1;
    private String emoticonImageUrl2;
    private String emoticonImageUrl3;
    private Long likeCount;
    private Long dislikeCount;
    private Long reportCount;
    private Long replyCount;
    /** 로그인 회원 기준: L=추천, D=비추천, 없으면 null */
    private String myVoteType;
    private String createDt;

    /** 목록 API에서만 채움: 대댓글 (최대 1단) */
    @Builder.Default
    private List<BoardCommentDto> children = new ArrayList<>();
}
