package com.cxcron.controller.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "邮件日志")
public class EmailNotificationLogResp {
    @Schema(description = "邮件通知记录 ID")
    private Long id;

    @Schema(description = "任务名称快照")
    private String taskName;

    @Schema(description = "Cron 表达式快照")
    private String cronExpression;

    @Schema(description = "邮件收件人")
    private String recipient;

    @Schema(description = "通知状态：PENDING-待发送，SENT-已发送，FAILED-发送失败，SKIPPED-已跳过")
    private String status;

    @Schema(description = "通知记录创建时间")
    private LocalDateTime createTime;

    @Schema(description = "邮件发送成功时间")
    private LocalDateTime sentTime;

    @Schema(description = "发送失败或跳过原因")
    private String errorMessage;

    @Schema(description = "邮件主题快照")
    private String subject;

    @Schema(description = "邮件 HTML 正文快照")
    private String htmlBody;
}
