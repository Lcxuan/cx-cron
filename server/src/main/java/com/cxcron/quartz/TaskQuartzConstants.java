package com.cxcron.quartz;

import org.quartz.CronExpression;

public final class TaskQuartzConstants {

    public static final String GROUP = "CX_CRON";
    public static final String EXECUTION_SERVICE_KEY = "executionTaskService";

    private TaskQuartzConstants() {
    }

    public static String jobName(Long taskId) {
        return "task-" + taskId;
    }

    public static String triggerName(Long taskId) {
        return jobName(taskId);
    }

    public static boolean isValidCron(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }
}
