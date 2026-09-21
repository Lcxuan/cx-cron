package com.cxcron.controller.task.dto;

import com.cxcron.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时任务分页查询参数")
public class TaskPageReq extends PageParam {
}
