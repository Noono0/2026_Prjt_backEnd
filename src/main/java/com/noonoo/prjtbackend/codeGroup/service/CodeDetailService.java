package com.noonoo.prjtbackend.codeGroup.service;

import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSearchCondition;
import com.noonoo.prjtbackend.common.paging.PageResponse;

import java.util.Map;

public interface CodeDetailService {

    PageResponse<CodeDetailDto> selectList(CodeDetailSearchCondition condition);

    CodeDetailDto selectDetail(CodeDetailSearchCondition condition);

    int insertData(CodeDetailSaveRequest condition);

    int updateData(CodeDetailSaveRequest condition);

    int deleteData(CodeDetailSaveRequest condition);
}