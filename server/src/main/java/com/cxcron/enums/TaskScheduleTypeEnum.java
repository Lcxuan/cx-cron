package com.cxcron.enums;

/**
 * 任务调度方式。
 */
public enum TaskScheduleTypeEnum {
    /**
     * 按 Cron 表达式定时执行。
     */
    CRON,

    /**
     * 每天在指定时间范围内随机执行一次。
     */
    RANDOM_DAILY;
}
