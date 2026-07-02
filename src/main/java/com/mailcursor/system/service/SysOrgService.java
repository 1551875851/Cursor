package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.mapper.SysOrgMapper;
import com.mailcursor.system.model.SysOrg;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysOrgService {

    private final SysOrgMapper sysOrgMapper;

    public SysOrgService(SysOrgMapper sysOrgMapper) {
        this.sysOrgMapper = sysOrgMapper;
    }

    public List<SysOrg> listAll() {
        return sysOrgMapper.selectAll();
    }

    public List<SysOrg> listTree() {
        return buildTree(listAll());
    }

    public SysOrg getById(Long id) {
        return sysOrgMapper.selectById(id);
    }

    public Long create(SysOrg org) {
        String now = DateTimeUtils.now();
        org.setParentId(org.getParentId() == null ? 0L : org.getParentId());
        org.setSortOrder(org.getSortOrder() == null ? 0 : org.getSortOrder());
        org.setStatus(org.getStatus() == null ? 1 : org.getStatus());
        org.setCreatedAt(now);
        org.setUpdatedAt(now);
        sysOrgMapper.insert(org);
        return org.getId();
    }

    public void update(SysOrg org) {
        org.setParentId(org.getParentId() == null ? 0L : org.getParentId());
        org.setUpdatedAt(DateTimeUtils.now());
        sysOrgMapper.update(org);
    }

    public void delete(Long id) {
        if (sysOrgMapper.countChildren(id) > 0) {
            throw new IllegalStateException("存在下级机构，无法删除");
        }
        if (sysOrgMapper.countUsersByOrgId(id) > 0) {
            throw new IllegalStateException("机构下存在用户，无法删除");
        }
        sysOrgMapper.deleteById(id);
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
