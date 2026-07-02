package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.mapper.SysMenuMapper;
import com.mailcursor.system.model.SysMenu;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    public SysMenuService(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    public List<SysMenu> listAll() {
        return sysMenuMapper.selectAll();
    }

    public List<SysMenu> listTree() {
        return buildTree(listAll());
    }

    public List<SysMenu> listByUserId(Long userId, boolean superAdmin) {
        if (superAdmin) {
            return listTree();
        }
        return buildTree(sysMenuMapper.selectByUserId(userId));
    }

    public SysMenu getById(Long id) {
        return sysMenuMapper.selectById(id);
    }

    public Long create(SysMenu menu) {
        String now = DateTimeUtils.now();
        menu.setParentId(menu.getParentId() == null ? 0L : menu.getParentId());
        menu.setSortOrder(menu.getSortOrder() == null ? 0 : menu.getSortOrder());
        menu.setVisible(menu.getVisible() == null ? 1 : menu.getVisible());
        menu.setStatus(menu.getStatus() == null ? 1 : menu.getStatus());
        menu.setCreatedAt(now);
        menu.setUpdatedAt(now);
        sysMenuMapper.insert(menu);
        return menu.getId();
    }

    public void update(SysMenu menu) {
        menu.setParentId(menu.getParentId() == null ? 0L : menu.getParentId());
        menu.setUpdatedAt(DateTimeUtils.now());
        sysMenuMapper.update(menu);
    }

    public void delete(Long id) {
        if (sysMenuMapper.countChildren(id) > 0) {
            throw new IllegalStateException("存在子菜单，无法删除");
        }
        sysMenuMapper.deleteRoleMenusByMenuId(id);
        sysMenuMapper.deleteById(id);
    }

    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        sysMenuMapper.deleteRoleMenusByRoleId(roleId);
        if (menuIds == null) {
            return;
        }
        for (Long menuId : menuIds) {
            if (menuId != null) {
                sysMenuMapper.insertRoleMenu(roleId, menuId);
            }
        }
    }

    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return sysMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    private List<SysMenu> buildTree(List<SysMenu> menus) {
        Map<Long, SysMenu> map = new HashMap<Long, SysMenu>();
        for (SysMenu menu : menus) {
            map.put(menu.getId(), menu);
            menu.setChildren(new ArrayList<SysMenu>());
        }
        List<SysMenu> roots = new ArrayList<SysMenu>();
        for (SysMenu menu : menus) {
            Long parentId = menu.getParentId() == null ? 0L : menu.getParentId();
            if (parentId == 0L) {
                roots.add(menu);
            } else if (map.containsKey(parentId)) {
                map.get(parentId).getChildren().add(menu);
            }
        }
        return roots;
    }
}
