package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 任务邮件通知记录实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_email_notifications")
public class TaskEmailNotificationDO extends BaseDO {
    /**
     * 关联的任务执行记录 ID。
     */
    private Long executionRecordId;

    /**
     * 定时任务 ID。
     */
    private Long scheduledTaskId;

    /**
     * 任务名称快照。
     */
    private String taskName;

    /**
     * 调度方式快照：CRON 或 RANDOM_DAILY。
     */
    private String scheduleType;

    /**
     * Cron 表达式快照。
     */
    private String cronExpression;

    /**
     * 每日随机执行开始时间快照。
     */
    private LocalTime randomStartTime;

    /**
     * 每日随机执行结束时间快照。
     */
    private LocalTime randomEndTime;

    /**
     * 通知收件人。
     */
    private String recipient;

    /**
     * 邮件主题快照。
     */
    private String subject;

    /**
     * 邮件 HTML 正文快照。
     */
    private String htmlBody;

    /**
     * 通知处理状态：
     * PENDING 表示等待异步发送
     * SENT 表示发送成功
     * FAILED 表示发送失败
     * SKIPPED 表示因通知策略关闭或邮件配置未启用而跳过
     */
    private String status;

    /**
     * 发送失败或跳过原因。
     */
    private String errorMessage;

    /**
     * 邮件发送成功时间。
     */
    private LocalDateTime sentTime;
}
