package com.weibo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weibo.model.TaskExecutionLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TaskExecutionLogMapperTest {

    @Autowired
    private TaskExecutionLogMapper taskExecutionLogMapper;

    @Test
    public void testGetList() {
        // 创建查询条件
        LambdaQueryWrapper<TaskExecutionLog> queryWrapper = new LambdaQueryWrapper<>();
        
        // 执行查询
        List<TaskExecutionLog> list = taskExecutionLogMapper.selectList(queryWrapper);

        // 验证结果
        assertNotNull(list);
        
        // 打印结果
        System.out.println("查询到的记录数: " + list.size());
        list.forEach(log -> {
            System.out.println("日志ID: " + log.getId());
            System.out.println("------------------------");
        });
    }
} 