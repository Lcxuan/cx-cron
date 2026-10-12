package com.cxcron.service.quartz.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cxcron.common.enums.CommonStatusEnum;
import com.cxcron.entity.ScheduledTaskDO;
import com.cxcron.enums.TaskScheduleTypeEnum;
import com.cxcron.enums.TaskTriggerTypeEnum;
import com.cxcron.mapper.ScheduledTaskMapper;
import com.cxcron.quartz.RandomDailySchedule;
import com.cxcron.quartz.ScheduledTaskJob;
import com.cxcron.quartz.TaskQuartzConstants;
import com.cxcron.service.execution.TaskExecutionService;
import com.cxcron.service.quartz.QuartzTaskService;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
            scheduler.getContext().put(TaskQuartzConstants.QUARTZ_TASK_SERVICE_KEY, this);
            List<ScheduledTaskDO> tasks = scheduledTaskMapper.selectList(new LambdaQueryWrapper<ScheduledTaskDO>()
                    .eq(ScheduledTaskDO::getEnabled, CommonStatusEnum.ENABLE.getCode()));
            tasks.forEach(this::recover);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务恢复失败", e);
        }
    }

    @Override
    public void schedule(ScheduledTaskDO task) {
        try {
            JobKey jobKey = jobKey(task.getId());
            Trigger trigger = buildTrigger(task, LocalDateTime.now());
            if (scheduler.checkExists(jobKey)) {
                replaceTrigger(trigger);
                scheduler.resumeJob(jobKey);
                return;
            }
            scheduler.scheduleJob(buildJob(task), trigger);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务调度失败", e);
        }
    }

    @Override
    public void recover(ScheduledTaskDO task) {
        try {
            JobKey jobKey = jobKey(task.getId());
            TriggerKey triggerKey = triggerKey(task.getId());
            if (TaskScheduleTypeEnum.RANDOM_DAILY.name().equals(task.getScheduleType())) {
                if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    if (trigger != null && trigger.getNextFireTime() != null && trigger.getNextFireTime().after(new Date())) {
                        scheduler.resumeJob(jobKey);
                        return;
                    }
                }
                Trigger trigger = randomTrigger(task, RandomDailySchedule.nextDayExecutionTime(
                        task.getRandomStartTime(), task.getRandomEndTime(), LocalDateTime.now()));
                if (scheduler.checkExists(jobKey)) {
                    replaceTrigger(trigger);
                    scheduler.resumeJob(jobKey);
                } else {
                    scheduler.scheduleJob(buildJob(task), trigger);
                }
                return;
            }
            schedule(task);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 任务恢复失败", e);
        }
    }

    @Override
    public void scheduleNextRandom(Long taskId, LocalDateTime scheduledAt) {
        ScheduledTaskDO task = scheduledTaskMapper.selectById(taskId);
        if (task == null || !CommonStatusEnum.isEnabled(task.getEnabled())
                || !TaskScheduleTypeEnum.RANDOM_DAILY.name().equals(task.getScheduleType())) return;
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime earliest = scheduledAt.toLocalDate().plusDays(1).atStartOfDay();
            if (now.isAfter(earliest)) earliest = now;
            LocalDateTime next = RandomDailySchedule.nextExecutionTime(
                    task.getRandomStartTime(), task.getRandomEndTime(), earliest);
            Trigger trigger = randomTrigger(task, next);
            TriggerKey triggerKey = triggerKey(taskId);
            Trigger existing = scheduler.getTrigger(triggerKey);
            if (existing != null && existing.getNextFireTime() != null && existing.getNextFireTime().after(new Date())) return;
            if (scheduler.checkExists(triggerKey)) {
                scheduler.rescheduleJob(triggerKey, trigger);
            } else {
                scheduler.scheduleJob(trigger);
            }
        } catch (SchedulerException e) {
            throw new IllegalStateException("Quartz 每日随机调度失败", e);
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

    private void replaceTrigger(Trigger trigger) throws SchedulerException {
        TriggerKey triggerKey = trigger.getKey();
        if (scheduler.checkExists(triggerKey)) {
            scheduler.rescheduleJob(triggerKey, trigger);
        } else {
            scheduler.scheduleJob(trigger);
        }
    }

    private static JobDetail buildJob(ScheduledTaskDO task) {
        return JobBuilder.newJob(ScheduledTaskJob.class)
                .withIdentity(jobKey(task.getId()))
                .usingJobData(new JobDataMap(Map.of(ScheduledTaskJob.TASK_ID_KEY, task.getId())))
                .build();
    }

    private static Trigger buildTrigger(ScheduledTaskDO task, LocalDateTime now) {
        if (TaskScheduleTypeEnum.RANDOM_DAILY.name().equals(task.getScheduleType())) {
            LocalDateTime next = RandomDailySchedule.nextExecutionTime(
                    task.getRandomStartTime(), task.getRandomEndTime(), now);
            return randomTrigger(task, next);
        }
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey(task.getId()))
                .forJob(jobKey(task.getId()))
                .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                .build();
    }

    private static Trigger randomTrigger(ScheduledTaskDO task, LocalDateTime next) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey(task.getId()))
                .forJob(jobKey(task.getId()))
                .startAt(Date.from(next.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                .build();
    }

    private static JobKey jobKey(Long taskId) {
        return JobKey.jobKey(TaskQuartzConstants.jobName(taskId), TaskQuartzConstants.GROUP);
    }

    private static TriggerKey triggerKey(Long taskId) {
        return TriggerKey.triggerKey(TaskQuartzConstants.triggerName(taskId), TaskQuartzConstants.GROUP);
    }
}
