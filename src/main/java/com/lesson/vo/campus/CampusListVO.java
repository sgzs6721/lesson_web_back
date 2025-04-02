package com.lesson.vo.campus;

import lombok.Data;

import java.util.List;

/**
 * 校区列表 VO
 */
@Data
public class CampusListVO {
    /**
     * 校区列表
     */
    private List<CampusVO> list;

    /**
     * 总数
     */
    private Long total;
} 