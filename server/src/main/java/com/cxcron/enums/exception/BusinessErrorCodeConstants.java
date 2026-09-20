package com.cxcron.enums.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 认证业务错误码。
 */
@Getter
@RequiredArgsConstructor
public enum BusinessErrorCodeConstants implements ErrorCode {

    LOGIN_FAILED("1001", "账号或密码错误"),
    TOKEN_INVALID("1002", "令牌无效或已过期");

    private final String code;
    private final String msg;
}
