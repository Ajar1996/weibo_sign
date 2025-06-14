package com.weibo.exception;

import java.io.IOException;

// 自定义异常类（需添加到项目中）
public class WeiboApiException extends IOException {
    public WeiboApiException(String message) {
        super(message);
    }
}