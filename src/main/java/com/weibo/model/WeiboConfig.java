package com.weibo.model;

import lombok.Data;
import java.util.Date;

/**
        * 微博签到配置实体类
        */
@Data
public class WeiboConfig {
    /** 主键ID */
    private Integer id;
    
    /** 任务名称 */
    private String name;
    
    /** 咸鱼账号昵称（原real_name） */
    private String xianyuName;
    
    /** 微博账号昵称 */
    private String weiboNickname;

    /** 邮箱 */
    private String email;
    
    /** 微博API请求URL（包含cookie参数） */
    private String rowUrl;
    
    /** 单次签到数量限制 */
    private Integer signOnceCount;
    
    /** 钉钉机器人密钥 */
    private String dingSecret;
    
    /** 钉钉Webhook地址 */
    private String dingWebhook;
    
    /** Server酱推送KEY */
    private String serverKey;
    
    /** Qmsg酱推送KEY */
    private String qmsgKey;
    
    /** 是否排序（INCREASE/DECREASE/FALSE） */
    private String isSort;
    
    /** 结果显示类型（DETAIL/SIMPLE） */
    private String dispType;
    
    /** QQ邮箱（用于接收通知） */
    private String qqEmail;
    
    /** 是否启用（1启用 0禁用） */
    private Boolean isActive;
    
    /** 定时任务表达式 */
    private String cronExpression;
    
    /** 最后执行时间 */
    private Date lastExecuteTime;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
}