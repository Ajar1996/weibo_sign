package com.weibo.controller;

import com.weibo.mapper.WeiboConfigMapper;
import com.weibo.model.WeiboConfig;
import com.weibo.service.CookieManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weibo-configs")
public class WeiboConfigController {

    @Autowired
    WeiboConfigMapper configMapper;

    @Autowired
    CookieManager cookieManager;

    @GetMapping
    public List<WeiboConfig> getAllConfigs() {
        return configMapper.selectAllActive();
    }

    @PostMapping
    public void addConfig( @RequestBody WeiboConfig config) {
        config.setWeiboNickname(cookieManager.getUsernameFromCookie(cookieManager.extractCookieFromUrl(config.getRowUrl())));
        configMapper.insert(config);
    }

    @PutMapping("/{id}")
    public void updateConfig(@PathVariable Integer id,
                             @RequestBody WeiboConfig config) {
        config.setId(id);
        configMapper.updateById(config);
    }

    @DeleteMapping("/{id}")
    public void disableConfig(@PathVariable Integer id) {
        configMapper.updateStatus(id, false);
    }
}