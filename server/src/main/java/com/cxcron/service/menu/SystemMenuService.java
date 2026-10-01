package com.cxcron.service.menu;

import com.cxcron.controller.menu.vo.MenuRouteResp;

import java.util.List;

public interface SystemMenuService {
    
    /**
     * 查询所有已启用菜单的路由树。
     *
     * @return 已启用菜单路由树
     */
    List<MenuRouteResp> getEnabledMenuRoutes();
}
