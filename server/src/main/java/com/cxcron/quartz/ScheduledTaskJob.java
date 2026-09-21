package com.cxcron.quartz;

import com.cxcron.enums.TaskTriggerTypeEnum;
import com.cxcron.service.execution.TaskExecutionService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class ScheduledTaskJob implements Job {

    public static final String TASK_ID_KEY = "scheduledTaskId";
    public static final String TRIGGER_TYPE_KEY = "triggerType";

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            ((TaskExecutionService) context.getScheduler().getContext().get(TaskQuartzConstants.EXECUTION_SERVICE_KEY))
                    .execute(dataMap.getLong(TASK_ID_KEY),
                            dataMap.getString(TRIGGER_TYPE_KEY) == null ? TaskTriggerTypeEnum.SCHEDULED.name()
                                    : dataMap.getString(TRIGGER_TYPE_KEY),
                            context.getFireInstanceId());
        } catch (Exception e) {
            throw new IllegalStateException("任务执行失败", e);
        }
    }
}
