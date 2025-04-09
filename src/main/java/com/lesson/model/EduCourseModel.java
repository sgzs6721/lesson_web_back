package com.lesson.model;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import com.lesson.model.record.CourseDetailRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.lesson.repository.Tables.EDU_COURSE;

@Repository
@RequiredArgsConstructor
public class EduCourseModel {
    
    private final DSLContext dsl;
    
    /**
     * 创建课程
     */
    public String createCourse(String name, CourseType type, CourseStatus status,
                             BigDecimal unitHours, BigDecimal totalHours, BigDecimal price,
                             String coachId, String coachName,
                             Long campusId, String campusName,
                             Long institutionId, String institutionName,
                             String description) {
        String id = UUID.randomUUID().toString().replace("-", "");
        
        dsl.insertInto(EDU_COURSE)
           .set(EDU_COURSE.ID, id)
           .set(EDU_COURSE.NAME, name)
           .set(EDU_COURSE.TYPE, type.name())
           .set(EDU_COURSE.STATUS, status.name())
           .set(EDU_COURSE.UNIT_HOURS, unitHours)
           .set(EDU_COURSE.TOTAL_HOURS, totalHours)
           .set(EDU_COURSE.CONSUMED_HOURS, BigDecimal.ZERO)
           .set(EDU_COURSE.PRICE, price)
           .set(EDU_COURSE.COACH_ID, coachId)
           .set(EDU_COURSE.COACH_NAME, coachName)
           .set(EDU_COURSE.CAMPUS_ID, campusId)
           .set(EDU_COURSE.CAMPUS_NAME, campusName)
           .set(EDU_COURSE.INSTITUTION_ID, institutionId)
           .set(EDU_COURSE.INSTITUTION_NAME, institutionName)
           .set(EDU_COURSE.DESCRIPTION, description)
           .set(EDU_COURSE.DELETED, (byte) 0)
           .execute();
           
        return id;
    }
    
    /**
     * 更新课程
     */
    public void updateCourse(String id, String name, CourseType type, CourseStatus status,
                           BigDecimal unitHours, BigDecimal totalHours, BigDecimal price,
                           String coachId, String coachName,
                           Long campusId, String campusName,
                           Long institutionId, String institutionName,
                           String description) {
        dsl.update(EDU_COURSE)
           .set(EDU_COURSE.NAME, name)
           .set(EDU_COURSE.TYPE, type.name())
           .set(EDU_COURSE.STATUS, status.name())
           .set(EDU_COURSE.UNIT_HOURS, unitHours)
           .set(EDU_COURSE.TOTAL_HOURS, totalHours)
           .set(EDU_COURSE.PRICE, price)
           .set(EDU_COURSE.COACH_ID, coachId)
           .set(EDU_COURSE.COACH_NAME, coachName)
           .set(EDU_COURSE.CAMPUS_ID, campusId)
           .set(EDU_COURSE.CAMPUS_NAME, campusName)
           .set(EDU_COURSE.INSTITUTION_ID, institutionId)
           .set(EDU_COURSE.INSTITUTION_NAME, institutionName)
           .set(EDU_COURSE.DESCRIPTION, description)
           .where(EDU_COURSE.ID.eq(id))
           .and(EDU_COURSE.DELETED.eq((byte) 0))
           .execute();
    }
    
    /**
     * 删除课程
     */
    public void deleteCourse(String id) {
        dsl.update(EDU_COURSE)
           .set(EDU_COURSE.DELETED, (byte) 1)
           .where(EDU_COURSE.ID.eq(id))
           .and(EDU_COURSE.DELETED.eq((byte) 0))
           .execute();
    }
    
    /**
     * 更新课程状态
     */
    public void updateCourseStatus(String id, CourseStatus status) {
        dsl.update(EDU_COURSE)
           .set(EDU_COURSE.STATUS, status.name())
           .where(EDU_COURSE.ID.eq(id))
           .and(EDU_COURSE.DELETED.eq((byte) 0))
           .execute();
    }
    
    /**
     * 获取课程详情
     */
    public CourseDetailRecord getCourseById(String id) {
        Record record = dsl.select()
                          .from(EDU_COURSE)
                          .where(EDU_COURSE.ID.eq(id))
                          .and(EDU_COURSE.DELETED.eq((byte) 0))
                          .fetchOne();
                          
        if (record == null) {
            return null;
        }
        
        return convertToDetailRecord(record);
    }
    
