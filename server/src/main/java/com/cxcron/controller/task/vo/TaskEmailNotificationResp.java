package com.cxcron.controller.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "任务邮件通知响应")
public class TaskEmailNotificationResp {
    @Schema(description = "通知状态：PENDING-待发送，SENT-已发送，FAILED-发送失败，SKIPPED-已跳过")
    private String status;

    @Schema(description = "邮件发送成功时间")
    private LocalDateTime sentTime;

    @Schema(description = "发送失败或跳过原因")
    private String errorMessage;
}
