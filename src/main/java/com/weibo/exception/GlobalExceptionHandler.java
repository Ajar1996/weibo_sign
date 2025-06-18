package com.weibo.exception;

import com.weibo.constant.ResultCode;
import com.weibo.notify.impl.EmailNotifier;
import com.weibo.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final EmailNotifier emailNotifier;
    /**
            * 处理自定义业务异常
     */
    @ExceptionHandler(WeiboException.class)
    public ResponseEntity<Result<?>> handleWeiboException(WeiboException ex) {
        ResultCode code = ex.getResultCode(); // 获取枚举状态码
        sendAlertEmail(code, ex);
        return ResponseEntity.status(code.getCode())
                .body(Result.fail(code));
    }

    /**
            * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception ex) {
        log.error("系统异常: ", ex);
        sendAlertEmail(ResultCode.INTERNAL_ERROR, ex);
        return ResponseEntity.internalServerError()
                .body(Result.fail(ResultCode.INTERNAL_ERROR));
    }

    /**
            * 发送告警邮件（带状态码信息）
            */
    private void sendAlertEmail(ResultCode code, Exception ex) {
        String subject = "【" + code.getCode() + "】" + code.getMessage();
        String content = String.format(
                "【%s】\n时间: %s\n异常: %s\n消息: %s\n堆栈: \n%s",
                code.getMessage(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                getStackTraceAsString(ex)
        );
        emailNotifier.sendAlert(subject, content);
    }

    /**
     * 获取格式化的堆栈信息
     */
    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}