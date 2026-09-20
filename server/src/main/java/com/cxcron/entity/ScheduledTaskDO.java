package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scheduled_tasks")
public class ScheduledTaskDO extends BaseDO {

    /**
     * 任务类型。
     */
    private String taskType;

    /**
     * Cron 表达式。
     */
    private String cronExpression;

    /**
     * 是否启用：0-暂停，1-启用。
     */
    private Integer enabled;

    /**
     * XXL-JOB 任务 ID。
     */
    private Long xxlJobId;

    /**
     * 最近调度状态。
     */
    private String lastScheduleStatus;

    /**
     * 最近调度时间。
     */
    private LocalDateTime lastScheduleTime;
}
