package com.weibo;


import com.weibo.config.NotifyTemplateProperties;
import lombok.extern.slf4j.Slf4j;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties(NotifyTemplateProperties.class)
public class WeiboSignApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboSignApplication.class, args);
    }

 
}