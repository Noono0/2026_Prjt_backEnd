package com.noonoo.prjtbackend.codeGroup.mapper;

import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CodeGroupsMapper {

    List<CodeGroupDto> selectList(CodeGroupSearchCondition condition);

    long selectListCnt(CodeGroupSearchCondition condition);

    CodeGroupDto selectDetail(CodeGroupSearchCondition condition);

    CodeGroupDto findIdCheck(CodeGroupSaveRequest condition);

    int insertData(CodeGroupSaveRequest condition);

    int updateData(CodeGroupSaveRequest condition);

    int deleteData(CodeGroupSaveRequest condition);

    List<OptionDto> findCodeGroupOptions();
}