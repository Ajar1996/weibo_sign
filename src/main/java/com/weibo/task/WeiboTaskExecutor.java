package com.weibo.task;

import com.weibo.config.ScheduleConfig;
import com.weibo.constant.TaskLogType;
import com.weibo.model.WeiboConfig;
import com.weibo.service.TaskLogService;
import com.weibo.service.WeiboSignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
@Slf4j
public class WeiboTaskExecutor {
    private final TaskScheduler taskScheduler;
    private final WeiboSignService signService;
    private final ScheduleConfig scheduleConfig;
    private final TaskLogService taskLogService;

    // 任务执行记录器
    private final Map<Integer, Integer> attemptCounts = new ConcurrentHashMap<>();

    public void schedule(WeiboConfig config, LocalTime executeTime) {
        LocalDateTime scheduledTime = LocalDateTime.now()
                .withHour(executeTime.getHour())
                .withMinute(executeTime.getMinute())
                .withSecond(0);

        // 记录调度日志
        taskLogService.recordLog(config, TaskLogType.SCHEDULED.getCode(), 0, scheduledTime, null, null, null);

        String cron = String.format("0 %d %d * * ?",
                executeTime.getMinute(),
                executeTime.getHour());

        taskScheduler.schedule(
                () -> executeWithRetry(config, 0),
                new CronTrigger(cron)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void executeWithRetry(WeiboConfig config, int currentAttempt) {
        LocalDateTime startTime = LocalDateTime.now();
        taskLogService.recordLog(config, TaskLogType.EXECUTING.getCode(), currentAttempt+1, null, startTime, null, null);

        try {
            signService.weiBoSign(config);
            attemptCounts.remove(config.getId());

            LocalDateTime endTime = LocalDateTime.now();
            taskLogService.recordLog(config, TaskLogType.SUCCESS.getCode(), currentAttempt+1, null,
                    startTime, endTime, null);

        } catch (Exception e) {
            handleFailure(config, currentAttempt, startTime, e);
        }
    }

    private void handleFailure(WeiboConfig config, int currentAttempt,
                               LocalDateTime startTime, Exception e) {
        int maxAttempts = scheduleConfig.getRetry().getMaxAttempts();
        int delayMinutes = scheduleConfig.getRetry().getDelay();

        if (currentAttempt < maxAttempts) {
            int nextAttempt = currentAttempt + 1;
            attemptCounts.put(config.getId(), nextAttempt);

            LocalDateTime retryTime = LocalDateTime.now().plusMinutes(delayMinutes);
            taskLogService.recordLog(config, TaskLogType.RETRYING.getCode(), nextAttempt,
                    retryTime, startTime, null, e.getMessage());

            taskScheduler.schedule(
                    () -> executeWithRetry(config, nextAttempt),
                    new CronTrigger(buildRetryCron(retryTime))
            );
        } else {
            LocalDateTime endTime = LocalDateTime.now();
            taskLogService.recordLog(config, TaskLogType.FAILED.getCode(), currentAttempt+1,
                    null, startTime, endTime, e.getMessage());
            attemptCounts.remove(config.getId());
        }
    }

    private String buildRetryCron(LocalDateTime retryTime) {
        return String.format("0 %d %d %d %d ?",
                retryTime.getMinute(),
                retryTime.getHour(),
                retryTime.getDayOfMonth(),
                retryTime.getMonthValue());
    }
}