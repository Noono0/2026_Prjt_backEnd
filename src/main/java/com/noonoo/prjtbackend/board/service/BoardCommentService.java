package com.noonoo.prjtbackend.board.service;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;

import java.util.List;

public interface BoardCommentService {

    List<BoardCommentDto> findComments(Long boardSeq, String sort);

    long createComment(BoardCommentSaveRequest request);

    int likeComment(Long boardSeq, Long commentSeq);

    int dislikeComment(Long boardSeq, Long commentSeq);

    int reportComment(Long boardSeq, Long commentSeq);

    int updateComment(Long boardSeq, Long commentSeq, BoardCommentUpdateRequest body);

    int deleteComment(Long boardSeq, Long commentSeq);
}
