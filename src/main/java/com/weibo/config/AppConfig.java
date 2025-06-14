package com.weibo.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

// AppConfig.java
@Data // Lombok注解自动生成getter/setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppConfig {
    private String rowUrl;
    private int signOnceCount;
    private String dingSecret;
    private String dingWebhook;
    private String serverKey;
    private String qmsgKey;
    private String isSort;
    private String dispType;
}

