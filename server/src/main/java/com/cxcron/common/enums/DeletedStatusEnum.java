package com.cxcron.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用删除状态。
 */
@Getter
@AllArgsConstructor
public enum DeletedStatusEnum {

    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final int code;
    private final String description;
}
