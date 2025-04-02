package com.lesson.vo.campus;

import com.lesson.repository.tables.records.EduCampusRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 校区 VO 转换器
 */
@Mapper
public interface CampusVOConverter {
    CampusVOConverter INSTANCE = Mappers.getMapper(CampusVOConverter.class);

    /**
     * 将数据库记录转换为 VO
     *
     * @param record 数据库记录
     * @return VO
     */
    @Mapping(target = "status", expression = "java(record.getStatus() == 1)")
    CampusVO toVO(EduCampusRecord record);

    /**
     * 将数据库记录列表转换为 VO 列表
     *
     * @param records 数据库记录列表
     * @return VO 列表
     */
    List<CampusVO> toVOList(List<EduCampusRecord> records);

    /**
     * 将创建 VO 转换为数据库记录
     *
     * @param vo 创建 VO
     * @return 数据库记录
     */
    @Mapping(target = "status", constant = "1")
    @Mapping(target = "institutionId", constant = "1L")
    @Mapping(target = "contactPhone", source = "phone")
    EduCampusRecord toRecord(CampusCreateVO vo);

    /**
     * 将更新 VO 转换为数据库记录
     *
     * @param vo 更新 VO
     * @return 数据库记录
     */
    @Mapping(target = "status", ignore = true)
    EduCampusRecord toRecord(CampusUpdateVO vo);
} 