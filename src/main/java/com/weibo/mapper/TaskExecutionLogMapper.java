package com.weibo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weibo.model.TaskExecutionLog;
import com.weibo.model.dto.ExecutionStatDTO;
import com.weibo.model.req.TaskLogQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TaskExecutionLogMapper extends BaseMapper<TaskExecutionLog> {

    List<ExecutionStatDTO> selectStats();

    /**
     * 分页条件查询
     */
    Page<TaskExecutionLog> selectByCondition(
            @Param("page") Page<TaskExecutionLog> page,
            @Param("param") TaskLogQueryParam param);

}