package com.cxcron.controller.task;

import com.cxcron.common.Result;
import com.cxcron.common.pojo.PageResult;
import com.cxcron.controller.task.dto.TaskCreateReq;
import com.cxcron.controller.task.dto.TaskPageReq;
import com.cxcron.controller.task.dto.TaskUpdateReq;
import com.cxcron.controller.task.dto.TaskUpdateStatusReq;
import com.cxcron.controller.task.vo.ExecutionRecordResp;
import com.cxcron.controller.task.vo.ScheduledTaskResp;
import com.cxcron.service.task.ScheduledTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "定时任务")
@RestController
@RequestMapping("/client/tasks")
@RequiredArgsConstructor
public class ScheduledTaskController {

    private final ScheduledTaskService scheduledTaskService;

    @PostMapping
    @Operation(summary = "创建定时任务")
    public Result<Long> create(@Valid @ModelAttribute TaskCreateReq request) {
        return Result.success(scheduledTaskService.create(request));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "更新定时任务")
    public Result<Void> update(@PathVariable("taskId") Long taskId, @Valid @ModelAttribute TaskUpdateReq request) {
        scheduledTaskService.update(taskId, request);
        return Result.success();
    }

    @PutMapping("/{taskId}/status")
    @Operation(summary = "更新任务启用状态")
    public Result<Void> updateStatus(@PathVariable("taskId") Long taskId, @Valid @RequestBody TaskUpdateStatusReq request) {
        scheduledTaskService.updateStatus(taskId, request);
        return Result.success();
    }

    @DeleteMapping("/{taskId}")
    public Result<Void> delete(@PathVariable("taskId") Long taskId) {
        scheduledTaskService.delete(taskId);
        return Result.success();
    }

    @PostMapping("/{taskId}/trigger")
    public Result<Void> trigger(@PathVariable("taskId") Long taskId) {
        scheduledTaskService.trigger(taskId);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<ScheduledTaskResp>> getPage(@Valid TaskPageReq request) {
        return Result.success(scheduledTaskService.getPage(request));
    }

    @GetMapping("/{taskId}/executions")
    public Result<PageResult<ExecutionRecordResp>> getExecutionPage(@PathVariable("taskId") Long taskId, @Valid TaskPageReq request) {
        return Result.success(scheduledTaskService.getExecutionPage(taskId, request));
    }
}
