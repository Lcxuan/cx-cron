package com.cxcron.controller.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "任务执行记录响应")
public class ExecutionRecordResp {
    private Long id;
    private Long scheduledTaskId;
    private String triggerType;
    private String idempotencyKey;
    private String resultType;
    private String summary;
    private String quartzFireInstanceId;
    private LocalDateTime startedTime;
    private LocalDateTime finishedTime;
}
