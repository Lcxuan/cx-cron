package com.cxcron.quartz;

import com.cxcron.enums.TaskTriggerTypeEnum;
import com.cxcron.service.execution.TaskExecutionService;
import com.cxcron.service.quartz.QuartzTaskService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ScheduledTaskJob implements Job {

    public static final String TASK_ID_KEY = "scheduledTaskId";
    public static final String TRIGGER_TYPE_KEY = "triggerType";

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            String triggerType = dataMap.getString(TRIGGER_TYPE_KEY) == null ? TaskTriggerTypeEnum.SCHEDULED.name() : dataMap.getString(TRIGGER_TYPE_KEY);
            try {
                ((TaskExecutionService) context.getScheduler().getContext().get(TaskQuartzConstants.EXECUTION_SERVICE_KEY))
                        .execute(dataMap.getLong(TASK_ID_KEY), triggerType, context.getFireInstanceId());
            } finally {
                if (TaskTriggerTypeEnum.SCHEDULED.name().equals(triggerType)
                        && context.getScheduler().getContext().get(TaskQuartzConstants.QUARTZ_TASK_SERVICE_KEY) instanceof QuartzTaskService quartzTaskService) {
                    quartzTaskService.scheduleNextRandom(dataMap.getLong(TASK_ID_KEY),
                            LocalDateTime.ofInstant(context.getScheduledFireTime().toInstant(), ZoneId.systemDefault()));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("任务执行失败", e);
        }
    }
}
