package com.cxcron.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用启用状态。
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),
    ;

    private final int code;
    private final String description;
}
