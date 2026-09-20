package com.cxcron.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刷新令牌请求。
 */
@Schema(description = "刷新令牌请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenReq {

    /**
     * 刷新令牌。
     */
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
