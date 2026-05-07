package com.noonoo.prjtbackend.codeGroup.mapper;

import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

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
