package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.mapper.SysUserMapper;
import com.mailcursor.system.model.SysUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SysUserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public List<SysUser> listAll() {
        return sysUserMapper.selectAll();
    }

    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }

    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    public Long create(SysUser user, List<Long> roleIds) {
        String now = DateTimeUtils.now();
        String encodedPassword = passwordEncoder.encode(
                StringUtils.hasText(user.getPassword()) ? user.getPassword() : "123456");
        user.setPassword(encodedPassword);
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        user.setIsSuperAdmin(user.getIsSuperAdmin() == null ? 0 : user.getIsSuperAdmin());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        sysUserMapper.insert(user);
        saveUserRoles(user.getId(), roleIds);
        return user.getId();
    }

    public void update(SysUser user, List<Long> roleIds) {
        user.setUpdatedAt(DateTimeUtils.now());
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            sysUserMapper.updateWithPassword(user);
        } else {
            sysUserMapper.updateWithoutPassword(user);
        }
        saveUserRoles(user.getId(), roleIds);
    }

    public void delete(Long id) {
        SysUser user = getById(id);
        if (user != null && user.getIsSuperAdmin() != null && user.getIsSuperAdmin() == 1) {
            throw new IllegalStateException("超级管理员不能删除");
        }
        sysUserMapper.deleteUserRolesByUserId(id);
        sysUserMapper.deleteById(id);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public List<Long> listRoleIdsByUserId(Long userId) {
        return sysUserMapper.selectRoleIdsByUserId(userId);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        sysUserMapper.deleteUserRolesByUserId(userId);
        if (roleIds == null) {
            return;
        }
        for (Long roleId : roleIds) {
            if (roleId != null) {
                sysUserMapper.insertUserRole(userId, roleId);
            }
        }
    }
}
