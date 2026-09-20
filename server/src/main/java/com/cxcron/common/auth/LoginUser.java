package com.cxcron.common.auth;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前登录管理员身份。
 */
@Data
public class LoginUser implements Serializable {

    /**
     * 用户 ID。
     */
    private Long userId;

    /**
     * 用户类型。
     */
    private String userType;
}
