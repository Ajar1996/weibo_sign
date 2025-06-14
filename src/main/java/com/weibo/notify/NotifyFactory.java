package com.weibo.notify;

import com.weibo.model.WeiboConfig;
import com.weibo.notify.impl.EmailNotifier;
import com.weibo.notify.impl.ServerChanNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotifyFactory {
    private final EmailNotifier emailNotifier;
    private final ServerChanNotifier serverChanNotifier;

    public List<Notifier> createNotifiers(WeiboConfig config) {
        List<Notifier> notifiers = new ArrayList<>();
        
        // 优先邮箱通知
        if (StringUtils.isNotBlank(config.getEmail())) {
            notifiers.add(emailNotifier);
        } 
        // 次选Server酱
        else if (StringUtils.isNotBlank(config.getServerKey())) {
            notifiers.add(serverChanNotifier);
        }
        

        
        return notifiers;
    }
}