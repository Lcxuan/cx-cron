package com.cxcron.service.execution;

public interface TaskExecutionService {

    void execute(Long taskId, String triggerType, String quartzFireInstanceId);
}
