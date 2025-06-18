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
            @RequestParam(required = false) String weiboName,
            @RequestParam(required = false) String xianyuName,
            @RequestParam(required = false) String logType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        TaskLogQueryParam param = new TaskLogQueryParam();
        param.setWeiboName(weiboName);
        param.setXianyuName(xianyuName);
        param.setLogTypeCode(logType);
        return Result.success(logQueryService.queryLogs(param, page - 1, size));
    }

    @GetMapping("/stats")
    public Result<List<ExecutionStatDTO>> stats() {
        return Result.success(logQueryService.getStats());
    }
}