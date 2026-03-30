package com.noonoo.prjtbackend.blacklistreport.mapper;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistPopularLikeRuleRow;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSaveRequest;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlacklistReportMapper {

    List<OptionDto> findBlacklistCategoryOptions();

    /** 목록·조회 필터 칩용 (code_group_id A0006) */
    List<OptionDto> findBlacklistListCategoryOptions();

    List<BlacklistPopularLikeRuleRow> findBlacklistPopularLikeRules();

    List<BlacklistReportDto> findBlacklistReports(BlacklistReportSearchCondition condition);

    long findBlacklistReportsCnt(BlacklistReportSearchCondition condition);

    BlacklistReportDto findById(@Param("blacklistReportSeq") Long blacklistReportSeq);

    int insertBlacklistReport(BlacklistReportSaveRequest request);

    @Select("SELECT LAST_INSERT_ID()")
    long selectLastInsertId();

    int updateBlacklistReport(BlacklistReportSaveRequest request);

    int deleteBlacklistReport(@Param("blacklistReportSeq") Long blacklistReportSeq);

    int increaseViewCount(@Param("blacklistReportSeq") Long blacklistReportSeq);

    int increaseBlacklistReportLikeCount(@Param("blacklistReportSeq") Long blacklistReportSeq);

    int increaseBlacklistReportDislikeCount(@Param("blacklistReportSeq") Long blacklistReportSeq);

    int increaseBlacklistReportReportCount(@Param("blacklistReportSeq") Long blacklistReportSeq);

    int increaseBlacklistReportCommentCount(@Param("blacklistReportSeq") Long blacklistReportSeq);

    int adjustBlacklistReportCommentCount(@Param("blacklistReportSeq") Long blacklistReportSeq, @Param("delta") int delta);

    int insertBlacklistReportActionLog(@Param("boardKind") String boardKind,
                                         @Param("targetKind") String targetKind,
                                         @Param("targetSeq") Long targetSeq,
                                         @Param("actionType") String actionType,
                                         @Param("memberSeq") Long memberSeq,
                                         @Param("memberId") String memberId,
                                         @Param("clientIp") String clientIp);

    /** 엑셀용 (상한) */
    List<BlacklistReportDto> findBlacklistReportsForExport(BlacklistReportSearchCondition condition);
}
