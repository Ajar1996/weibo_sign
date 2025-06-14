package com.weibo.exception;

import com.weibo.constant.ResultCode;
import lombok.Getter;

@Getter
public class WeiboException extends RuntimeException {
    private final ResultCode resultCode;
    private final String detail;

    public WeiboException(ResultCode resultCode, String detail) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.detail = detail;
    }
}