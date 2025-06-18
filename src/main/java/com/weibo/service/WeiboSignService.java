package com.weibo.service;

import com.weibo.mapper.WeiboConfigMapper;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import com.weibo.notify.Notifier;
import com.weibo.notify.NotifyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeiboSignService {
    @Autowired
    private WeiboConfigMapper configMapper;

    @Autowired
    private CookieManager cookieManager;

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private SignExecutor signExecutor;

    @Autowired
    private NotifyFactory notifyFactory;


    public void weiBoSign(WeiboConfig config) {
        Map<String, String> userCookies = cookieManager.loadCookiesFromConfig(config);

        userCookies.forEach((username, cookie) -> {
            try {
                // 直接使用注入的topicManager
                List<Topic> followList = topicManager.getFollowList(username, cookie, config);

                if (topicManager.isAllTopicsSigned(followList)) {
                    log.info("跳过已签用户: {}", username);
                    return;
                }

                List<Topic> toSignList = topicManager.prepareSignList(followList, config);
                List<Topic> signedList = signExecutor.executeSign(cookie, toSignList, config);
                topicManager.updateUserTopics(username, signedList);

                sendNotification(config, username, signedList);
            } catch (Exception e) {
                throw new RuntimeException("用户签到失败: " + username, e);
            }
        });
    }

    private void sendNotification(WeiboConfig config, String username, List<Topic> signedList) {
        // 通过工厂获取适合当前配置的通知器
        List<Notifier> notifiers = notifyFactory.createNotifiers(config);

        notifiers.forEach(notifier -> {
            try {
                notifier.notify(config, username, signedList);
            } catch (Exception e) {
                log.error("通知发送失败", e);
            }
        });
    }


}