    /**
     * 分页查询课程列表
     */
    public List<CourseDetailRecord> listCourses(String keyword, CourseType type, CourseStatus status,
                                              String coachId, Long campusId, Long institutionId,
                                              String sortField, String sortOrder,
                                              int pageNum, int pageSize) {
        SelectConditionStep<Record> query = createBaseQuery(keyword, type, status, coachId, campusId, institutionId);
        
        // 排序
        if ("totalHours".equals(sortField)) {
            if ("desc".equals(sortOrder)) {
                query.orderBy(EDU_COURSE.TOTAL_HOURS.desc());
            } else {
                query.orderBy(EDU_COURSE.TOTAL_HOURS.asc());
            }
        } else if ("consumedHours".equals(sortField)) {
            if ("desc".equals(sortOrder)) {
                query.orderBy(EDU_COURSE.CONSUMED_HOURS.desc());
            } else {
                query.orderBy(EDU_COURSE.CONSUMED_HOURS.asc());
            }
        } else if ("price".equals(sortField)) {
            if ("desc".equals(sortOrder)) {
                query.orderBy(EDU_COURSE.PRICE.desc());
            } else {
                query.orderBy(EDU_COURSE.PRICE.asc());
            }
        } else {
            query.orderBy(EDU_COURSE.CREATED_TIME.desc());
        }
        
        // 分页
        Result<Record> result = query
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetch();
                
        List<CourseDetailRecord> records = new ArrayList<>();
        for (Record record : result) {
            records.add(convertToDetailRecord(record));
        }
        
        return records;
    }
    
    /**
     * 统计课程总数
     */
    public long countCourses(String keyword, CourseType type, CourseStatus status,
                           String coachId, Long campusId, Long institutionId) {
        SelectConditionStep<Record> query = createBaseQuery(keyword, type, status, coachId, campusId, institutionId);
        return query.fetchCount();
    }
    
    /**
     * 判断课程是否存在
     */
    public boolean existsById(String id) {
        return dsl.fetchExists(
            dsl.selectOne()
               .from(EDU_COURSE)
               .where(EDU_COURSE.ID.eq(id))
               .and(EDU_COURSE.DELETED.eq((byte) 0))
        );
    }
    
    /**
     * 创建基础查询
     */
    private SelectConditionStep<Record> createBaseQuery(String keyword, CourseType type, CourseStatus status,
                                                String coachId, Long campusId, Long institutionId) {
        SelectConditionStep<Record> query = dsl.select()
                                        .from(EDU_COURSE)
                                        .where(EDU_COURSE.DELETED.eq((byte) 0));
        
        // 关键词过滤
        if (keyword != null && !keyword.isEmpty()) {
            query.and(EDU_COURSE.NAME.like("%" + keyword + "%")
                  .or(EDU_COURSE.DESCRIPTION.like("%" + keyword + "%")));
        }
        
        // 类型过滤
        if (type != null) {
            query.and(EDU_COURSE.TYPE.eq(type.name()));
        }
        
        // 状态过滤
        if (status != null) {
            query.and(EDU_COURSE.STATUS.eq(status.name()));
        }
        
        // 教练过滤
        if (coachId != null && !coachId.isEmpty()) {
            query.and(EDU_COURSE.COACH_ID.eq(coachId));
        }
        
        // 校区过滤
        if (campusId != null) {
            query.and(EDU_COURSE.CAMPUS_ID.eq(campusId));
        }
        
        // 机构过滤
        if (institutionId != null) {
            query.and(EDU_COURSE.INSTITUTION_ID.eq(institutionId));
        }
        
        return query;
    }
    
    /**
     * 转换为详情记录
     */
    private CourseDetailRecord convertToDetailRecord(Record record) {
        CourseDetailRecord detailRecord = new CourseDetailRecord();
        detailRecord.setId(record.get(EDU_COURSE.ID));
        detailRecord.setName(record.get(EDU_COURSE.NAME));
        detailRecord.setType(CourseType.valueOf(record.get(EDU_COURSE.TYPE)));
        detailRecord.setStatus(CourseStatus.valueOf(record.get(EDU_COURSE.STATUS)));
        detailRecord.setUnitHours(record.get(EDU_COURSE.UNIT_HOURS));
        detailRecord.setTotalHours(record.get(EDU_COURSE.TOTAL_HOURS));
        detailRecord.setConsumedHours(record.get(EDU_COURSE.CONSUMED_HOURS));
        detailRecord.setPrice(record.get(EDU_COURSE.PRICE));
        detailRecord.setCoachId(record.get(EDU_COURSE.COACH_ID));
        detailRecord.setCoachName(record.get(EDU_COURSE.COACH_NAME));
        detailRecord.setCampusId(record.get(EDU_COURSE.CAMPUS_ID));
        detailRecord.setCampusName(record.get(EDU_COURSE.CAMPUS_NAME));
        detailRecord.setInstitutionId(record.get(EDU_COURSE.INSTITUTION_ID));
        detailRecord.setInstitutionName(record.get(EDU_COURSE.INSTITUTION_NAME));
        detailRecord.setDescription(record.get(EDU_COURSE.DESCRIPTION));
        detailRecord.setCreatedTime(record.get(EDU_COURSE.CREATED_TIME));
        detailRecord.setUpdateTime(record.get(EDU_COURSE.UPDATE_TIME));
        return detailRecord;
    }
} 