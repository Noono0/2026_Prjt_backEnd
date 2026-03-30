package com.noonoo.prjtbackend.board.service;

import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.board.dto.BoardPopularConfigDto;
import com.noonoo.prjtbackend.board.dto.BoardSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.paging.PageResponse;

import java.util.List;

public interface BoardService {
    List<OptionDto> findBoardCategoryOptions();

    /** 자유게시판 인기글 임계값·뱃지 문구 (대분류 그룹 A0001 + 중분류 code_id A00017, 없으면 기본 50·「인기글」) */
    BoardPopularConfigDto getBoardPopularConfig();

    PageResponse<BoardDto> findBoards(BoardSearchCondition condition);

    BoardDto findBoardDetail(Long boardSeq);

    int createBoard(BoardSaveRequest condition);

    int updateBoard(BoardSaveRequest condition);

    int deleteBoard(Long boardSeq);

    /** 작성자 본인만 소프트 삭제(use_yn = N). */
    int deleteBoardIfWriter(Long boardSeq);

    int increaseViewCount(Long boardSeq);

    int likeBoard(Long boardSeq);

    int dislikeBoard(Long boardSeq);

    int reportBoard(Long boardSeq);
}
