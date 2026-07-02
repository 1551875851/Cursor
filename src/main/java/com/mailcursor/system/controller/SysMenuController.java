package com.mailcursor.system.controller;

import com.mailcursor.common.ApiResult;
import com.mailcursor.system.model.SysMenu;
import com.mailcursor.system.service.SysMenuService;
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
@RequestMapping("/api/system/menus")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @GetMapping
    public ApiResult<List<SysMenu>> list() {
        return ApiResult.success(sysMenuService.listAll());
    }

    @GetMapping("/tree")
    public ApiResult<List<SysMenu>> tree() {
        return ApiResult.success(sysMenuService.listTree());
    }

    @PostMapping
    public ApiResult<Long> create(@RequestBody SysMenu menu) {
        return ApiResult.success(sysMenuService.create(menu));
    }

    @PutMapping("/{id}")
    public ApiResult<String> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        sysMenuService.update(menu);
        return ApiResult.success("更新成功", "ok");
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return ApiResult.success("删除成功", "ok");
    }
}
