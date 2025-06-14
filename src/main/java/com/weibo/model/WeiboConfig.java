package com.weibo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
        * 微博签到配置实体类
        */
@Data
@Component
public class WeiboConfig {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 闲鱼名称 */
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

    /** Server酱推送KEY */
    private String serverKey;
    
    /** 是否排序（INCREASE/DECREASE/FALSE） */
    private String isSort;
    
    /** 结果显示类型（DETAIL/SIMPLE） */
    private String dispType;
    
    /** 是否启用（1启用 0禁用） */
    private Boolean isActive;
    
    /** 最后执行时间 */
    private Date lastExecuteTime;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
}