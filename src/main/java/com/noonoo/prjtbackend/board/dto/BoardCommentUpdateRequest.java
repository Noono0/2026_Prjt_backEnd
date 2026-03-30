package com.noonoo.prjtbackend.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCommentUpdateRequest {

    private String content;
    private Long emoticonSeq1;
    private Long emoticonSeq2;
    private Long emoticonSeq3;
}
