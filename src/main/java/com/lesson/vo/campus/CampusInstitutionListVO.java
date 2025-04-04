package com.lesson.vo.campus;

import lombok.Data;
import java.util.List;

/**
 * 机构校区列表响应
 */
@Data
public class CampusInstitutionListVO {
    /**
     * 校区列表
     */
    private List<CampusSimpleVO> list;

    public static CampusInstitutionListVO of(List<CampusSimpleVO> list) {
        CampusInstitutionListVO vo = new CampusInstitutionListVO();
        vo.setList(list);
        return vo;
    }
} 