package com.weibo.model.dto;

import lombok.Data;

@Data
public class ExecutionStatDTO {
    private Integer configId;
    private String configName;
    private Long total;
    private Long successCount;

    // 计算成功率（非数据库字段）
    public Double getSuccessRate() {
        return total == 0 ? 0 : Math.round(successCount * 100.0 / total * 100) / 100.0;
    }
}