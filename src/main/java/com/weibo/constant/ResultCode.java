package com.weibo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局状态码和消息枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // 成功状态码
    SUCCESS(200, "操作成功"),

    // 通用错误码
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统繁忙"),

    // 业务异常码 (1000~1999)
    CONNECT_ERROR(1001,"url请求失败"),

    SIGN_FAILED(1002, "签到失败"),
    EMAIL_SEND_ERROR(1003, "邮件发送失败");

    private final int code;
    private final String message;
}