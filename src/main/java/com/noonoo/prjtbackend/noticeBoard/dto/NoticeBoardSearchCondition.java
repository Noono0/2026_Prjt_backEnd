package com.noonoo.prjtbackend.noticeBoard.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class NoticeBoardSearchCondition extends PageRequest {
    private String categoryCode;
    private String title;
    private String writerName;
    private String keyword;
    private String showYn;
    private String highlightYn;

    public NoticeBoardSearchCondition() {
        setSortBy("noticeBoardSeq");
        setSortDir("desc");
    }
}
