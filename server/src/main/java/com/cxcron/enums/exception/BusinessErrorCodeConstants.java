package com.cxcron.enums.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCodeConstants implements ErrorCode {

    // 认证模块
    LOGIN_FAILED("1001", "账号或密码错误"),
    PASSWORD_INCORRECT("1002", "当前密码错误"),

    // 任务模块
    TASK_NOT_FOUND("2001", "任务不存在"),
    TASK_CRON_INVALID("2002", "Cron 表达式不合法"),
    TASK_DISABLED("2003", "任务已暂停"),

    // 邮件
    EMAIL_CONFIG_INVALID("3001", "邮件配置不完整或无效"),
    EMAIL_ENCRYPTION_KEY_MISSING("3002", "未配置邮件密码加密密钥"),
    EMAIL_CONFIG_DISABLED("3003", "邮件通知未启用或未配置");

    private final String code;
    private final String msg;
}
