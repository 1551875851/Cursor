package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.system.model.SysRole;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleService {

    private final JdbcTemplate jdbcTemplate;
    private final SysMenuService sysMenuService;

    public SysRoleService(JdbcTemplate jdbcTemplate, SysMenuService sysMenuService) {
        this.jdbcTemplate = jdbcTemplate;
        this.sysMenuService = sysMenuService;
    }

    public List<SysRole> listAll() {
        return jdbcTemplate.query(
                "SELECT id, role_code AS roleCode, role_name AS roleName, status, remark, "
                        + "created_at AS createdAt, updated_at AS updatedAt FROM sys_role ORDER BY id ASC",
                new BeanPropertyRowMapper<SysRole>(SysRole.class));
    }

    public SysRole getById(Long id) {
        List<SysRole> list = jdbcTemplate.query(
                "SELECT id, role_code AS roleCode, role_name AS roleName, status, remark, "
                        + "created_at AS createdAt, updated_at AS updatedAt FROM sys_role WHERE id = ?",
                new BeanPropertyRowMapper<SysRole>(SysRole.class),
                id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Long create(SysRole role) {
        String now = DateTimeUtils.now();
        jdbcTemplate.update(
                "INSERT INTO sys_role (role_code, role_name, status, remark, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
                role.getRoleCode(),
                role.getRoleName(),
                role.getStatus() == null ? 1 : role.getStatus(),
                role.getRemark(),
                now,
                now);
        return jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
    }

    public void update(SysRole role) {
        jdbcTemplate.update(
                "UPDATE sys_role SET role_code=?, role_name=?, status=?, remark=?, updated_at=? WHERE id=?",
                role.getRoleCode(),
                role.getRoleName(),
                role.getStatus(),
                role.getRemark(),
                DateTimeUtils.now(),
                role.getId());
    }

    public void delete(Long id) {
        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_user_role WHERE role_id = ?", Integer.class, id);
        if (userCount != null && userCount > 0) {
            throw new IllegalStateException("角色已分配用户，无法删除");
        }
        jdbcTemplate.update("DELETE FROM sys_role_menu WHERE role_id = ?", id);
        jdbcTemplate.update("DELETE FROM sys_role WHERE id = ?", id);
    }

    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        sysMenuService.saveRoleMenus(roleId, menuIds);
    }

    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return sysMenuService.listMenuIdsByRoleId(roleId);
    }
}
