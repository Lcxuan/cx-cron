package com.cxcron.service.execution.impl;

import com.cxcron.entity.ExecutionRecordDO;
import com.cxcron.entity.ScheduledTaskDO;
import com.cxcron.enums.TaskExecutionResultEnum;
import com.cxcron.mapper.ExecutionRecordMapper;
import com.cxcron.mapper.ScheduledTaskMapper;
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

    @Override
    public void execute(Long taskId, String triggerType, String quartzFireInstanceId) {
        LocalDateTime startedTime = LocalDateTime.now();
        RLock lock = redissonClient.getLock("task-" + taskId);
        boolean locked = lock.tryLock();
        String summary = "任务正在执行";
        String resultType = TaskExecutionResultEnum.FAILED.name();
        try {
            if (locked) {
                ScheduledTaskDO task = scheduledTaskMapper.selectById(taskId);
                summary = task == null || task.getScriptPath() == null || !Files.isRegularFile(Path.of(task.getScriptPath()))
                        ? "脚本不存在" : scriptCommandExecutor.execute(task.getRunCommand(), Path.of(task.getScriptPath()));
                resultType = "执行成功".equals(summary) ? TaskExecutionResultEnum.SUCCESS.name() : TaskExecutionResultEnum.FAILED.name();
            }
            saveRecord(taskId, triggerType, quartzFireInstanceId, startedTime, resultType, summary);
            updateLastSchedule(taskId, resultType, startedTime);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    private void saveRecord(Long taskId, String triggerType, String quartzFireInstanceId, LocalDateTime startedTime,
                            String resultType, String summary) {
        ExecutionRecordDO record = new ExecutionRecordDO();
        record.setScheduledTaskId(taskId); record.setTriggerType(triggerType);
        record.setIdempotencyKey(taskId + "-" + startedTime.toLocalDate()); record.setQuartzFireInstanceId(quartzFireInstanceId);
        record.setResultType(resultType); record.setSummary(summary); record.setStartedTime(startedTime);
        record.setFinishedTime(LocalDateTime.now()); executionRecordMapper.insert(record);
    }

    private void updateLastSchedule(Long taskId, String resultType, LocalDateTime scheduleTime) {
        ScheduledTaskDO task = new ScheduledTaskDO();
        task.setId(taskId); task.setLastScheduleStatus(resultType); task.setLastScheduleTime(scheduleTime);
        scheduledTaskMapper.updateById(task);
    }
}
