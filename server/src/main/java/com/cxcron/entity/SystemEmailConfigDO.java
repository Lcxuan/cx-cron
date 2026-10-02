package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统邮件配置实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_email_config")
public class SystemEmailConfigDO extends BaseDO {
    /**
     * 是否启用邮件通知。
     */
    private Boolean enabled;

    /**
     * 全局通知收件人。
     */
    private String recipient;

    /**
     * 邮件发件地址。
     */
    private String fromAddress;

    /**
     * SMTP 服务器地址。
     */
    private String smtpHost;

    /**
     * SMTP 服务器端口。
     */
    private Integer smtpPort;

    /**
     * SMTP 用户名。
     */
    private String smtpUsername;

    /**
     * AES-GCM 加密后的 SMTP 密码。
     */
    private String smtpPasswordCiphertext;

    /**
     * SMTP 协议：smtp 或 smtps。
     */
    private String smtpProtocol;
}
