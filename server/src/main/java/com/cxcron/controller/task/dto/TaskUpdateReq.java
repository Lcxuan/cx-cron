package com.cxcron.controller.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "更新定时任务请求")
public class TaskUpdateReq {

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "任务名称不能为空")
    private String name;

    @Schema(description = "Cron 表达式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Cron 表达式不能为空")
    private String cronExpression;

    @Schema(description = "运行命令", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "运行命令不能为空")
    private String runCommand;

    @Schema(description = "启用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "启用状态不能为空")
    private Integer enabled;

    @Schema(description = "邮件通知策略：OFF-不通知，FAILURE-失败时通知，ALL-始终通知", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"OFF", "FAILURE", "ALL"})
    @NotBlank(message = "邮件通知策略不能为空")
    private String emailNotificationPolicy;

    @Schema(description = "要替换的任务脚本文件；为空时保留当前脚本")
    private MultipartFile script;
}
