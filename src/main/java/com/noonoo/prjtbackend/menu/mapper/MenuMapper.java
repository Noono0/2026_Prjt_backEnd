package com.noonoo.prjtbackend.menu.mapper;

import com.noonoo.prjtbackend.menu.dto.MenuDto;
import com.noonoo.prjtbackend.menu.dto.MenuSaveRequest;
import com.noonoo.prjtbackend.menu.dto.MenuSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<MenuDto> findMenusByRole(@Param("roleCode") String roleCode);

    /** 트리 UI용: 페이징 없이 전체 메뉴 */
    List<MenuDto> selectAllForTree();

    long selectListCnt(MenuSearchCondition condition);

    List<MenuDto> selectList(MenuSearchCondition condition);

    MenuDto selectDetail(MenuSearchCondition condition);

    long countByMenuCode(@Param("menuCode") String menuCode, @Param("excludeMenuId") Long excludeMenuId);

    int insertData(MenuSaveRequest request);

    int updateData(MenuSaveRequest request);

    int deleteData(MenuSaveRequest request);
}
