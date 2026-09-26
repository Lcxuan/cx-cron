package com.cxcron.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录请求。
 */
@Schema(description = "管理员登录请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {

    /**
     * 登录用户名。
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "登录用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * RSA 加密后的登录密码。
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "RSA 加密后的登录密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
