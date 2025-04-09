package com.lesson.request.campus;

import lombok.Data;

/**
 * 查询校区请求
 */
@Data
public class CampusQueryRequest {
    /**
     * 搜索关键字（校区名称/地址/电话）
     */
    private String keyword;

    /**
     * 状态：0-已关闭，1-营业中
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
} 