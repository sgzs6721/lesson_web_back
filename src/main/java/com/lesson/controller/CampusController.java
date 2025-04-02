package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.repository.tables.records.EduCampusRecord;
import com.lesson.service.CampusService;
import com.lesson.vo.campus.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 校区控制器
 */
@RestController
@RequestMapping("/api/campus")
public class CampusController {

    @Autowired
    private CampusService campusService;

    /**
     * 查询校区列表
     *
     * @param query 查询条件
     * @return 校区列表
     */
    @GetMapping("/list")
    public Result<CampusListVO> list(CampusQueryVO query) {
        return campusService.list(query.getName(), query.getStatus());
    }

    /**
     * 创建校区
     *
     * @param vo 校区信息
     * @return 创建的校区
     */
    @PostMapping("/create")
    public Result<CampusVO> create(@RequestBody CampusCreateVO vo) {
        return campusService.create(vo);
    }

    /**
     * 更新校区
     *
     * @param vo 校区信息
     * @return 更新的校区
     */
    @PutMapping("/update")
    public Result<CampusVO> update(@RequestBody CampusUpdateVO vo) {
        return campusService.update(vo);
    }

    /**
     * 删除校区
     *
     * @param id 校区ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return campusService.delete(id);
    }

    /**
     * 切换校区状态
     *
     * @param id 校区ID
     * @return 操作结果
     */
    @PutMapping("/toggle-status/{id}")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        return campusService.toggleStatus(id);
    }
} 