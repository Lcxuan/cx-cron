package com.cxcron.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 任务执行配置。
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cxcron.task.execution")
public class TaskExecutionProperties {

    /**
     * 单次执行采集的最大日志字节数。
     */
    private int maxLogBytes = 1048576;
}
