package com.noonoo.prjtbackend.noticeBoard.service;

import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardDto;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSaveRequest;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSearchCondition;

import java.util.List;

public interface NoticeBoardService {

    List<OptionDto> findNoticeCategoryOptions();

    PageResponse<NoticeBoardDto> findNoticeBoards(NoticeBoardSearchCondition condition);

    NoticeBoardDto findNoticeBoardDetail(Long noticeBoardSeq);

    /** 자유게시판 목록 상단 고정용(BOARD 메뉴 조회 권한으로 노출) */
    List<NoticeBoardDto> findNoticeBoardsPinnedOnFreeBoard();

    int createNoticeBoard(NoticeBoardSaveRequest condition);

    int updateNoticeBoard(NoticeBoardSaveRequest condition);

    int deleteNoticeBoard(Long noticeBoardSeq);

    int increaseViewCount(Long noticeBoardSeq);

    int likeNoticeBoard(Long noticeBoardSeq);

    int dislikeNoticeBoard(Long noticeBoardSeq);

    int reportNoticeBoard(Long noticeBoardSeq);
}
