package com.cxcron.controller.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "全局邮件配置响应")
public class EmailConfigResp {
    @Schema(description = "是否启用全局邮件通知")
    private Boolean enabled;

    @Schema(description = "全局通知邮件的收件人邮箱")
    private String recipient;

    @Schema(description = "SMTP 邮件发件人邮箱")
    private String fromAddress;

    @Schema(description = "SMTP 服务地址")
    private String smtpHost;

    @Schema(description = "SMTP 服务端口")
    private Integer smtpPort;

    @Schema(description = "SMTP 登录用户名")
    private String smtpUsername;

    @Schema(description = "SMTP 协议")
    private String smtpProtocol;

    @Schema(description = "是否已配置 SMTP 密码；不返回密码明文或密文")
    private Boolean passwordConfigured;
}
