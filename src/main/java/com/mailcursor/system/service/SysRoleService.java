package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.mapper.SysRoleMapper;
import com.mailcursor.system.model.SysRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysMenuService sysMenuService;

    public SysRoleService(SysRoleMapper sysRoleMapper, SysMenuService sysMenuService) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuService = sysMenuService;
    }

    public List<SysRole> listAll() {
        return sysRoleMapper.selectAll();
    }

    public SysRole getById(Long id) {
        return sysRoleMapper.selectById(id);
    }

    public Long create(SysRole role) {
        String now = DateTimeUtils.now();
        role.setStatus(role.getStatus() == null ? 1 : role.getStatus());
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        sysRoleMapper.insert(role);
        return role.getId();
    }

    public void update(SysRole role) {
        role.setUpdatedAt(DateTimeUtils.now());
        sysRoleMapper.update(role);
    }

    public void delete(Long id) {
        if (sysRoleMapper.countUsersByRoleId(id) > 0) {
            throw new IllegalStateException("角色已分配用户，无法删除");
        }
        sysRoleMapper.deleteRoleMenusByRoleId(id);
        sysRoleMapper.deleteById(id);
    }

    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        sysMenuService.saveRoleMenus(roleId, menuIds);
    }

    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return sysMenuService.listMenuIdsByRoleId(roleId);
    }
}
