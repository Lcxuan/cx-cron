package com.cxcron.controller.system;

import com.cxcron.common.Result;
import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.system.dto.EmailNotificationLogPageReq;
import com.cxcron.controller.system.dto.EmailConfigReq;
import com.cxcron.controller.system.vo.EmailNotificationLogResp;
import com.cxcron.controller.system.vo.EmailConfigResp;
import com.cxcron.service.email.TaskEmailNotificationLogService;
import com.cxcron.service.email.SystemEmailConfigService;
import com.cxcron.service.email.TaskEmailNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统邮件通知")
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/system/email-notification")
public class SystemEmailNotificationController {
    private final SystemEmailConfigService configService;
    private final TaskEmailNotificationService notificationService;
    private final TaskEmailNotificationLogService logService;

    @GetMapping("/logs")
    @Operation(summary = "分页查询邮件日志")
    public Result<PageResult<EmailNotificationLogResp>> getLogs(@Valid EmailNotificationLogPageReq request) {
        return Result.success(logService.getPage(request));
    }

    @GetMapping("/config")
    @Operation(summary = "获取邮件配置")
    public Result<EmailConfigResp> getConfig() {
        return Result.success(configService.get());
    }

    @PutMapping("/config")
    @Operation(summary = "保存邮件配置")
    public Result<Void> saveConfig(@Valid @RequestBody EmailConfigReq request) {
        configService.save(request);
        return Result.success();
    }

    @PostMapping("/test")
    @Operation(summary = "发送测试邮件")
    public Result<Void> sendTest() {
        notificationService.sendTest();
        return Result.success();
    }
}
