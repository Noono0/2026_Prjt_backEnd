package com.noonoo.prjtbackend.menu.mapper;

import com.noonoo.prjtbackend.menu.dto.MenuDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<MenuDto> findMenusByRole(@Param("roleCode") String roleCode);
}
