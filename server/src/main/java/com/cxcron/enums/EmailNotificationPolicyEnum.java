package com.cxcron.enums;

/**
 * 任务邮件通知策略。
 */
public enum EmailNotificationPolicyEnum {
    /**
     * 不发送任务邮件通知。
     */
    OFF,

    /**
     * 仅任务执行失败时发送通知。
     */
    FAILURE,

    /**
     * 无论任务执行结果如何都发送通知。
     */
    ALL;

    /**
     * 解析通知策略；未指定时默认通知所有执行结果。
     *
     * @param policy 策略名称
     * @return 通知策略
     */
    public static EmailNotificationPolicyEnum resolve(String policy) {
        return policy == null ? ALL : valueOf(policy);
    }

    /**
     * 根据通知策略和执行结果判断是否发送邮件。
     *
     * @param resultType 任务执行结果类型
     * @return 是否发送邮件通知
     */
    public boolean shouldNotify(String resultType) {
        return this == ALL || this == FAILURE && TaskExecutionResultEnum.FAILED.name().equals(resultType);
    }
}
