package com.cxcron.controller.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailConfigReq {
    @Schema(description = "是否启用全局邮件通知")
    private Boolean enabled;

    @Schema(description = "全局通知邮件的收件人邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    private String recipient;

    @Schema(description = "SMTP 邮件的发件人邮箱；为空时使用 SMTP 用户名")
    @Email
    private String fromAddress;

    @Schema(description = "SMTP 服务地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String smtpHost;

    @Schema(description = "SMTP 服务端口，范围为 1–65535", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    @Max(65535)
    private Integer smtpPort;

    @Schema(description = "SMTP 登录用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String smtpUsername;

    @Schema(description = "SMTP 登录密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String smtpPassword;

    @Schema(description = "SMTP 协议，仅支持 smtp 或 smtps", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"smtp", "smtps"})
    @NotBlank
    @Pattern(regexp = "(?i)smtp|smtps")
    private String smtpProtocol;
}
