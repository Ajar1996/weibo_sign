package com.weibo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weibo.model.dto.ExecutionStatDTO;
import com.weibo.model.TaskExecutionLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface TaskExecutionLogMapper extends BaseMapper<TaskExecutionLog> {

    List<ExecutionStatDTO> selectStats();

}