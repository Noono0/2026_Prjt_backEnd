package com.noonoo.prjtbackend.codeGroup.service;

import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSearchCondition;
import com.noonoo.prjtbackend.common.paging.PageResponse;

import java.util.Map;

public interface CodeDetailService {

    PageResponse<CodeDetailDto> selectList(CodeDetailSearchCondition condition);

    CodeDetailDto selectDetail(Long codeDetailSeq);

    Map<String, Object> insertData(CodeDetailSaveRequest condition);

    Map<String, Object> updateData(CodeDetailSaveRequest condition);

    Map<String, Object> deleteData(Long codeDetailSeq);
}