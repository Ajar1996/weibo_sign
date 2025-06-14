package com.weibo.utils;

import com.weibo.constant.ResultCode;
import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static Result<?> fail(ResultCode code) {
        Result<?> result = new Result<>();
        result.setCode(code.getCode());
        result.setMessage(code.getMessage());
        return result;
    }
}