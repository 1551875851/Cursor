package com.mailcursor.mapper;

import com.mailcursor.system.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {

    List<SysUser> selectAll();

    SysUser selectById(@Param("id") Long id);

    SysUser selectByUsername(@Param("username") String username);

    int insert(SysUser user);

    int updateWithPassword(SysUser user);

    int updateWithoutPassword(SysUser user);

    int deleteById(@Param("id") Long id);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    int deleteUserRolesByUserId(@Param("userId") Long userId);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
