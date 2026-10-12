package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 定时任务实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scheduled_tasks")
public class ScheduledTaskDO extends BaseDO {

    /** 
     * 任务名称。 
     */
    private String name;

    /** 
     * Cron 表达式。 
     */
    private String cronExpression;

    /**
     * 任务调度方式：CRON 或 RANDOM_DAILY。
     */
    private String scheduleType;

    /**
     * 每日随机执行开始时间范围
     */
    private LocalTime randomStartTime;

    /**
     * 每日随机执行结束时间范围
     */
    private LocalTime randomEndTime;

    /** 
     * 上传脚本的存储路径。 
     */
    private String scriptPath;

    /** 
     * 运行命令，使用 {script} 表示上传脚本路径。
     */
    private String runCommand;

    /**
     * 是否启用：1-启用，0-关闭。
     */
    private Integer enabled;

    /** 
     * 邮件通知策略：OFF 不通知，FAILURE 任务失败时通知，ALL 任务执行完成后通知。
     */
    private String emailNotificationPolicy;

    /** 
     * Quartz Job 名称。 
     */
    private String quartzJobName;

    /** 
     * 最近一次调度状态。 
     */
    private String lastScheduleStatus;

    /** 
     * 最近一次调度时间。 
     */
    private LocalDateTime lastScheduleTime;
}
