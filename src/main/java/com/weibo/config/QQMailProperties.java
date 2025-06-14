package com.weibo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mail.qq") // 配置前缀
public class QQMailProperties {
    private boolean enabled = false;
    private String from;
    private String username;
    private String password; // 建议搭配配置中心加密存储
    private String host = "smtp.qq.com";
    private int port = 587;
    private String protocol = "smtp";
    private String defaultEncoding = "UTF-8";
}