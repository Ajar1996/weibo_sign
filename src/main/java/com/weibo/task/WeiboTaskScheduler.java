package com.weibo.task;

import com.weibo.mapper.WeiboConfigMapper;
import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;
import com.weibo.notify.Notifier;
import com.weibo.notify.NotifyFactory;
import com.weibo.service.CookieManager;
import com.weibo.service.SignExecutor;
import com.weibo.service.TopicManager;
import com.weibo.service.WeiboApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeiboTaskScheduler {
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

    @Autowired
    private WeiboApiClient apiClient;
    @Scheduled(initialDelay = 1, fixedRate = Long.MAX_VALUE)
    @Scheduled(cron = "${weibo.sign.cron:0 0 8 * * ?}")
    public void executeSignTask() {
        List<WeiboConfig> activeConfigs = configMapper.selectAllActive();
        if (activeConfigs.isEmpty()) {
            log.info("没有激活的签到配置");
            return;
        }

        activeConfigs.forEach(config -> {
            try {
                executeSingleConfig(config);
                configMapper.updateLastExecuteTime(config.getId());
            } catch (Exception e) {
                handleError(config, e);
            }
        });
    }

    private void executeSingleConfig(WeiboConfig config) throws IOException {
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

    private void handleError(WeiboConfig config, Exception e) {
        log.error("配置[{}]执行失败: {}", config.getName(), e.getMessage());

        // 错误通知（使用相同的通知通道）
        List<Notifier> notifiers = notifyFactory.createNotifiers(config);
        String errorMsg = String.format("签到任务失败\n配置: %s\n错误: %s",
                config.getName(), e.getMessage());

        notifiers.forEach(notifier -> {
            try {
                // 临时构建错误信息（实际项目可定义专门错误模板）
                notifier.notify(config, "系统报警", List.of(
                        new Topic("任务失败", errorMsg,null,null)
                ));
            } catch (Exception ex) {
                log.error("错误通知发送失败", ex);
            }
        });
    }
}