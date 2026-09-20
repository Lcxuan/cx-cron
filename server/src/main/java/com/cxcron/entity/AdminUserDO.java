package com.cxcron.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxcron.common.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_users")
public class AdminUserDO extends BaseDO {

    /**
     * 登录用户名。
     */
    private String username;

    /**
     * 密码哈希。
     */
    private String password;

    /**
     * 显示名称。
     */
    private String nickname;

    /**
     * 状态：0-启用，1-禁用。
     */
    private Integer status;

    /**
     * 最后登录时间。
     */
    private LocalDateTime lastLoginTime;
}
