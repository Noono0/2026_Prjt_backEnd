package com.noonoo.prjtbackend.codeGroup.service;

import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.paging.PageResponse;

import java.util.List;
import java.util.Map;

public interface CodeGroupService {

    PageResponse<CodeGroupDto> selectList(CodeGroupSearchCondition condition);

    CodeGroupDto selectDetail(Long codeGroupSeq);

    CodeGroupDto findIdCheck(CodeGroupSaveRequest condition);

    Map<String, Object> insertData(CodeGroupSaveRequest condition);

    Map<String, Object> updateData(CodeGroupSaveRequest condition);

    Map<String, Object> deleteData(Long codeGroupSeq);

    List<OptionDto> findCodeGroupOptions();
}