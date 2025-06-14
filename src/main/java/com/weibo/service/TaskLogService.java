package com.weibo.service;

import com.weibo.mapper.TaskExecutionLogMapper;
import com.weibo.model.TaskExecutionLog;
import com.weibo.model.WeiboConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskLogService {
    private final TaskExecutionLogMapper logMapper;

    @Transactional(rollbackFor = Exception.class)
    public void recordLog(WeiboConfig config, String executionType,
                          Integer attempt, LocalDateTime scheduledTime,
                          LocalDateTime startTime, LocalDateTime endTime,
                          String errorMsg) {
        TaskExecutionLog log = new TaskExecutionLog();
        log.setConfigId(config.getId());
        log.setConfigName(config.getName());
        log.setExecutionType(executionType);
        log.setAttemptNumber(attempt);
        log.setScheduledTime(scheduledTime);
        log.setActualStartTime(startTime);
        log.setEndTime(endTime);
        
        if (startTime != null && endTime != null) {
            log.setDurationSeconds(Duration.between(startTime, endTime).getSeconds());
        }
        
        log.setErrorMessage(errorMsg);
        logMapper.insert(log);
    }
}