package com.cxcron.service.quartz.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.enums.CommonStatusEnum;
import com.cxcron.entity.ScheduledTaskDO;
import com.cxcron.enums.TaskTriggerTypeEnum;
import com.cxcron.mapper.ScheduledTaskMapper;
import com.cxcron.quartz.ScheduledTaskJob;
import com.cxcron.quartz.TaskQuartzConstants;
import com.cxcron.service.execution.TaskExecutionService;
import com.cxcron.service.quartz.QuartzTaskService;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuartzTaskServiceImpl implements QuartzTaskService, ApplicationRunner {

    private final Scheduler scheduler;
    private final ScheduledTaskMapper scheduledTaskMapper;
    private final TaskExecutionService taskExecutionService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            scheduler.getContext().put(TaskQuartzConstants.EXECUTION_SERVICE_KEY, taskExecutionService);
            List<ScheduledTaskDO> tasks = scheduledTaskMapper.selectList(new LambdaQueryWrapper<ScheduledTaskDO>()
                    .eq(ScheduledTaskDO::getEnabled, CommonStatusEnum.ENABLE.getCode()));
            tasks.forEach(this::schedule);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务恢复失败", e);
        }
    }

    @Override
    public void schedule(ScheduledTaskDO task) {
        try {
            JobKey jobKey = jobKey(task.getId());
            Trigger trigger = buildTrigger(task);
            if (scheduler.checkExists(jobKey)) {
                if (scheduler.checkExists(trigger.getKey())) {
                    scheduler.rescheduleJob(trigger.getKey(), trigger);
                } else {
                    scheduler.scheduleJob(trigger);
                }
                scheduler.resumeJob(jobKey);
                return;
            }
            scheduler.scheduleJob(JobBuilder.newJob(ScheduledTaskJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(new JobDataMap(Map.of(ScheduledTaskJob.TASK_ID_KEY, task.getId())))
                    .build(), trigger);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务调度失败", e);
        }
    }

    @Override
    public void pause(ScheduledTaskDO task) {
        try {
            scheduler.pauseJob(jobKey(task.getId()));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务暂停失败", e);
        }
    }

    @Override
    public void delete(ScheduledTaskDO task) {
        try {
            scheduler.deleteJob(jobKey(task.getId()));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务删除失败", e);
        }
    }

    @Override
    public void trigger(ScheduledTaskDO task) {
        try {
            scheduler.triggerJob(jobKey(task.getId()), new JobDataMap(Map.of(
                    ScheduledTaskJob.TASK_ID_KEY, task.getId(),
                    ScheduledTaskJob.TRIGGER_TYPE_KEY, TaskTriggerTypeEnum.MANUAL.name())));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务触发失败", e);
        }
    }

    /**
     * 根据任务 ID 构建 Quartz Job Key。
     *
     * @param taskId 任务 ID
     * @return Job Key
     */
    private static JobKey jobKey(Long taskId) {
        return JobKey.jobKey(TaskQuartzConstants.jobName(taskId), TaskQuartzConstants.GROUP);
    }

    /**
     * 根据任务 Cron 表达式构建 Trigger。
     *
     * @param task 定时任务
     * @return Cron Trigger
     */
    private static Trigger buildTrigger(ScheduledTaskDO task) {
        return TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(TaskQuartzConstants.triggerName(task.getId()), TaskQuartzConstants.GROUP))
                .forJob(jobKey(task.getId()))
                .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                .build();
    }
}
