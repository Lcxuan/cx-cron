package com.cxcron.controller.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "定时任务响应")
public class ScheduledTaskResp {
    @Schema(description = "定时任务 ID")
    private Long id;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "Cron 表达式")
    private String cronExpression;

    @Schema(description = "任务脚本存储路径")
    private String scriptPath;

    @Schema(description = "运行命令")
    private String runCommand;

    @Schema(description = "任务启用状态：0-禁用，1-启用")
    private Integer enabled;

    @Schema(description = "邮件通知策略：OFF-不通知，FAILURE-失败时通知，ALL-始终通知")
    private String emailNotificationPolicy;

    @Schema(description = "Quartz Job 名称")
    private String quartzJobName;

    @Schema(description = "最近一次调度状态")
    private String lastScheduleStatus;

    @Schema(description = "最近一次调度时间")
    private LocalDateTime lastScheduleTime;

    @Schema(description = "任务创建时间")
    private LocalDateTime createTime;
}
