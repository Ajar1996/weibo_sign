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
    private final WeiboConfigMapper configMapper;
    private final CookieManager cookieManager;
    private final TopicManager topicManager;
    private final SignExecutor signExecutor;
    private final NotifyFactory notifyFactory; // 替换原来的Notifier
    private final WeiboApiClient apiClient;

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
        TopicManager configAwareTopicManager = new TopicManager(apiClient, config);
        Map<String, String> userCookies = cookieManager.loadCookiesFromConfig(config);

        userCookies.forEach((username, cookie) -> {
            try {
                List<Topic> followList = configAwareTopicManager.getFollowList(username, cookie);

                if (configAwareTopicManager.isAllTopicsSigned(followList)) {
                    log.info("用户[{}]所有超话已签到，跳过", username);
                    return;
                }

                List<Topic> toSignList = configAwareTopicManager.prepareSignList(followList);
                List<Topic> signedList = signExecutor.executeSign(cookie, toSignList);
                configAwareTopicManager.updateUserTopics(username, signedList);

                sendNotification(config, username, signedList);
            } catch (Exception e) {
                throw new RuntimeException("用户[" + username + "]签到失败", e);
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