package com.cxcron.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改管理员密码请求。
 */
@Schema(description = "修改管理员密码请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordReq {

    /**
     * RSA 加密后的当前密码。
     */
    @NotBlank(message = "当前密码不能为空")
    @Schema(description = "RSA 加密后的当前密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    /**
     * RSA 加密后的新密码。
     */
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "RSA 加密后的新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
