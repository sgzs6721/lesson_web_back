package com.lesson.model;

import com.lesson.common.enums.CampusStatus;
import com.lesson.model.record.CampusDetailRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SysCampusModelTest {

    @Autowired
    private SysCampusModel sysCampusModel;

    @Test
    public void testListCampuses() {
        // 测试用例1：基本查询
        List<CampusDetailRecord> records = sysCampusModel.listCampuses(
            null,           // 不设置关键词
            null,          // 不设置状态
            1L,           // 机构ID
            1,            // 第一页
            10           // 每页10条
        );
        assertNotNull(records);
        assertFalse(records.isEmpty());
        
        // 打印结果
        System.out.println("测试用例1 - 基本查询结果：");
        records.forEach(record -> {
            System.out.println("校区ID: " + record.getId());
            System.out.println("校区名称: " + record.getName());
            System.out.println("校区地址: " + record.getAddress());
            System.out.println("校区状态: " + record.getStatus());
            System.out.println("管理员姓名: " + record.getManagerName());
            System.out.println("管理员电话: " + record.getManagerPhone());
            System.out.println("------------------------");
        });

        // 测试用例2：带关键词查询
        records = sysCampusModel.listCampuses(
            "测试",        // 搜索关键词
            null,          // 不设置状态
            1L,           // 机构ID
            1,            // 第一页
            10           // 每页10条
        );
        assertNotNull(records);
        System.out.println("\n测试用例2 - 带关键词查询结果：");
        records.forEach(record -> {
            System.out.println("校区ID: " + record.getId());
            System.out.println("校区名称: " + record.getName());
            System.out.println("------------------------");
        });

        // 测试用例3：带状态查询
        records = sysCampusModel.listCampuses(
            null,           // 不设置关键词
            CampusStatus.OPEN,  // 只查询营业中的校区
            1L,           // 机构ID
            1,            // 第一页
            10           // 每页10条
        );
        assertNotNull(records);
        System.out.println("\n测试用例3 - 带状态查询结果：");
        records.forEach(record -> {
            System.out.println("校区ID: " + record.getId());
            System.out.println("校区名称: " + record.getName());
            System.out.println("校区状态: " + record.getStatus());
            System.out.println("------------------------");
        });
    }
} 