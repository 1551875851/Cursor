package com.mailcursor.system;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.system.model.SysMenu;
import com.mailcursor.system.model.SysOrg;
import com.mailcursor.system.model.SysRole;
import com.mailcursor.system.model.SysUser;
import com.mailcursor.system.service.SysMenuService;
import com.mailcursor.system.service.SysOrgService;
import com.mailcursor.system.service.SysRoleService;
import com.mailcursor.system.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SystemDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemDataInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final SysOrgService sysOrgService;
    private final SysRoleService sysRoleService;
    private final SysMenuService sysMenuService;
    private final SysUserService sysUserService;

    public SystemDataInitializer(JdbcTemplate jdbcTemplate,
                                 SysOrgService sysOrgService,
                                 SysRoleService sysRoleService,
                                 SysMenuService sysMenuService,
                                 SysUserService sysUserService) {
        this.jdbcTemplate = jdbcTemplate;
        this.sysOrgService = sysOrgService;
        this.sysRoleService = sysRoleService;
        this.sysMenuService = sysMenuService;
        this.sysUserService = sysUserService;
    }

    @Override
    public void run(String... args) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM sys_user", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        log.info("初始化系统基础数据...");
        initData();
        log.info("系统基础数据初始化完成");
    }

    private void initData() {
        SysOrg org = new SysOrg();
        org.setParentId(0L);
        org.setOrgName("总部");
        org.setOrgCode("HQ");
        org.setSortOrder(1);
        org.setStatus(1);
        Long orgId = sysOrgService.create(org);

        SysRole role = new SysRole();
        role.setRoleCode("ADMIN");
        role.setRoleName("超级管理员");
        role.setStatus(1);
        role.setRemark("拥有全部菜单权限");
        Long roleId = sysRoleService.create(role);

        Long emailMenuId = createMenu(0L, "邮件管理", "DIR", null, null, "el-icon-message", 1);
        Long mailItemId = createMenu(emailMenuId, "发送邮件", "MENU", "/email/mail", "MailSend", "el-icon-s-promotion", 1);
        Long ruankaoItemId = createMenu(emailMenuId, "软考扫描", "MENU", "/email/ruankao", "RuankaoScan", "el-icon-search", 2);

        Long systemMenuId = createMenu(0L, "系统管理", "DIR", null, null, "el-icon-setting", 2);
        Long userMenuId = createMenu(systemMenuId, "用户管理", "MENU", "/system/user", "SystemUser", "el-icon-user", 1);
        Long roleMenuId = createMenu(systemMenuId, "角色管理", "MENU", "/system/role", "SystemRole", "el-icon-s-custom", 2);
        Long orgMenuId = createMenu(systemMenuId, "机构管理", "MENU", "/system/org", "SystemOrg", "el-icon-office-building", 3);
        Long menuMenuId = createMenu(systemMenuId, "菜单管理", "MENU", "/system/menu", "SystemMenu", "el-icon-menu", 4);

        List<Long> allMenuIds = Arrays.asList(
                emailMenuId, mailItemId, ruankaoItemId,
                systemMenuId, userMenuId, roleMenuId, orgMenuId, menuMenuId);
        sysRoleService.saveRoleMenus(roleId, allMenuIds);

        SysUser admin = new SysUser();
        admin.setOrgId(orgId);
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setNickname("超级管理员");
        admin.setStatus(1);
        admin.setIsSuperAdmin(1);
        sysUserService.create(admin, Arrays.asList(roleId));
    }

    private Long createMenu(Long parentId, String name, String type, String path,
                            String component, String icon, int sortOrder) {
        SysMenu menu = new SysMenu();
        menu.setParentId(parentId);
        menu.setMenuName(name);
        menu.setMenuType(type);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setIcon(icon);
        menu.setSortOrder(sortOrder);
        menu.setVisible(1);
        menu.setStatus(1);
        return sysMenuService.create(menu);
    }
}
