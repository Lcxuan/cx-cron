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
        // 记录本次执行的开始时间，并使用任务 ID 获取分布式锁，避免同一任务并发执行。
        LocalDateTime startedTime = LocalDateTime.now();
        RLock lock = redissonClient.getLock("task-" + taskId);
        boolean locked = lock.tryLock();
        String summary = "任务正在执行";
        String resultType = TaskExecutionResultEnum.FAILED.name();
        try {
            // 查询任务配置；只有成功获取锁且脚本文件有效时才执行命令。
            ScheduledTaskDO task = scheduledTaskMapper.selectById(taskId);
            if (locked) {
                summary = task == null || task.getScriptPath() == null || !Files.isRegularFile(Path.of(task.getScriptPath()))
                        ? "脚本不存在" : scriptCommandExecutor.execute(task.getRunCommand(), Path.of(task.getScriptPath()));
                resultType = "执行成功".equals(summary) ? TaskExecutionResultEnum.SUCCESS.name() : TaskExecutionResultEnum.FAILED.name();
            }
            // 保存本次执行结果并更新任务最近执行状态。
            ExecutionRecordDO record = saveRecord(taskId, triggerType, quartzFireInstanceId, startedTime, resultType, summary);
            updateLastSchedule(taskId, resultType, startedTime);
            // 任务记录存在时通知邮件服务；通知异常不影响任务执行结果。
            if (task != null) {
                try {
                    emailNotificationService.notifyAfterExecution(task, record);
                } catch (Exception ignored) {
                }
            }
        } finally {
            // 仅释放当前线程持有的锁。
            if (locked && lock.isHeldByCurrentThread()) lock.unlock();
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
     * @return 已保存的执行记录
     */
    private ExecutionRecordDO saveRecord(Long taskId, String triggerType, String quartzFireInstanceId, LocalDateTime startedTime,
                            String resultType, String summary) {
        ExecutionRecordDO record = new ExecutionRecordDO();
        record.setScheduledTaskId(taskId); record.setTriggerType(triggerType);
        record.setIdempotencyKey(taskId + "-" + startedTime.toLocalDate()); record.setQuartzFireInstanceId(quartzFireInstanceId);
        record.setResultType(resultType); record.setSummary(summary); record.setStartedTime(startedTime);
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
