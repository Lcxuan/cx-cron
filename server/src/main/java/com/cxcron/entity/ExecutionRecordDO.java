package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("execution_records")
public class ExecutionRecordDO extends BaseDO {

    /**
     * 定时任务 ID。
     */
    private Long scheduledTaskId;

    /**
     * 触发方式：SCHEDULED-调度，MANUAL-手动。
     */
    private String triggerType;

    /**
     * 业务幂等键。
     */
    private String idempotencyKey;

    /**
     * 执行结果。
     */
    private String resultType;

    /**
     * 脱敏执行摘要。
     */
    private String summary;

    /**
     * Quartz 触发实例 ID。
     */
    private String quartzFireInstanceId;

    /**
     * 开始执行时间。
     */
    private LocalDateTime startedTime;

    /**
     * 结束执行时间。
     */
    private LocalDateTime finishedTime;
}
