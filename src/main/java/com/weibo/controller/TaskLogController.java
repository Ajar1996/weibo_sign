package com.weibo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weibo.model.dto.ExecutionStatDTO;
import com.weibo.model.TaskExecutionLog;
import com.weibo.model.req.TaskLogQueryParam;
import com.weibo.service.TaskLogQueryService;
import com.weibo.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/task-logs")
@RequiredArgsConstructor
public class TaskLogController {
    private final TaskLogQueryService logQueryService;

    @GetMapping
    public Result<Page<TaskExecutionLog>> list(
            @ModelAttribute TaskLogQueryParam param,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(logQueryService.queryLogs(param, page, size));
    }

    @GetMapping("/stats")
    public Result<List<ExecutionStatDTO>> stats() {
        return Result.success(logQueryService.getStats());
    }
}