package com.weibo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_execution_log")
public class TaskExecutionLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("config_id")
    private Integer configId;

    @TableField("config_name")
    private String configName;

    @TableField("execution_type")
    private String executionType; // 执行类型枚举

    @TableField("attempt_number")
    private Integer attemptNumber;

    @TableField("scheduled_time")
    private LocalDateTime scheduledTime;

    @TableField("actual_start_time")
    private LocalDateTime actualStartTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("duration_seconds")
    private Long durationSeconds;

    @TableField("error_message")
    private String errorMessage;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}