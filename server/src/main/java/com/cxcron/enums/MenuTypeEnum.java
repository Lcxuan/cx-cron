package com.cxcron.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum MenuTypeEnum {
    /**
     * 菜单目录，可包含子菜单。
     */
    DIRECTORY("DIRECTORY"),

    /**
     * 页面菜单，对应一个前端页面路由。
     */
    MENU("MENU");

    @EnumValue
    private final String value;

    MenuTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}