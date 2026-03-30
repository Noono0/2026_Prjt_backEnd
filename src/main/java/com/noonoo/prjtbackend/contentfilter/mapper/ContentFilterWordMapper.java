package com.noonoo.prjtbackend.contentfilter.mapper;

import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordDto;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSaveRequest;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentFilterWordMapper {

    List<ContentFilterWordDto> selectAllActiveForFilter();

    long selectListCnt(ContentFilterWordSearchCondition condition);

    List<ContentFilterWordDto> selectList(ContentFilterWordSearchCondition condition);

    ContentFilterWordDto selectById(@Param("contentFilterWordSeq") Long contentFilterWordSeq);

    int insertWord(ContentFilterWordSaveRequest request);

    int updateWord(ContentFilterWordSaveRequest request);

    int softDelete(@Param("contentFilterWordSeq") Long contentFilterWordSeq);

    long countByCategoryAndKeywordIgnoreCase(
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("excludeSeq") Long excludeSeq
    );
}
