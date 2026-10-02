package com.cxcron.controller.menu.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuRouteResp {
    /**
     * 菜单 ID。
     */
    private String id;

    /**
     * 父菜单 ID，0 表示顶级菜单。
     */
    private String parentId;

    /**
     * 菜单显示名称。
     */
    private String name;

    /**
     * 前端路由路径。
     */
    private String path;

    /**
     * 页面组件路径；目录菜单使用 BasicLayout。
     */
    private String component;

    /**
     * 页面路由名称。
     */
    private String componentName;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 是否隐藏侧栏：true-隐藏，false-显示。
     */
    private Boolean visible;

    /**
     * 是否缓存页面。
     */
    private Boolean keepAlive;

    /**
     * 是否始终显示父级菜单。
     */
    private Boolean alwaysShow;

    /**
     * 子菜单列表。
     */
    private List<MenuRouteResp> children = new ArrayList<>();
}
