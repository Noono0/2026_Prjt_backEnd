package com.noonoo.prjtbackend.sitesupport.mapper;

import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportDto;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSaveRequest;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSearchCondition;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteSupportMapper {

    List<SiteSupportDto> searchList(SiteSupportSearchCondition condition);

    long searchListCnt(SiteSupportSearchCondition condition);

    SiteSupportDto findDetail(Long supportSeq);

    List<SiteSupportDto> findActiveForSite();

    int insert(SiteSupportSaveRequest request);

    int update(SiteSupportSaveRequest request);

    int softDelete(SiteSupportSaveRequest request);
}
