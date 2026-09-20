package com.cxcron.controller.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 当前管理员信息响应。
 */
@Schema(description = "当前管理员信息")
@Data
@AllArgsConstructor
public class CurrentUserResp {

    /**
     * 管理员 ID。
     */
    @Schema(description = "管理员 ID", example = "1")
    private Long id;

    /**
     * 登录用户名。
     */
    @Schema(description = "登录用户名", example = "admin")
    private String username;

    /**
     * 显示名称。
     */
    @Schema(description = "显示名称", example = "管理员")
    private String nickname;
}
