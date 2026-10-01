package com.cxcron.service.menu.impl;

import com.cxcron.common.enums.CommonStatusEnum;
import com.cxcron.controller.menu.vo.MenuRouteResp;
import com.cxcron.convert.menu.SystemMenuConvert;
import com.cxcron.entity.SystemMenuDO;
import com.cxcron.enums.MenuTypeEnum;
import com.cxcron.mapper.SystemMenuMapper;
import com.cxcron.service.menu.SystemMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemMenuServiceImpl implements SystemMenuService {

    private final SystemMenuMapper systemMenuMapper;

    @Override
    public List<MenuRouteResp> getEnabledMenuRoutes() {
        return buildMenuTree(systemMenuMapper.selectAllMenus());
    }

    /**
     * 按父子关系及排序值构建启用菜单的路由树。
     *
     * @param menus 菜单列表
     * @return 已排序的菜单路由树
     */
    public static List<MenuRouteResp> buildMenuTree(List<SystemMenuDO> menus) {
        Map<Long, List<SystemMenuDO>> children = new HashMap<>();
        for (SystemMenuDO menu : menus) {
            children.computeIfAbsent(menu.getParentId(), key -> new ArrayList<>()).add(menu);
        }
        children.values().forEach(items -> items.sort(Comparator.comparing(SystemMenuDO::getSort,
                Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(SystemMenuDO::getId)));
        return buildChildren(0L, children);
    }

    /**
     * 递归构建指定父菜单下已启用菜单的路由列表。
     *
     * @param parentId 当前父菜单 ID
     * @param children 按父菜单 ID 分组的子菜单列表
     * @return 已排序的子菜单路由列表
     */
    private static List<MenuRouteResp> buildChildren(Long parentId, Map<Long, List<SystemMenuDO>> children) {
        List<MenuRouteResp> routes = new ArrayList<>();
        for (SystemMenuDO menu : children.getOrDefault(parentId, List.of())) {
            if (!CommonStatusEnum.isEnabled(menu.getEnabled())) continue;
            MenuRouteResp route = SystemMenuConvert.INSTANCE.toMenuRouteResp(menu);
            if (menu.getType() == MenuTypeEnum.DIRECTORY) {
                route.setChildren(buildChildren(menu.getId(), children));
            }
            routes.add(route);
        }
        return routes;
    }
}
