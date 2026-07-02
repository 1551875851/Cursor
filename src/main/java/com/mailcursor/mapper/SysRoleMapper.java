package com.mailcursor.mapper;

import com.mailcursor.system.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {

    List<SysRole> selectAll();

    SysRole selectById(@Param("id") Long id);

    int insert(SysRole role);

    int update(SysRole role);

    int countUsersByRoleId(@Param("roleId") Long roleId);

    int deleteRoleMenusByRoleId(@Param("roleId") Long roleId);

    int deleteById(@Param("id") Long id);
}
