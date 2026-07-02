package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.system.model.SysOrg;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysOrgService {

    private final JdbcTemplate jdbcTemplate;

    public SysOrgService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SysOrg> listAll() {
        return jdbcTemplate.query(
                "SELECT id, parent_id AS parentId, org_name AS orgName, org_code AS orgCode, "
                        + "sort_order AS sortOrder, status, created_at AS createdAt, updated_at AS updatedAt "
                        + "FROM sys_org ORDER BY sort_order ASC, id ASC",
                new BeanPropertyRowMapper<SysOrg>(SysOrg.class));
    }

    public List<SysOrg> listTree() {
        return buildTree(listAll());
    }

    public SysOrg getById(Long id) {
        List<SysOrg> list = jdbcTemplate.query(
                "SELECT id, parent_id AS parentId, org_name AS orgName, org_code AS orgCode, "
                        + "sort_order AS sortOrder, status, created_at AS createdAt, updated_at AS updatedAt "
                        + "FROM sys_org WHERE id = ?",
                new BeanPropertyRowMapper<SysOrg>(SysOrg.class),
                id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Long create(SysOrg org) {
        String now = DateTimeUtils.now();
        jdbcTemplate.update(
                "INSERT INTO sys_org (parent_id, org_name, org_code, sort_order, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                org.getParentId() == null ? 0L : org.getParentId(),
                org.getOrgName(),
                org.getOrgCode(),
                org.getSortOrder() == null ? 0 : org.getSortOrder(),
                org.getStatus() == null ? 1 : org.getStatus(),
                now,
                now);
        return jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
    }

    public void update(SysOrg org) {
        jdbcTemplate.update(
                "UPDATE sys_org SET parent_id=?, org_name=?, org_code=?, sort_order=?, status=?, updated_at=? WHERE id=?",
                org.getParentId() == null ? 0L : org.getParentId(),
                org.getOrgName(),
                org.getOrgCode(),
                org.getSortOrder(),
                org.getStatus(),
                DateTimeUtils.now(),
                org.getId());
    }

    public void delete(Long id) {
        Integer childCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_org WHERE parent_id = ?", Integer.class, id);
        if (childCount != null && childCount > 0) {
            throw new IllegalStateException("存在下级机构，无法删除");
        }
        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_user WHERE org_id = ?", Integer.class, id);
        if (userCount != null && userCount > 0) {
            throw new IllegalStateException("机构下存在用户，无法删除");
        }
        jdbcTemplate.update("DELETE FROM sys_org WHERE id = ?", id);
    }

    private List<SysOrg> buildTree(List<SysOrg> orgs) {
        Map<Long, SysOrg> map = new HashMap<Long, SysOrg>();
        for (SysOrg org : orgs) {
            map.put(org.getId(), org);
        }
        List<SysOrg> roots = new ArrayList<SysOrg>();
        for (SysOrg org : orgs) {
            Long parentId = org.getParentId() == null ? 0L : org.getParentId();
            if (parentId == 0L) {
                roots.add(org);
            }
        }
        return roots.isEmpty() ? orgs : roots;
    }
}
