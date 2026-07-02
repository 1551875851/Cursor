package com.mailcursor.system;

import com.mailcursor.mapper.SystemBootstrapMapper;
import com.mailcursor.system.model.SysMenu;
import com.mailcursor.system.service.SysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class DashboardMenuInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DashboardMenuInitializer.class);

    private final SystemBootstrapMapper systemBootstrapMapper;
    private final SysMenuService sysMenuService;

    public DashboardMenuInitializer(SystemBootstrapMapper systemBootstrapMapper,
                                    SysMenuService sysMenuService) {
        this.systemBootstrapMapper = systemBootstrapMapper;
        this.sysMenuService = sysMenuService;
    }

    @Override
    public void run(String... args) {
        if (systemBootstrapMapper.countMenuByPath("/home") > 0) {
            return;
        }

        log.info("初始化首页菜单...");
        SysMenu home = new SysMenu();
        home.setParentId(0L);
        home.setMenuName("首页");
        home.setMenuType("MENU");
        home.setPath("/home");
        home.setComponent("DashboardHome");
        home.setIcon("el-icon-s-home");
        home.setSortOrder(0);
        home.setVisible(1);
        home.setStatus(1);
        Long homeMenuId = sysMenuService.create(home);

        systemBootstrapMapper.bumpRootMenuSortOrder(homeMenuId);

        List<Long> roleIds = systemBootstrapMapper.selectAllRoleIds();
        for (Long roleId : roleIds) {
            if (systemBootstrapMapper.countRoleMenu(roleId, homeMenuId) == 0) {
                systemBootstrapMapper.insertRoleMenu(roleId, homeMenuId);
            }
        }
        log.info("首页菜单初始化完成，menuId={}", homeMenuId);
    }
}
