package com.weibo.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

// ConfigLoader.java
public class ConfigLoader {
    private static final String CONFIG_PATH = "config.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static AppConfig loadConfig() throws IOException {
        InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
        return mapper.readValue(is, AppConfig.class);
    }
}