package com.lesson.vo.campus;

import lombok.Data;

/**
 * 校区创建响应
 */
@Data
public class CampusCreateResponseVO {
    /**
     * 校区ID
     */
    private Long campusId;

    public static CampusCreateResponseVO of(Long campusId) {
        CampusCreateResponseVO vo = new CampusCreateResponseVO();
        vo.setCampusId(campusId);
        return vo;
    }
} 