package com.mailcursor.mapper;

import com.mailcursor.system.model.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuMapper {

    List<SysMenu> selectAll();

    List<SysMenu> selectByUserId(@Param("userId") Long userId);

    SysMenu selectById(@Param("id") Long id);

    int insert(SysMenu menu);

    int update(SysMenu menu);

    int countChildren(@Param("id") Long id);

    int deleteById(@Param("id") Long id);

    int deleteRoleMenusByMenuId(@Param("menuId") Long menuId);

    int deleteRoleMenusByRoleId(@Param("roleId") Long roleId);

    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
