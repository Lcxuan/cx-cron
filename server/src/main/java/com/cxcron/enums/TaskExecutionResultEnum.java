package com.cxcron.enums;

public enum TaskExecutionResultEnum {
    SUCCESS,
    FAILED,
    /**
     * 同一任务已有执行实例，本次未执行。
     */
    SKIPPED_CONCURRENT
}
