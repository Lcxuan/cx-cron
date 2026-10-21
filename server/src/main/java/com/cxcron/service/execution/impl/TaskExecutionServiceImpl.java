package com.cxcron.service.execution.impl;

import com.cxcron.entity.ExecutionRecordDO;
import com.cxcron.entity.ScheduledTaskDO;
import com.cxcron.enums.TaskExecutionResultEnum;
import com.cxcron.mapper.ExecutionRecordMapper;
import com.cxcron.mapper.ScheduledTaskMapper;
import com.cxcron.service.email.TaskEmailNotificationService;
import com.cxcron.service.execution.ScriptCommandExecutor;
import com.cxcron.service.execution.TaskExecutionService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final RedissonClient redissonClient;

    private final ExecutionRecordMapper executionRecordMapper;

    private final ScheduledTaskMapper scheduledTaskMapper;

    private final ScriptCommandExecutor scriptCommandExecutor;

    private final TaskEmailNotificationService emailNotificationService;

    @Override
    public void execute(Long taskId, String triggerType, String quartzFireInstanceId) {
        LocalDateTime startedTime = LocalDateTime.now();
        RLock lock = redissonClient.getLock("task-" + taskId);
        boolean locked = lock.tryLock();
        String summary = "任务正在执行中，本次跳过";
        String executionLog = "";
        String resultType = TaskExecutionResultEnum.SKIPPED_CONCURRENT.name();
        ScheduledTaskDO task = null;
        try {
            task = scheduledTaskMapper.selectById(taskId);
            if (locked) {
                if (task == null || task.getScriptPath() == null
                        || !Files.isRegularFile(Path.of(task.getScriptPath()))) {
                    summary = task == null ? "任务不存在" : "脚本不存在";
                } else {
                    ScriptCommandExecutor.ExecutionResult result = scriptCommandExecutor.execute(
                            task.getRunCommand(), Path.of(task.getScriptPath()));
                    summary = result.summary();
                    executionLog = result.executionLog();
                }
                resultType = "执行成功".equals(summary)
                        ? TaskExecutionResultEnum.SUCCESS.name()
                        : TaskExecutionResultEnum.FAILED.name();
            }

            // 无论脚本是否实际执行，都记录触发结果；并发跳过不会伪装成失败。
            ExecutionRecordDO record = saveRecord(taskId, triggerType, quartzFireInstanceId,
                    startedTime, resultType, summary, executionLog);
            updateLastSchedule(taskId, resultType, startedTime);
            // 只有真正执行过脚本的结果才触发任务邮件通知。
            if (locked && task != null) {
                try {
                    emailNotificationService.notifyAfterExecution(task, record);
                } catch (Exception ignored) {
                }
            }
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 创建并保存任务执行记录。
     *
     * @param taskId 定时任务 ID
     * @param triggerType 触发方式
     * @param quartzFireInstanceId Quartz 触发实例 ID
     * @param startedTime 执行开始时间
     * @param resultType 执行结果
     * @param summary 执行摘要
     * @param executionLog 命令执行日志
     * @return 已保存的执行记录
     */
    private ExecutionRecordDO saveRecord(Long taskId, String triggerType, String quartzFireInstanceId, LocalDateTime startedTime,
                            String resultType, String summary, String executionLog) {
        ExecutionRecordDO record = new ExecutionRecordDO();
        record.setScheduledTaskId(taskId); record.setTriggerType(triggerType);
        record.setIdempotencyKey(taskId + "-" + startedTime.toLocalDate()); record.setQuartzFireInstanceId(quartzFireInstanceId);
        record.setResultType(resultType); 
        record.setSummary(summary); 
        record.setExecutionLog(executionLog); 
        record.setStartedTime(startedTime);
        record.setFinishedTime(LocalDateTime.now()); executionRecordMapper.insert(record);
        return record;
    }

    /**
     * 更新任务最近一次执行的状态和时间。
     *
     * @param taskId 定时任务 ID
     * @param resultType 本次执行结果
     * @param scheduleTime 本次执行开始时间
     */
    private void updateLastSchedule(Long taskId, String resultType, LocalDateTime scheduleTime) {
        ScheduledTaskDO task = new ScheduledTaskDO();
        task.setId(taskId); task.setLastScheduleStatus(resultType); task.setLastScheduleTime(scheduleTime);
        scheduledTaskMapper.updateById(task);
    }
}
