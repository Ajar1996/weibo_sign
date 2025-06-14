package com.weibo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "weibo.notify.template")
public class NotifyTemplateProperties {
    private String email;
    private String serverchan;
}