package com.noonoo.prjtbackend.blacklistreport.service;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSaveRequest;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.paging.PageResponse;

import java.util.List;

public interface BlacklistReportService {

    List<OptionDto> findBlacklistCategoryOptions();

    /** 목록 조회 필터 칩 (A0006) */
    List<OptionDto> findBlacklistListCategoryOptions();

    PageResponse<BlacklistReportDto> findBlacklistReports(BlacklistReportSearchCondition condition);

    BlacklistReportDto findDetail(Long blacklistReportSeq);

    int create(BlacklistReportSaveRequest request);

    int update(BlacklistReportSaveRequest request);

    int deleteIfWriter(Long blacklistReportSeq);

    int increaseViewCount(Long blacklistReportSeq);

    int likeBlacklistReport(Long blacklistReportSeq);

    int dislikeBlacklistReport(Long blacklistReportSeq);

    int reportBlacklistReport(Long blacklistReportSeq);

    byte[] exportExcel(String blacklistTargetId, String keyword, String createDtFrom, String createDtTo) throws Exception;
}
