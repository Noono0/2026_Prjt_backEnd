package com.noonoo.prjtbackend.board.mapper;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardCommentMapper {

    List<BoardCommentDto> findBoardCommentsFlat(@Param("boardSeq") Long boardSeq,
                                                 @Param("currentMemberSeq") Long currentMemberSeq,
                                                 @Param("blindReportThreshold") int blindReportThreshold,
                                                 @Param("blindCommentMaskedContent") String blindCommentMaskedContent);

    BoardCommentDto findBoardCommentById(@Param("boardCommentSeq") Long boardCommentSeq);

    int insertBoardComment(BoardCommentSaveRequest row);

    String findVoteType(@Param("boardCommentSeq") Long boardCommentSeq,
                        @Param("memberSeq") Long memberSeq);

    int insertVote(@Param("boardCommentSeq") Long boardCommentSeq,
                   @Param("memberSeq") Long memberSeq,
                   @Param("voteType") String voteType);

    int updateVoteType(@Param("boardCommentSeq") Long boardCommentSeq,
                       @Param("memberSeq") Long memberSeq,
                       @Param("voteType") String voteType);

    int adjustLikeDislike(@Param("boardCommentSeq") Long boardCommentSeq,
                          @Param("deltaLike") long deltaLike,
                          @Param("deltaDislike") long deltaDislike);

    int insertReportIgnore(@Param("boardCommentSeq") Long boardCommentSeq,
                           @Param("memberSeq") Long memberSeq);

    int increaseReportCount(@Param("boardCommentSeq") Long boardCommentSeq);

    int updateBoardComment(@Param("boardSeq") Long boardSeq,
                           @Param("boardCommentSeq") Long boardCommentSeq,
                           @Param("writerMemberSeq") Long writerMemberSeq,
                           @Param("body") BoardCommentUpdateRequest body);

    /** 루트 댓글과 그 1단 답글까지 숨김 */
    int softDeleteBoardCommentThread(@Param("boardSeq") Long boardSeq,
                                     @Param("rootCommentSeq") Long rootCommentSeq);

    /** 단일 댓글(답글) 숨김 */
    int softDeleteBoardCommentRow(@Param("boardSeq") Long boardSeq,
                                  @Param("boardCommentSeq") Long boardCommentSeq);

    /** 해당 회원이 이 글에 남긴 댓글 수(삽입 직전 조회용, 본인 댓글만) */
    int countCommentsByBoardAndMember(@Param("boardSeq") long boardSeq, @Param("memberSeq") long memberSeq);

    /** 작성자 기준: ids 에 포함된 이모티콘이 모두 본인 소유인 개수 */
    int countEmoticonsForMember(@Param("memberSeq") Long memberSeq,
                                @Param("ids") java.util.List<Long> ids);
}
