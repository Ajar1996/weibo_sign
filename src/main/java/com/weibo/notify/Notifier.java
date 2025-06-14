package com.weibo.notify;

import com.weibo.model.Topic;
import com.weibo.model.WeiboConfig;

import java.util.List;

public interface Notifier {
    /******
     * 发送通知
     * @param config 微博配置
     * @param username 用户名
     * @param signedTopics 已签超话列表
     */
    void notify(WeiboConfig config, String username, List<Topic> signedTopics);
}