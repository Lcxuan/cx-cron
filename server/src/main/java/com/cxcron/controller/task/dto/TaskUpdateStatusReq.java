package com.cxcron.controller.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新任务启用状态请求。
 */
@Data
@Schema(description = "更新任务启用状态请求")
public class TaskUpdateStatusReq {

    /**
     * 启用状态：1 启用，0 关闭。
     */
    @NotNull(message = "启用状态不能为空")
    @Min(value = 0, message = "启用状态只能为 0 或 1")
    @Max(value = 1, message = "启用状态只能为 0 或 1")
    private Integer enabled;
}
