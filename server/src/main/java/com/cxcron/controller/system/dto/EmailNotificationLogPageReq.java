package com.cxcron.controller.system.dto;

import com.cxcron.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "邮件日志查询参数")
public class EmailNotificationLogPageReq extends PageParam {
    @Schema(description = "通知状态：PENDING-待发送，SENT-已发送，FAILED-发送失败，SKIPPED-已跳过")
    private String status;

    @Schema(description = "任务名称，支持模糊查询")
    private String taskName;

    @Schema(description = "收件人邮箱，支持模糊查询")
    private String recipient;

    @Schema(description = "记录创建时间起始值")
    private LocalDateTime createTimeStart;

    @Schema(description = "记录创建时间结束值")
    private LocalDateTime createTimeEnd;
}
