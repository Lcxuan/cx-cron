package com.cxcron.convert.menu;

import com.cxcron.controller.menu.vo.MenuRouteResp;
import com.cxcron.entity.SystemMenuDO;
import com.cxcron.enums.MenuTypeEnum;

/**
 * 菜单对象转换器。
 */
public interface SystemMenuConvert {

    SystemMenuConvert INSTANCE = new SystemMenuConvert() {};

    /**
     * 菜单实体转换为路由响应。
     *
     * @param menu 菜单实体
     * @return 菜单路由响应
     */
    default MenuRouteResp toMenuRouteResp(SystemMenuDO menu) {
        MenuRouteResp route = new MenuRouteResp();
        route.setId(menu.getId() == null ? null : String.valueOf(menu.getId()));
        route.setParentId(menu.getParentId() == null ? null : String.valueOf(menu.getParentId()));
        route.setName(menu.getName());
        route.setPath(menu.getPath());
        route.setComponent(menu.getType() == MenuTypeEnum.DIRECTORY ? "BasicLayout" : menu.getComponent());
        route.setComponentName(menu.getComponentName());
        route.setIcon(menu.getIcon());
        route.setVisible(menu.getVisible() != null && menu.getVisible() == 1);
        route.setKeepAlive(menu.getKeepAlive() != null && menu.getKeepAlive() == 1);
        route.setAlwaysShow(menu.getAlwaysShow() != null && menu.getAlwaysShow() == 1);
        return route;
    }
}
