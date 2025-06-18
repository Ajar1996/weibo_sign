package com.weibo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SignExecutor {

    @Autowired
    private WeiboApiClient apiClient;

    private int currentSignedCount = 0;


    public List<Topic> executeSign(String cookie, List<Topic> toSignList, WeiboConfig config) throws IOException {
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



    private void randomSleep(int minSec, int maxSec) {
        try {
            TimeUnit.SECONDS.sleep(minSec + new Random().nextInt(maxSec - minSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}