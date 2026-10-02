package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import com.cxcron.enums.MenuTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_menus")
public class SystemMenuDO extends BaseDO {
    /**
     * 父菜单 ID，0 表示顶级菜单。
     */
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    private String name;

    /**
     * 前端路由路径。
     */
    private String path;

    /**
     * 页面组件路径
     */
    private String component;

    /**
     * 页面路由名称。
     */
    private String componentName;

    /**
     * 菜单类型：目录或页面菜单。
     */
    private MenuTypeEnum type;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 同级菜单排序值，值越小越靠前。
     */
    private Integer sort;

    /**
     * 是否启用：0-否，1-是。
     */
    private Integer enabled;

    /**
     * 是否隐藏侧栏：0-否，1-是。
     */
    private Integer visible;

    /**
     * 是否缓存页面：0-否，1-是。
     */
    private Integer keepAlive;

    /**
     * 是否始终显示父级菜单：0-否，1-是。
     */
    private Integer alwaysShow;
}
