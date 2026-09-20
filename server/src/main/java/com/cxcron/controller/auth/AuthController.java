package com.cxcron.controller.auth;

import com.cxcron.common.Result;
import com.cxcron.controller.auth.dto.LoginReq;
import com.cxcron.controller.auth.dto.RefreshTokenReq;
import com.cxcron.controller.auth.vo.CurrentUserResp;
import com.cxcron.controller.auth.vo.TokenResp;
import com.cxcron.service.auth.AuthService;
import com.cxcron.service.token.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理员认证")
@RestController
@RequestMapping("/client/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @return 认证令牌
     */
    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<TokenResp> login(@Valid @RequestBody LoginReq request) {
        return Result.success(authService.login(request));
    }

    /**
     * 刷新访问令牌。
     *
     * @param request 刷新令牌请求
     * @return 新认证令牌
     */
    @Operation(summary = "刷新访问令牌")
    @PostMapping("/refresh")
    public Result<TokenResp> refresh(@Valid @RequestBody RefreshTokenReq request) {
        return Result.success(tokenService.refreshTokens(request.getRefreshToken()));
    }

    /**
     * 退出登录。
     *
     * @return 空响应
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        tokenService.logout();
        return Result.success();
    }

    /**
     * 获取当前管理员信息。
     *
     * @return 当前管理员信息
     */
    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/me")
    public Result<CurrentUserResp> me() {
        return Result.success(authService.getCurrentUser());
    }
}
