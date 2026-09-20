package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("refresh_tokens")
public class RefreshTokenDO extends BaseDO {

    /**
     * 用户 ID。
     */
    private Long userId;

    /**
     * 用户类型。
     */
    private String userType;

    /**
     * 原始 Refresh Token。
     */
    private String refreshToken;

    /**
     * 过期时间。
     */
    private LocalDateTime expiresTime;
}
