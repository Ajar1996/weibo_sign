package com.weibo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TopicManager {

    @Autowired
    private WeiboApiClient apiClient;

    private final Map<String, List<Topic>> userTopics = new HashMap<>();

    public List<Topic> getFollowList(String username, String cookie, WeiboConfig config) throws IOException {

        List<Topic> followList = new ArrayList<>();
        String page = "1";
        String sinceId = "";
        int retryCount = 0;
        final int MAX_RETRY = 3;

        while (retryCount < MAX_RETRY) {
            try {
                JsonNode root = apiClient.getTopicList(cookie, page, sinceId);
                JsonNode cards = root.path("cards");
                
                if (cards.isEmpty()) {
                    log.warn("第{}次获取到空数据，页码: {}", retryCount + 1, page);
                    retryCount++;
                    continue;
                }

                processCardGroup(cards, followList);
                JsonNode cardlistInfo = root.path("cardlistInfo");
                sinceId = cardlistInfo.path("since_id").asText();

                if (sinceId.isEmpty() || isLastPage(cards)) {
                    break;
                }

                page = updatePageNumber(page, sinceId);
                retryCount = 0;
                TimeUnit.MILLISECONDS.sleep(3000 + new Random().nextInt(2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("数据获取被中断");
            }
        }

        log.info("用户[{}]获取到{}条超话数据", username, followList.size());
        return followList;
    }

    public List<Topic> prepareSignList(List<Topic> rawList, WeiboConfig config) {
        // 使用config参数获取配置
        String isSort = config.getIsSort();
        int signOnceCount = config.getSignOnceCount();
        if (rawList == null || rawList.isEmpty()) {
            log.warn("输入的超话列表为空");
            return Collections.emptyList();
        }

        // 1. 过滤出未签到的超话
        List<Topic> toSignList = rawList.stream()
                .filter(topic -> !"已签".equals(topic.getSignStatus()))
                .collect(Collectors.toList());

        log.info("待签到超话数: {}/{}", toSignList.size(), rawList.size());

        // 2. 执行排序（使用构造器注入的isSort）
        if ("INCREASE".equalsIgnoreCase(isSort)) {
            log.debug("按等级升序排序");
            toSignList.sort(Comparator.comparingInt(this::extractLevel));
        } else if ("DECREASE".equalsIgnoreCase(isSort)) {
            log.debug("按等级降序排序");
            toSignList.sort((t1, t2) -> extractLevel(t2) - extractLevel(t1));
        }

        // 3. 限制单次签到数量（使用构造器注入的signOnceCount）
        if (toSignList.size() > signOnceCount) {
            log.info("限制签到数量: {} -> {}", toSignList.size(), signOnceCount);
            toSignList = toSignList.subList(0, signOnceCount);
        }

        return toSignList;
    }

    public boolean isAllTopicsSigned(List<Topic> topics) {
        return topics.stream().allMatch(t -> "已签".equals(t.getSignStatus()));
    }

    public void updateUserTopics(String username, List<Topic> topics) {
        userTopics.put(username, topics);
    }

    // 私有辅助方法
    private void processCardGroup(JsonNode cards, List<Topic> followList) {
        for (JsonNode card : cards) {
            JsonNode cardGroup = card.path("card_group");
            for (JsonNode item : cardGroup) {
                if (item.path("card_type").asInt() == 8) {
                    Topic topic = new Topic();
                    topic.setTitle(item.path("title_sub").asText());

                    String desc1 = item.path("desc1").asText();
                    topic.setLevel(desc1.contains("LV") ? 
                            desc1.substring(desc1.indexOf("LV")) : "LV.0");

                    JsonNode buttons = item.path("buttons");
                    if (buttons.size() > 0) {
                        topic.setSignStatus(buttons.get(0).path("name").asText());
                        topic.setSignAction(buttons.get(0).path("params").path("action").asText());
                    }

                    followList.add(topic);
                }
            }
        }
    }

    private boolean isLastPage(JsonNode cards) {
        for (JsonNode card : cards) {
            JsonNode cardGroup = card.path("card_group");
            for (JsonNode item : cardGroup) {
                if (item.path("card_type").asInt() == 6 &&
                        item.path("desc").asText().contains("更多")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String updatePageNumber(String currentPage, String sinceId) {
        try {
            JsonNode node = apiClient.getObjectMapper().readTree(sinceId);
            return node.path("page").asText(currentPage);
        } catch (IOException e) {
            return String.valueOf(Integer.parseInt(currentPage) + 1);
        }
    }

    private int extractLevel(Topic topic) {
        try {
            String levelStr = topic.getLevel().replaceAll("[^0-9]", "");
            return levelStr.isEmpty() ? 0 : Integer.parseInt(levelStr);
        } catch (Exception e) {
            log.warn("解析超话等级失败: {}", topic.getLevel(), e);
            return 0;
        }
    }
}