package com.mailcursor.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemBootstrapMapper {

    int countUsers();

    int countMenuByPath(@Param("path") String path);

    Long selectSystemMenuId();

    List<Long> selectAllRoleIds();

    int countRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    int bumpRootMenuSortOrder(@Param("excludeMenuId") Long excludeMenuId);
}
