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

    /**
     * 判断状态是否启用。
     *
     * @param code 状态编码
     * @return 是否启用
     */
    public static boolean isEnabled(Integer code) {
        return Integer.valueOf(ENABLE.code).equals(code);
    }
}
