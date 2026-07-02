package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.system.model.SysUser;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysUserService {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SysUserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SysUser> listAll() {
        return jdbcTemplate.query(
                "SELECT u.id, u.org_id AS orgId, u.username, u.password, u.nickname, u.status, "
                        + "u.is_super_admin AS isSuperAdmin, u.created_at AS createdAt, u.updated_at AS updatedAt, "
                        + "o.org_name AS orgName FROM sys_user u "
                        + "LEFT JOIN sys_org o ON u.org_id = o.id ORDER BY u.id ASC",
                new BeanPropertyRowMapper<SysUser>(SysUser.class));
    }

    public SysUser getById(Long id) {
        List<SysUser> list = jdbcTemplate.query(
                "SELECT u.id, u.org_id AS orgId, u.username, u.password, u.nickname, u.status, "
                        + "u.is_super_admin AS isSuperAdmin, u.created_at AS createdAt, u.updated_at AS updatedAt, "
                        + "o.org_name AS orgName FROM sys_user u "
                        + "LEFT JOIN sys_org o ON u.org_id = o.id WHERE u.id = ?",
                new BeanPropertyRowMapper<SysUser>(SysUser.class),
                id);
        return list.isEmpty() ? null : list.get(0);
    }

    public SysUser getByUsername(String username) {
        List<SysUser> list = jdbcTemplate.query(
                "SELECT u.id, u.org_id AS orgId, u.username, u.password, u.nickname, u.status, "
                        + "u.is_super_admin AS isSuperAdmin, u.created_at AS createdAt, u.updated_at AS updatedAt, "
                        + "o.org_name AS orgName FROM sys_user u "
                        + "LEFT JOIN sys_org o ON u.org_id = o.id WHERE u.username = ?",
                new BeanPropertyRowMapper<SysUser>(SysUser.class),
                username);
        return list.isEmpty() ? null : list.get(0);
    }

    public Long create(SysUser user, List<Long> roleIds) {
        String now = DateTimeUtils.now();
        String encodedPassword = passwordEncoder.encode(
                StringUtils.hasText(user.getPassword()) ? user.getPassword() : "123456");
        jdbcTemplate.update(
                "INSERT INTO sys_user (org_id, username, password, nickname, status, is_super_admin, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                user.getOrgId(),
                user.getUsername(),
                encodedPassword,
                user.getNickname(),
                user.getStatus() == null ? 1 : user.getStatus(),
                user.getIsSuperAdmin() == null ? 0 : user.getIsSuperAdmin(),
                now,
                now);
        Long userId = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        saveUserRoles(userId, roleIds);
        return userId;
    }

    public void update(SysUser user, List<Long> roleIds) {
        if (StringUtils.hasText(user.getPassword())) {
            jdbcTemplate.update(
                    "UPDATE sys_user SET org_id=?, username=?, password=?, nickname=?, status=?, is_super_admin=?, updated_at=? WHERE id=?",
                    user.getOrgId(),
                    user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getNickname(),
                    user.getStatus(),
                    user.getIsSuperAdmin(),
                    DateTimeUtils.now(),
                    user.getId());
        } else {
            jdbcTemplate.update(
                    "UPDATE sys_user SET org_id=?, username=?, nickname=?, status=?, is_super_admin=?, updated_at=? WHERE id=?",
                    user.getOrgId(),
                    user.getUsername(),
                    user.getNickname(),
                    user.getStatus(),
                    user.getIsSuperAdmin(),
                    DateTimeUtils.now(),
                    user.getId());
        }
        saveUserRoles(user.getId(), roleIds);
    }

    public void delete(Long id) {
        SysUser user = getById(id);
        if (user != null && user.getIsSuperAdmin() != null && user.getIsSuperAdmin() == 1) {
            throw new IllegalStateException("超级管理员不能删除");
        }
        jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM sys_user WHERE id = ?", id);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public List<Long> listRoleIdsByUserId(Long userId) {
        return jdbcTemplate.queryForList("SELECT role_id FROM sys_user_role WHERE user_id = ?", Long.class, userId);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = ?", userId);
        if (roleIds == null) {
            return;
        }
        for (Long roleId : roleIds) {
            if (roleId != null) {
                jdbcTemplate.update("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)", userId, roleId);
            }
        }
    }
}
