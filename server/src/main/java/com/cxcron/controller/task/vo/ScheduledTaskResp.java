package com.cxcron.controller.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "定时任务响应")
public class ScheduledTaskResp {
    private Long id;
    private String name;
    private String cronExpression;
    private String scriptPath;
    private String runCommand;
    private Integer enabled;
    private String quartzJobName;
    private String lastScheduleStatus;
    private LocalDateTime lastScheduleTime;
    private LocalDateTime createTime;
}
