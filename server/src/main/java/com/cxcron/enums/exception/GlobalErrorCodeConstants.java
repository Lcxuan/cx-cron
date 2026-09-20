package com.cxcron.enums.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCodeConstants implements ErrorCode {
    SUCCESS("0", "操作成功"),
    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未登录或登录已失效"),
    FORBIDDEN("403", "无权限访问"),
    NOT_FOUND("404", "资源不存在"),
    METHOD_NOT_ALLOWED("405", "请求方法不支持"),
    INTERNAL_SERVER_ERROR("500", "系统异常");

    private final String code;
    private final String msg;
}
