package com.weibo.service;

import com.weibo.model.WeiboConfig;
import com.weibo.model.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class WeiboSignServiceTest {

    @Autowired
    private WeiboSignService signService;

    private WeiboConfig config;

    @BeforeEach
    void setUp() {
        // 设置测试配置
        config = new WeiboConfig();
        config.setId(1);
        config.setName("测试配置");
        config.setRowUrl("https://api.weibo.cn/2/cardlist?aid=01A8vEx3Dot4keenEPVXPe4amUH0z_P7BrosagmyPJ-EwP264.&c=weicoabroad&containerid=100803_-_followsuper&count=20&from=12E5093010&gsid=_2A25K0iy_DeRxGeBP71AS8i_MyTqIHXVnxid3rDV6PUJbj9AbLXP_kWpNRVJncoMDvdK-2s9F5qNWK0CuTOMuWI4R&lang=zh_CN&page=1&s=56222222&ua=iPhone14%2C2_iOS18.3.2_Weibo_intl._6680_wifi__iphone__os18.3.2&v_p=90");
        config.setSignOnceCount(8);
        config.setServerKey("SCT273149TKcuZJH5X90Ui5wWyaYnq3g0i");
        config.setIsSort("NO_SORT");
        config.setDispType("DETAIL");
        config.setIsActive(true);
    }

    @Test
    void testWeiBoSign() {
        // 执行签到
            signService.weiBoSign(config);
    }

    @Test
    void testWeiBoSignWithInactiveConfig() {
        // 设置非活跃配置
        config.setIsActive(false);

        // 执行签到
        signService.weiBoSign(config);
    }
} 