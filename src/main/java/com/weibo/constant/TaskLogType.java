package com.weibo.constant;

import lombok.Getter;


import lombok.Getter;

/**
        * 任务日志类型枚举
        */
@Getter
public enum TaskLogType {
    SCHEDULE("SCHE", "任务调度"),
    EXECUTE("EXEC", "任务执行"),
    RETRY("RETRY", "任务重试"),
    SUCCESS("SUCCESS", "任务成功"),
    FAILURE("FAIL", "任务失败");

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