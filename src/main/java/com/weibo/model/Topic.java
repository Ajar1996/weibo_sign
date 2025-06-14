package com.weibo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    private String title;        // 超话标题
    private String level;        // 等级描述（如 "LV.12"）
    private String signStatus;   // 签到状态（"已签"/"签到"）
    private String signAction;   // 签到API路径

    public boolean isSigned() {
        return "已签".equals(this.signStatus);
    }
}