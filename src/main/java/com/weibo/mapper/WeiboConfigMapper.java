package com.weibo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weibo.model.WeiboConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WeiboConfigMapper extends BaseMapper<WeiboConfig> {
    // 插入并返回主键
    int insert(WeiboConfig config);

    // 根据ID更新
    int update(WeiboConfig config);

    // 根据ID删除
    int deleteById(@Param("id") Integer id);

    // 查询所有有效配置
    List<WeiboConfig> selectAllActive();

    // 更新最后执行时间
    int updateLastExecuteTime(@Param("id") Integer id);

    //   更新配置状态
    int updateStatus(@Param("id") Integer id, @Param("status") Boolean status);
}