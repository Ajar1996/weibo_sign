package com.weibo.task;

import com.weibo.config.ScheduleConfig;
import com.weibo.mapper.WeiboConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeiboScheduler {
    private final WeiboConfigMapper configMapper;
    private final WeiboTaskExecutor taskExecutor;
    private final ScheduleConfig scheduleConfig; // 注入配置
    
    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE)
    public void initOnStartup() {
        scheduleAllConfigs();
    }

    @Scheduled(cron = "0 0 0 * * ?") 
    public void reloadDaily() {
        scheduleAllConfigs();
    }

    private void scheduleAllConfigs() {
        configMapper.selectAllActive().forEach(config -> {
            LocalTime randomTime = getRandomTime(
                scheduleConfig.getMinHour(), 
                scheduleConfig.getMaxHour()
            );
            // 打印计划时间日志
            log.info("【任务计划】配置ID: {} | 名称: {} | 分配执行时间: {}",
                    config.getId(),
                    config.getName(),
                    randomTime.format(DateTimeFormatter.ofPattern("HH:mm")));

            taskExecutor.schedule(config, randomTime);
        });
    }
    
    private LocalTime getRandomTime(int startHour, int endHour) {
        // 使用注入的配置范围
        int hour = ThreadLocalRandom.current().nextInt(startHour, endHour + 1);
        int minute = ThreadLocalRandom.current().nextInt(0, 60);
        return LocalTime.of(hour, minute);
    }
}