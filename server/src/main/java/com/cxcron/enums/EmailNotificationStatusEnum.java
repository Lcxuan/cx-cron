package com.cxcron.enums;

/**
 * 任务邮件通知发送状态。
 */
public enum EmailNotificationStatusEnum {
    /**
     * 通知记录已创建，等待发送。
     */
    PENDING,

    /**
     * 邮件发送成功。
     */
    SENT,

    /**
     * 邮件发送失败。
     */
    FAILED,

    /**
     * 根据配置跳过发送。
     */
    SKIPPED;
}
