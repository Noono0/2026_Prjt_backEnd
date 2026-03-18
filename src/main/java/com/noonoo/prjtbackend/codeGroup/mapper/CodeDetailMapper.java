package com.noonoo.prjtbackend.codeGroup.mapper;

import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CodeDetailMapper {

    List<CodeDetailDto> selectList(CodeDetailSearchCondition condition);

    long selectListCnt(CodeDetailSearchCondition condition);

    CodeDetailDto selectDetail(Long codeDetailSeq);

    CodeDetailDto findCodeDetailByCode(CodeDetailSaveRequest condition);

    int insertData(CodeDetailSaveRequest condition);

    int updateData(CodeDetailSaveRequest condition);

    int deleteData(Long codeDetailSeq);
}