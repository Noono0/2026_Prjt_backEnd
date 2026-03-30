package com.noonoo.prjtbackend.blacklistreport.service;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;

import java.util.List;

public interface BlacklistReportCommentService {

    List<BoardCommentDto> findComments(Long blacklistReportSeq, String sort);

    long createComment(BoardCommentSaveRequest request);

    int likeComment(Long blacklistReportSeq, Long commentSeq);

    int dislikeComment(Long blacklistReportSeq, Long commentSeq);

    int reportComment(Long blacklistReportSeq, Long commentSeq);

    int updateComment(Long blacklistReportSeq, Long commentSeq, BoardCommentUpdateRequest body);

    int deleteComment(Long blacklistReportSeq, Long commentSeq);
}
