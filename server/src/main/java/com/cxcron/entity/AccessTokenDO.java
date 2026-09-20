package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("access_tokens")
public class AccessTokenDO extends BaseDO {

    /**
     * 用户 ID。
     */
    private Long userId;

    /**
     * 用户类型。
     */
    private String userType;

    /**
     * 刷新令牌 ID。
     */
    private Long refreshTokenId;

    /**
     * 原始 Access Token。
     */
    private String accessToken;

    /**
     * 过期时间。
     */
    private LocalDateTime expiresTime;
}
