package com.weibo.constant;

import lombok.Getter;

/**
 * 任务日志类型枚举
 */
@Getter
public enum TaskLogType {
    SCHEDULED("SCHEDULED", "计划执行"),
    EXECUTING("EXECUTING", "执行中"),
    RETRYING("RETRYING", "重试中"),
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败");

    private final String code;
    private final String desc;

    TaskLogType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举（避免大小写问题）
     */
    public static TaskLogType getByCode(String code) {
        for (TaskLogType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的日志类型编码: " + code);
    }

    /**
     * 判断是否为成功状态
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }
}