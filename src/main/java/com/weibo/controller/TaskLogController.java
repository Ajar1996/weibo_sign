package com.weibo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weibo.model.TaskExecutionLog;
import com.weibo.service.TaskLogQueryService;
import com.weibo.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/task-logs")
@RequiredArgsConstructor
public class TaskLogController {
    private final TaskLogQueryService logQueryService;

    @GetMapping
    public Result<Page<TaskExecutionLog>> list(
            @RequestParam(required = false) Integer configId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(logQueryService.queryLogs(configId, page, size));
    }

    @GetMapping("/stats")
    public Result<List<ExecutionStatDTO>> stats() {
        return Result.success(logQueryService.getStats());
    }
}