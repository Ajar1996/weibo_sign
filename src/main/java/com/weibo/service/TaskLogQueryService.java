package com.weibo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weibo.model.dto.ExecutionStatDTO;
import com.weibo.mapper.TaskExecutionLogMapper;
import com.weibo.model.TaskExecutionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskLogQueryService {
    private final TaskExecutionLogMapper logMapper;

    public Page<TaskExecutionLog> queryLogs(Integer configId, int page, int size) {
        LambdaQueryWrapper<TaskExecutionLog> query = new LambdaQueryWrapper<>();
        query.eq(configId != null, TaskExecutionLog::getConfigId, configId)
             .orderByDesc(TaskExecutionLog::getCreateTime);
        return logMapper.selectPage(new Page<>(page, size), query);
    }

    public List<ExecutionStatDTO> getStats() {
        return logMapper.selectStats();
    }
}