package com.noonoo.prjtbackend.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCommentSaveRequest {

    private Long boardCommentSeq;
    private Long boardSeq;
    private Long parentBoardCommentSeq;
    private Long writerMemberSeq;
    private String writerName;
    private String content;
    private Long emoticonSeq1;
    private Long emoticonSeq2;
    private Long emoticonSeq3;
    private String createId;
    private String createIp;
}
