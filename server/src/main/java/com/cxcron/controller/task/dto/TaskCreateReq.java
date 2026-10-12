package com.cxcron.controller.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Data
@Schema(description = "创建定时任务请求")
public class TaskCreateReq {

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "任务名称不能为空")
    private String name;

    @Schema(description = "调度方式：CRON-按 Cron 定时，RANDOM_DAILY-每天随机执行", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "调度方式不能为空")
    private String scheduleType = "CRON";

    @Schema(description = "Cron 表达式；Cron 定时模式必填")
    private String cronExpression;

    @Schema(description = "每日随机执行开始时间；格式 HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime randomStartTime;

    @Schema(description = "每日随机执行结束时间；格式 HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime randomEndTime;

    @Schema(description = "运行命令", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "运行命令不能为空")
    private String runCommand;

    @Schema(description = "启用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "启用状态不能为空")
    private Integer enabled;

    @Schema(description = "邮件通知策略：OFF-不通知，FAILURE-失败时通知，ALL-始终通知", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"OFF", "FAILURE", "ALL"})
    @NotBlank(message = "邮件通知策略不能为空")
    private String emailNotificationPolicy;

    @Schema(description = "任务脚本文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "脚本不能为空")
    private MultipartFile script;
}
