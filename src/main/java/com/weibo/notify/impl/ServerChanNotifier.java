package com.weibo.notify.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerChanNotifier implements Notifier {
    private final RestTemplate restTemplate;
    private final NotifyTemplateProperties templates;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String[] WEEKDAYS = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

    @Override
    public void notify(WeiboConfig config, String username, List<Topic> signedTopics) {
        if (StringUtils.isBlank(config.getServerKey())) {
            return;
        }

        try {
            // 获取当前日期和星期
            LocalDate now = LocalDate.now();
            String date = now.format(DateTimeFormatter.ofPattern("MM-dd"));
            String weekday = WEEKDAYS[now.getDayOfWeek().getValue() - 1];

            // 统计成功和失败的数量
            long successCount = signedTopics.stream()
                .filter(topic -> "已签".equals(topic.getSignStatus()))
                .count();
            List<Topic> failedTopics = signedTopics.stream()
                .filter(topic -> "签到".equals(topic.getSignStatus()))
                .collect(Collectors.toList());

            // 从YAML加载模板
            String content = templates.getServerchan()
                .replace("${date}", date)
                .replace("${weekday}", weekday)
                .replace("${username}", username)
                .replace("${count}", String.valueOf(successCount))
                .replace("${total}", String.valueOf(signedTopics.size()))
                .replace("${details}", buildDetails(signedTopics, failedTopics));

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("charset", "utf-8");

            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("title", "微博签到通知");
            requestBody.put("desp", content);

            // 创建请求实体
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 构建URL
            String url = "https://sctapi.ftqq.com/" + config.getServerKey() + ".send";
            log.info("发送通知URL: {}", url);

            // 发送POST请求并获取响应
            String response = restTemplate.postForObject(url, requestEntity, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            if (jsonResponse.get("code").asInt() == 0) {
                log.info("Server酱通知已发送成功，pushid: {}", jsonResponse.get("data").get("pushid").asText());
            } else {
                log.error("Server酱通知发送失败: {}", jsonResponse.get("message").asText());
            }
        } catch (Exception e) {
            log.error("Server酱通知异常", e);
        }
    }

    private String buildDetails(List<Topic> allTopics, List<Topic> failedTopics) {
        StringBuilder details = new StringBuilder();
        
        // 添加成功签到的超话
        details.append("成功签到\n\n");
        allTopics.stream()
            .filter(topic -> "已签".equals(topic.getSignStatus()))
            .forEach(topic -> details.append("- ").append(topic.getTitle())
                .append(" (等级: ").append(topic.getLevel()).append(") ✅\n"));

        // 如果有失败的，添加失败的超话
        if (!failedTopics.isEmpty()) {
            details.append("\n签到失败\n\n");
            failedTopics.forEach(topic -> details.append("- ")
                .append(topic.getTitle()).append(" ❌\n"));
        }

        return details.toString();
    }
}