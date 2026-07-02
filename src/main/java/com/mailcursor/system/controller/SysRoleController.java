package com.mailcursor.system.controller;

import com.mailcursor.common.ApiResult;
import com.mailcursor.system.model.SysRole;
import com.mailcursor.system.service.SysRoleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/roles")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping
    public ApiResult<List<SysRole>> list() {
        return ApiResult.success(sysRoleService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            return ApiResult.fail("角色不存在");
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("role", role);
        data.put("menuIds", sysRoleService.listMenuIdsByRoleId(id));
        return ApiResult.success(data);
    }

    @PostMapping
    public ApiResult<Long> create(@RequestBody RoleSaveRequest request) {
        Long roleId = sysRoleService.create(request.getRole());
        sysRoleService.saveRoleMenus(roleId, request.getMenuIds());
        return ApiResult.success(roleId);
    }

    @PutMapping("/{id}")
    public ApiResult<String> update(@PathVariable Long id, @RequestBody RoleSaveRequest request) {
        request.getRole().setId(id);
        sysRoleService.update(request.getRole());
        sysRoleService.saveRoleMenus(id, request.getMenuIds());
        return ApiResult.success("更新成功", "ok");
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return ApiResult.success("删除成功", "ok");
    }

    public static class RoleSaveRequest {
        private SysRole role;
        private List<Long> menuIds;

        public SysRole getRole() {
            return role;
        }

        public void setRole(SysRole role) {
            this.role = role;
        }

        public List<Long> getMenuIds() {
            return menuIds;
        }

        public void setMenuIds(List<Long> menuIds) {
            this.menuIds = menuIds;
        }
    }
}
