package com.noonoo.prjtbackend.noticeBoard.mapper;

import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardDto;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSaveRequest;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeBoardMapper {

    List<OptionDto> findNoticeCategoryOptions();

    List<NoticeBoardDto> findNoticeBoards(NoticeBoardSearchCondition condition);

    long findNoticeBoardsCnt(NoticeBoardSearchCondition condition);

    NoticeBoardDto findNoticeBoardById(Long noticeBoardSeq);

    List<NoticeBoardDto> findNoticeBoardsPinnedOnFreeBoard();

    int insertNoticeBoard(NoticeBoardSaveRequest condition);

    int updateNoticeBoard(NoticeBoardSaveRequest condition);

    int deleteNoticeBoard(Long noticeBoardSeq);

    int increaseNoticeBoardViewCount(Long noticeBoardSeq);

    int increaseNoticeBoardLikeCount(Long noticeBoardSeq);

    int increaseNoticeBoardDislikeCount(Long noticeBoardSeq);

    int increaseNoticeBoardReportCount(Long noticeBoardSeq);

    int increaseNoticeBoardCommentCount(Long noticeBoardSeq);

    int adjustNoticeBoardCommentCount(@Param("noticeBoardSeq") Long noticeBoardSeq, @Param("delta") long delta);

    int insertNoticeBoardActionLog(@Param("boardKind") String boardKind,
                                   @Param("targetKind") String targetKind,
                                   @Param("targetSeq") Long targetSeq,
                                   @Param("actionType") String actionType,
                                   @Param("memberSeq") Long memberSeq,
                                   @Param("memberId") String memberId,
                                   @Param("clientIp") String clientIp);
}
