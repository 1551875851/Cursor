package com.mailcursor.system.controller;

import com.mailcursor.common.ApiResult;
import com.mailcursor.system.model.SysOrg;
import com.mailcursor.system.service.SysOrgService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system/orgs")
public class SysOrgController {

    private final SysOrgService sysOrgService;

    public SysOrgController(SysOrgService sysOrgService) {
        this.sysOrgService = sysOrgService;
    }

    @GetMapping
    public ApiResult<List<SysOrg>> list() {
        return ApiResult.success(sysOrgService.listAll());
    }

    @GetMapping("/tree")
    public ApiResult<List<SysOrg>> tree() {
        return ApiResult.success(sysOrgService.listTree());
    }

    @PostMapping
    public ApiResult<Long> create(@RequestBody SysOrg org) {
        return ApiResult.success(sysOrgService.create(org));
    }

    @PutMapping("/{id}")
    public ApiResult<String> update(@PathVariable Long id, @RequestBody SysOrg org) {
        org.setId(id);
        sysOrgService.update(org);
        return ApiResult.success("更新成功", "ok");
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> delete(@PathVariable Long id) {
        sysOrgService.delete(id);
        return ApiResult.success("删除成功", "ok");
    }
}
