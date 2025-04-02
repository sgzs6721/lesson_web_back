package com.lesson.service;

import com.lesson.common.Result;
import com.lesson.vo.campus.CampusCreateVO;
import com.lesson.vo.campus.CampusListVO;
import com.lesson.vo.campus.CampusUpdateVO;
import com.lesson.vo.campus.CampusVO;

/**
 * 校区服务接口
 */
public interface CampusService {
    
    /**
     * 查询校区列表
     *
     * @param name 校区名称
     * @param status 状态
     * @return 校区列表
     */
    Result<CampusListVO> list(String name, Boolean status);

    /**
     * 创建校区
     *
     * @param vo 校区信息
     * @return 创建的校区
     */
    Result<CampusVO> create(CampusCreateVO vo);

    /**
     * 更新校区
     *
     * @param vo 校区信息
     * @return 更新的校区
     */
    Result<CampusVO> update(CampusUpdateVO vo);

    /**
     * 删除校区
     *
     * @param id 校区ID
     * @return 操作结果
     */
    Result<Void> delete(Long id);

    /**
     * 切换校区状态
     *
     * @param id 校区ID
     * @return 操作结果
     */
    Result<Void> toggleStatus(Long id);
} 