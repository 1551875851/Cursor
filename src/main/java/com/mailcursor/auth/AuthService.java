package com.mailcursor.auth;

import com.mailcursor.system.model.SysMenu;
import com.mailcursor.system.model.SysUser;
import com.mailcursor.system.service.SysMenuService;
import com.mailcursor.system.service.SysUserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(SysUserService sysUserService,
                       SysMenuService sysMenuService,
                       JwtTokenProvider jwtTokenProvider) {
        this.sysUserService = sysUserService;
        this.sysMenuService = sysMenuService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Map<String, Object> login(String username, String password) {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("用户已禁用");
        }
        if (!sysUserService.matchesPassword(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        boolean superAdmin = user.getIsSuperAdmin() != null && user.getIsSuperAdmin() == 1;
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), superAdmin);
        List<SysMenu> menus = sysMenuService.listByUserId(user.getId(), superAdmin);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("token", token);
        result.put("user", buildUserInfo(user, superAdmin));
        result.put("menus", menus);
        return result;
    }

    public Map<String, Object> currentUserInfo(Long userId, boolean superAdmin) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("user", buildUserInfo(user, superAdmin));
        result.put("menus", sysMenuService.listByUserId(userId, superAdmin));
        return result;
    }

    private Map<String, Object> buildUserInfo(SysUser user, boolean superAdmin) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("orgId", user.getOrgId());
        info.put("orgName", user.getOrgName());
        info.put("superAdmin", superAdmin);
        return info;
    }
}
