package com.noonoo.prjtbackend.board.mapper;

import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.board.dto.BoardPopularCodeRawDto;
import com.noonoo.prjtbackend.board.dto.BoardSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<OptionDto> findBoardCategoryOptions();
    List<OptionDto> findCategoryOptionsByGroupId(@Param("groupId") String groupId);

    /**
     * 대분류 그룹 ID A0001(code_group) + 중분류 코드 ID A00017(code_detail).
     * 임계값은 ATTR1(우선), 없으면 CODE_VALUE / DESCRIPTION 중 숫자.
     */
    BoardPopularCodeRawDto findBoardPopularCodeDetail();

    List<BoardDto> findBoards(BoardSearchCondition condition);
    List<BoardDto> findInquiryBoards(BoardSearchCondition condition);

    long findBoardsCnt(BoardSearchCondition condition);
    long findInquiryBoardsCnt(BoardSearchCondition condition);

    BoardDto findBoardById(Long boardSeq);
    BoardDto findInquiryBoardById(Long boardSeq);

    int insertBoard(BoardSaveRequest condition);

    /** 동일 커넥션·트랜잭션에서 직전 INSERT 의 자동 증가 키 (boardSeq 미채움 시 보정용) */
    @Select("SELECT LAST_INSERT_ID()")
    long selectLastInsertId();

    int updateBoard(BoardSaveRequest condition);

    int deleteBoard(Long boardSeq);

    int increaseBoardViewCount(Long boardSeq);

    int increaseBoardLikeCount(Long boardSeq);

    int increaseBoardDislikeCount(Long boardSeq);

    int increaseBoardReportCount(Long boardSeq);

    int increaseBoardCommentCount(@Param("boardSeq") Long boardSeq);

    int adjustBoardCommentCount(@Param("boardSeq") Long boardSeq, @Param("delta") long delta);

    int insertBoardActionLog(@Param("boardKind") String boardKind,
                             @Param("targetKind") String targetKind,
                             @Param("targetSeq") Long targetSeq,
                             @Param("actionType") String actionType,
                             @Param("memberSeq") Long memberSeq,
                             @Param("memberId") String memberId,
                             @Param("clientIp") String clientIp);
}
