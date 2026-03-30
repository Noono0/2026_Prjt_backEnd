package com.noonoo.prjtbackend.noticeBoard.service;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;

import java.util.List;

public interface NoticeBoardCommentService {

    List<BoardCommentDto> findComments(Long noticeBoardSeq, String sort);

    long createComment(BoardCommentSaveRequest request);

    int likeComment(Long noticeBoardSeq, Long commentSeq);

    int dislikeComment(Long noticeBoardSeq, Long commentSeq);

    int reportComment(Long noticeBoardSeq, Long commentSeq);

    int updateComment(Long noticeBoardSeq, Long commentSeq, BoardCommentUpdateRequest body);

    int deleteComment(Long noticeBoardSeq, Long commentSeq);
}
