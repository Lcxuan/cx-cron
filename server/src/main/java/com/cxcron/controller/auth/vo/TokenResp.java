package com.cxcron.controller.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 认证令牌响应。
 */
@Schema(description = "认证令牌响应")
@Data
@AllArgsConstructor
public class TokenResp {

    /**
     * 访问令牌。
     */
    @Schema(description = "访问令牌")
    private String accessToken;

    /**
     * 刷新令牌。
     */
    @Schema(description = "刷新令牌")
    private String refreshToken;

    /**
     * 访问令牌有效期，单位：秒。
     */
    @Schema(description = "访问令牌有效期，单位：秒", example = "7200")
    private long accessTokenExpiresIn;

    /**
     * 刷新令牌有效期，单位：秒。
     */
    @Schema(description = "刷新令牌有效期，单位：秒", example = "2592000")
    private long refreshTokenExpiresIn;
}
