package com.cxcron.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户类型枚举。
 */
@Getter
@RequiredArgsConstructor
public enum UserTypeEnum {

    /**
     * 管理员。
     */
    ADMIN("ADMIN");

    private final String value;
}
