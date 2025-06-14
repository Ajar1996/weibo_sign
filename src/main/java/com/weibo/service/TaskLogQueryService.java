package com.weibo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weibo.model.dto.ExecutionStatDTO;
import com.weibo.mapper.TaskExecutionLogMapper;
import com.weibo.model.TaskExecutionLog;
import com.weibo.model.req.TaskLogQueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskLogQueryService {
    private final TaskExecutionLogMapper logMapper;

    public Page<TaskExecutionLog> queryLogs(TaskLogQueryParam param, int page, int size) {
        return logMapper.selectByCondition(
                new Page<>(page, size),
                param
        );
    }
    public List<ExecutionStatDTO> getStats() {
        return logMapper.selectStats();
    }
}