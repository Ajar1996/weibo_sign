package com.weibo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weibo.config.AppConfig;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SignExecutor {
    private final WeiboApiClient apiClient;
    private final WeiboConfig config;
    private int currentSignedCount = 0;

    public SignExecutor(WeiboApiClient apiClient, WeiboConfig config) {
        this.apiClient = apiClient;
        this.config = config;
    }

    public List<Topic> executeSign(String cookie, List<Topic> toSignList) throws IOException {
        List<Topic> signedList = new ArrayList<>();

        for (Topic topic : toSignList) {
            if (currentSignedCount >= config.getSignOnceCount()) break;

            if ("已签".equals(topic.getSignStatus())) {
                log.info(topic.getTitle() + " already signed");
                continue;
            }

            JsonNode result = apiClient.signTopic(cookie, topic.getSignAction());
            if ("已签到".equals(result.path("msg").asText())) {
                topic.setSignStatus("已签");
                currentSignedCount++;
                log.info(topic.getTitle() + " signed successfully");
            }
            signedList.add(topic);
            randomSleep(15, 30);
        }
        return signedList;
    }

    public boolean isAllUsersSigned() {
        return currentSignedCount >= config.getSignOnceCount();
    }

    private void randomSleep(int minSec, int maxSec) {
        try {
            TimeUnit.SECONDS.sleep(minSec + new Random().nextInt(maxSec - minSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}