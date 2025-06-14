package com.weibo;


import com.weibo.config.NotifyTemplateProperties;
import lombok.extern.slf4j.Slf4j;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableConfigurationProperties(NotifyTemplateProperties.class)
public class WeiboSignApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboSignApplication.class, args);
    }

 
}