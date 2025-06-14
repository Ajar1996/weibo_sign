package com.weibo.model.req;

import lombok.Data;
import org.intellij.lang.annotations.Pattern;

@Data
public class TaskLogQueryParam {
    private String weiboName;
    
    private String xianyuName;
    
    //SCHE|EXEC|RETRY|SUCCESS|FAIL
    private String logTypeCode;
}