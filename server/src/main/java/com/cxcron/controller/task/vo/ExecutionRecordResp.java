package com.cxcron.controller.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "任务执行记录响应")
public class ExecutionRecordResp {
    @Schema(description = "执行记录 ID")
    private Long id;

    @Schema(description = "关联的定时任务 ID")
    private Long scheduledTaskId;

    @Schema(description = "触发方式：SCHEDULED-定时调度，MANUAL-手动触发")
    private String triggerType;

    @Schema(description = "执行请求的幂等键")
    private String idempotencyKey;

    @Schema(description = "执行结果类型")
    private String resultType;

    @Schema(description = "执行摘要")
    private String summary;

    @Schema(description = "Quartz 调度触发实例 ID")
    private String quartzFireInstanceId;

    @Schema(description = "执行开始时间")
    private LocalDateTime startedTime;

    @Schema(description = "执行结束时间")
    private LocalDateTime finishedTime;

    @Schema(description = "此执行记录对应的邮件通知信息")
    private TaskEmailNotificationResp notification;
}
