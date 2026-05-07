package com.noonoo.prjtbackend.gamniverseprofile.mapper;

import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileDto;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSaveRequest;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSearchCondition;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GamniverseProfileMapper {
    long selectListCnt(GamniverseProfileSearchCondition condition);

    List<GamniverseProfileDto> selectList(GamniverseProfileSearchCondition condition);

    GamniverseProfileDto selectById(@Param("gamniverseProfileSeq") Long gamniverseProfileSeq);

    int insertProfile(GamniverseProfileSaveRequest request);

    int updateProfile(GamniverseProfileSaveRequest request);

    int softDelete(@Param("gamniverseProfileSeq") Long gamniverseProfileSeq);
}
