package com.noonoo.prjtbackend.blacklistreport.mapper;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlacklistReportCommentMapper {

    List<BoardCommentDto> findBlacklistReportCommentsFlat(@Param("blacklistReportSeq") Long blacklistReportSeq,
                                                         @Param("currentMemberSeq") Long currentMemberSeq,
                                                         @Param("blindReportThreshold") int blindReportThreshold,
                                                         @Param("blindCommentMaskedContent") String blindCommentMaskedContent);

    BoardCommentDto findBlacklistReportCommentById(@Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq);

    int insertBlacklistReportComment(BoardCommentSaveRequest row);

    String findVoteType(@Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq,
                        @Param("memberSeq") Long memberSeq);

    int insertVote(@Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq,
                   @Param("memberSeq") Long memberSeq,
                   @Param("voteType") String voteType);

    int updateVoteType(@Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq,
                       @Param("memberSeq") Long memberSeq,
                       @Param("voteType") String voteType);

    int adjustLikeDislike(@Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq,
                          @Param("deltaLike") long deltaLike,
                          @Param("deltaDislike") long deltaDislike);

    int insertReportIgnore(@Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq,
                           @Param("memberSeq") Long memberSeq);

    int increaseReportCount(@Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq);

    int updateBlacklistReportComment(@Param("blacklistReportSeq") Long blacklistReportSeq,
                                     @Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq,
                                     @Param("writerMemberSeq") Long writerMemberSeq,
                                     @Param("body") BoardCommentUpdateRequest body);

    int softDeleteBlacklistReportCommentThread(@Param("blacklistReportSeq") Long blacklistReportSeq,
                                               @Param("rootCommentSeq") Long rootCommentSeq);

    int softDeleteBlacklistReportCommentRow(@Param("blacklistReportSeq") Long blacklistReportSeq,
                                            @Param("blacklistReportCommentSeq") Long blacklistReportCommentSeq);

    int countCommentsByBlacklistReportAndMember(@Param("blacklistReportSeq") long blacklistReportSeq,
                                                @Param("memberSeq") long memberSeq);

    int countEmoticonsForMember(@Param("memberSeq") Long memberSeq,
                                @Param("ids") java.util.List<Long> ids);
}
