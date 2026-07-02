package com.mailcursor.system.controller;

import com.mailcursor.common.ApiResult;
import com.mailcursor.system.model.SysMenu;
import com.mailcursor.system.model.SysOrg;
import com.mailcursor.system.model.SysRole;
import com.mailcursor.system.model.SysUser;
import com.mailcursor.system.service.SysMenuService;
import com.mailcursor.system.service.SysOrgService;
import com.mailcursor.system.service.SysRoleService;
import com.mailcursor.system.service.SysUserService;
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
@RequestMapping("/api/system/users")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @GetMapping
    public ApiResult<List<SysUser>> list() {
        List<SysUser> users = sysUserService.listAll();
        for (SysUser user : users) {
            user.setPassword(null);
        }
        return ApiResult.success(users);
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return ApiResult.fail("用户不存在");
        }
        user.setPassword(null);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("user", user);
        data.put("roleIds", sysUserService.listRoleIdsByUserId(id));
        return ApiResult.success(data);
    }

    @PostMapping
    public ApiResult<Long> create(@RequestBody UserSaveRequest request) {
        return ApiResult.success(sysUserService.create(request.getUser(), request.getRoleIds()));
    }

    @PutMapping("/{id}")
    public ApiResult<String> update(@PathVariable Long id, @RequestBody UserSaveRequest request) {
        request.getUser().setId(id);
        sysUserService.update(request.getUser(), request.getRoleIds());
        return ApiResult.success("更新成功", "ok");
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return ApiResult.success("删除成功", "ok");
    }

    public static class UserSaveRequest {
        private SysUser user;
        private List<Long> roleIds;

        public SysUser getUser() {
            return user;
        }

        public void setUser(SysUser user) {
            this.user = user;
        }

        public List<Long> getRoleIds() {
            return roleIds;
        }

        public void setRoleIds(List<Long> roleIds) {
            this.roleIds = roleIds;
        }
    }
}
