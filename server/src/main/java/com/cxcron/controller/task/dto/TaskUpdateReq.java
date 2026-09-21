package com.cxcron.controller.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "更新定时任务请求")
public class TaskUpdateReq {

    @NotBlank(message = "任务名称不能为空")
    private String name;

    @NotBlank(message = "Cron 表达式不能为空")
    private String cronExpression;

    @NotBlank(message = "运行命令不能为空")
    private String runCommand;

    @NotNull(message = "启用状态不能为空")
    private Integer enabled;

    private MultipartFile script;
}
