package com.weibo.controller;

import com.weibo.mapper.WeiboConfigMapper;
import com.weibo.model.WeiboConfig;
import com.weibo.task.WeiboTaskScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weibo-configs")
@RequiredArgsConstructor
public class WeiboConfigController {
    private final WeiboConfigMapper configMapper;
    private final WeiboTaskScheduler taskScheduler;

    @GetMapping
    public List<WeiboConfig> getAllConfigs() {
        return configMapper.selectAllActive();
    }

    @PostMapping
    public void addConfig( @RequestBody WeiboConfig config) {
        configMapper.insert(config);
    }

    @PutMapping("/{id}")
    public void updateConfig(@PathVariable Integer id,
                             @RequestBody WeiboConfig config) {
        config.setId(id);
        configMapper.update(config);
    }

    @DeleteMapping("/{id}")
    public void disableConfig(@PathVariable Integer id) {
        configMapper.updateStatus(id, false);
    }
}