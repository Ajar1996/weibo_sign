package com.weibo.notify.impl;

import com.weibo.config.NotifyTemplateProperties;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import com.weibo.notify.Notifier;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerChanNotifier implements Notifier {
    private final RestTemplate restTemplate;
    private final NotifyTemplateProperties templates;

    @Override
    public void notify(WeiboConfig config, String username, List<Topic> signedTopics) {
        if (StringUtils.isBlank(config.getServerKey())) {
            return;
        }

        try {
            // 从YAML加载模板
            String content = templates.getServerchan()
                .replace("${username}", username)
                .replace("${count}", String.valueOf(signedTopics.size()))
                .replace("${details}", buildDetails(signedTopics));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("title", "微博签到通知");
            body.add("desp", content);

            restTemplate.postForEntity(
                "https://sctapi.ftqq.com/" + config.getServerKey() + ".send",
                new HttpEntity<>(body, headers),
                String.class
            );
        } catch (Exception e) {
            log.error("Server酱通知异常", e);
        }
    }

    private String buildDetails(List<Topic> topics) {
        return topics.stream()
            .map(t -> t.getTitle() + "(Lv." + t.getLevel() + ")")
            .collect(Collectors.joining("、"));
    }
}