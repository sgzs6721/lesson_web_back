package com.lesson.service;

import com.lesson.request.campus.CampusCreateRequest;
import com.lesson.request.campus.CampusQueryRequest;
import com.lesson.request.campus.CampusUpdateRequest;
import com.lesson.vo.CampusVO;
import com.lesson.vo.CampusSimpleVO;
import com.lesson.vo.PageResult;

import java.util.List;

public interface CampusService {

    /**
     * 创建校区
     *
     * @param request 创建请求
     * @return 校区ID
     */
    Long createCampus(CampusCreateRequest request);

    /**
     * 更新校区
     *
     * @param id      校区ID
     * @param request 更新请求
     */
    void updateCampus(Long id, CampusUpdateRequest request);

    /**
     * 删除校区
     *
     * @param id 校区ID
     */
    void deleteCampus(Long id);

    /**
     * 获取校区详情
     *
     * @param id 校区ID
     * @return 校区信息
     */
    CampusVO getCampus(Long id);

    /**
     * 查询校区列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<CampusVO> listCampuses(CampusQueryRequest request);

    /**
     * 更新校区状态
     *
     * @param id 校区ID
     * @param status 状态：0-已关闭，1-营业中
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取校区简单列表
     *
     * @return 校区简单信息列表
     */
    List<CampusSimpleVO> listSimpleCampuses();
}
