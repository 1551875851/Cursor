package com.mailcursor.system.service;

import com.mailcursor.common.DateTimeUtils;
import com.mailcursor.system.model.SysMenu;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysMenuService {

    private final JdbcTemplate jdbcTemplate;

    public SysMenuService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String MENU_COLUMN_LIST =
            "id, parent_id AS parentId, menu_name AS menuName, menu_type AS menuType, path, component, icon, "
                    + "sort_order AS sortOrder, visible, status, created_at AS createdAt, updated_at AS updatedAt";

    private static final String MENU_COLUMN_LIST_WITH_ALIAS =
            "m.id, m.parent_id AS parentId, m.menu_name AS menuName, m.menu_type AS menuType, m.path, m.component, m.icon, "
                    + "m.sort_order AS sortOrder, m.visible, m.status, m.created_at AS createdAt, m.updated_at AS updatedAt";

    public List<SysMenu> listAll() {
        return jdbcTemplate.query(
                "SELECT " + MENU_COLUMN_LIST + " FROM sys_menu ORDER BY sort_order ASC, id ASC",
                new BeanPropertyRowMapper<SysMenu>(SysMenu.class));
    }

    public List<SysMenu> listTree() {
        return buildTree(listAll());
    }

    public List<SysMenu> listByUserId(Long userId, boolean superAdmin) {
        if (superAdmin) {
            return listTree();
        }
        List<SysMenu> menus = jdbcTemplate.query(
                "SELECT DISTINCT " + MENU_COLUMN_LIST_WITH_ALIAS + " FROM sys_menu m "
                        + "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id "
                        + "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id "
                        + "WHERE ur.user_id = ? AND m.status = 1 AND m.visible = 1 "
                        + "ORDER BY m.sort_order ASC, m.id ASC",
                new BeanPropertyRowMapper<SysMenu>(SysMenu.class),
                userId);
        return buildTree(menus);
    }

    public SysMenu getById(Long id) {
        List<SysMenu> list = jdbcTemplate.query(
                "SELECT " + MENU_COLUMN_LIST + " FROM sys_menu WHERE id = ?",
                new BeanPropertyRowMapper<SysMenu>(SysMenu.class),
                id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Long create(SysMenu menu) {
        String now = DateTimeUtils.now();
        jdbcTemplate.update(
                "INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, icon, sort_order, visible, status, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                menu.getParentId() == null ? 0L : menu.getParentId(),
                menu.getMenuName(),
                menu.getMenuType(),
                menu.getPath(),
                menu.getComponent(),
                menu.getIcon(),
                menu.getSortOrder() == null ? 0 : menu.getSortOrder(),
                menu.getVisible() == null ? 1 : menu.getVisible(),
                menu.getStatus() == null ? 1 : menu.getStatus(),
                now,
                now);
        return jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
    }

    public void update(SysMenu menu) {
        jdbcTemplate.update(
                "UPDATE sys_menu SET parent_id=?, menu_name=?, menu_type=?, path=?, component=?, icon=?, "
                        + "sort_order=?, visible=?, status=?, updated_at=? WHERE id=?",
                menu.getParentId() == null ? 0L : menu.getParentId(),
                menu.getMenuName(),
                menu.getMenuType(),
                menu.getPath(),
                menu.getComponent(),
                menu.getIcon(),
                menu.getSortOrder(),
                menu.getVisible(),
                menu.getStatus(),
                DateTimeUtils.now(),
                menu.getId());
    }

    public void delete(Long id) {
        Integer childCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sys_menu WHERE parent_id = ?", Integer.class, id);
        if (childCount != null && childCount > 0) {
            throw new IllegalStateException("存在子菜单，无法删除");
        }
        jdbcTemplate.update("DELETE FROM sys_role_menu WHERE menu_id = ?", id);
        jdbcTemplate.update("DELETE FROM sys_menu WHERE id = ?", id);
    }

    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        jdbcTemplate.update("DELETE FROM sys_role_menu WHERE role_id = ?", roleId);
        if (menuIds == null) {
            return;
        }
        for (Long menuId : menuIds) {
            if (menuId != null) {
                jdbcTemplate.update("INSERT INTO sys_role_menu (role_id, menu_id) VALUES (?, ?)", roleId, menuId);
            }
        }
    }

    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return jdbcTemplate.queryForList("SELECT menu_id FROM sys_role_menu WHERE role_id = ?", Long.class, roleId);
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
