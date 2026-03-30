package com.noonoo.prjtbackend.noticeBoard.mapper;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeBoardCommentMapper {

    List<BoardCommentDto> findNoticeBoardCommentsFlat(@Param("noticeBoardSeq") Long noticeBoardSeq,
                                                       @Param("currentMemberSeq") Long currentMemberSeq,
                                                       @Param("blindReportThreshold") int blindReportThreshold,
                                                       @Param("blindCommentMaskedContent") String blindCommentMaskedContent);

    BoardCommentDto findNoticeBoardCommentById(@Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq);

    int insertNoticeBoardComment(BoardCommentSaveRequest row);

    String findVoteType(@Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq,
                        @Param("memberSeq") Long memberSeq);

    int insertVote(@Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq,
                   @Param("memberSeq") Long memberSeq,
                   @Param("voteType") String voteType);

    int updateVoteType(@Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq,
                       @Param("memberSeq") Long memberSeq,
                       @Param("voteType") String voteType);

    int adjustLikeDislike(@Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq,
                          @Param("deltaLike") long deltaLike,
                          @Param("deltaDislike") long deltaDislike);

    int insertReportIgnore(@Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq,
                           @Param("memberSeq") Long memberSeq);

    int increaseReportCount(@Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq);

    int updateNoticeBoardComment(@Param("noticeBoardSeq") Long noticeBoardSeq,
                                 @Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq,
                                 @Param("writerMemberSeq") Long writerMemberSeq,
                                 @Param("body") BoardCommentUpdateRequest body);

    int softDeleteNoticeBoardCommentThread(@Param("noticeBoardSeq") Long noticeBoardSeq,
                                           @Param("rootCommentSeq") Long rootCommentSeq);

    int softDeleteNoticeBoardCommentRow(@Param("noticeBoardSeq") Long noticeBoardSeq,
                                        @Param("noticeBoardCommentSeq") Long noticeBoardCommentSeq);

    int countEmoticonsForMember(@Param("memberSeq") Long memberSeq,
                                @Param("ids") List<Long> ids);

    int countCommentsByNoticeAndMember(@Param("noticeBoardSeq") long noticeBoardSeq, @Param("memberSeq") long memberSeq);
}
