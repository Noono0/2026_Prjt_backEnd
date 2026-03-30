package com.noonoo.prjtbackend.board.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BoardDetailDto {
    private Long postSeq;
    private String boardType;
    private String categoryCode;
    private String title;
    private String content;
    private Long writerMemberSeq;
    private String writerName;
    private Long viewCount;
    private Long recommendCount;
    private Long commentCount;
    private String showYn;
    private String highlightYn;
    private LocalDateTime createDt;
    private String createIp;
    private String createId;
    private LocalDateTime modifyDt;
    private String modifyIp;
    private String modifyId;
}
