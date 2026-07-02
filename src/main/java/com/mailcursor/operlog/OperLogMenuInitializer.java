package com.mailcursor.operlog;

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
public class OperLogMenuInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OperLogMenuInitializer.class);

    private final SystemBootstrapMapper systemBootstrapMapper;
    private final SysMenuService sysMenuService;

    public OperLogMenuInitializer(SystemBootstrapMapper systemBootstrapMapper,
                                  SysMenuService sysMenuService) {
        this.systemBootstrapMapper = systemBootstrapMapper;
        this.sysMenuService = sysMenuService;
    }

    @Override
    public void run(String... args) {
        if (systemBootstrapMapper.countMenuByPath("/system/oper-log") > 0) {
            return;
        }

        Long systemMenuId = systemBootstrapMapper.selectSystemMenuId();
        if (systemMenuId == null) {
            log.warn("未找到系统管理菜单，跳过日志管理菜单初始化");
            return;
        }

        log.info("初始化日志管理菜单...");
        SysMenu operLogMenu = new SysMenu();
        operLogMenu.setParentId(systemMenuId);
        operLogMenu.setMenuName("日志管理");
        operLogMenu.setMenuType("MENU");
        operLogMenu.setPath("/system/oper-log");
        operLogMenu.setComponent("SystemOperLog");
        operLogMenu.setIcon("el-icon-document-copy");
        operLogMenu.setSortOrder(5);
        operLogMenu.setVisible(1);
        operLogMenu.setStatus(1);
        Long menuId = sysMenuService.create(operLogMenu);

        List<Long> roleIds = systemBootstrapMapper.selectAllRoleIds();
        for (Long roleId : roleIds) {
            if (systemBootstrapMapper.countRoleMenu(roleId, menuId) == 0) {
                systemBootstrapMapper.insertRoleMenu(roleId, menuId);
            }
        }
        log.info("日志管理菜单初始化完成，menuId={}", menuId);
    }
}
