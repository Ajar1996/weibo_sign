package com.weibo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "weibo.schedule")
public class ScheduleConfig {
    private int minHour = 8;
    private int maxHour = 15;

    @NestedConfigurationProperty
    private RetryConfig retry = new RetryConfig();

    @Data
    public static class RetryConfig {
        private int delay = 30;
        private int maxAttempts = 3;
    }
}