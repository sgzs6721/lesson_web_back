package com.lesson.service.impl;

import com.lesson.service.CourseSharingService;
import com.lesson.vo.request.CourseSharingRequest;
import com.lesson.vo.request.CourseSharingQueryRequest;
import com.lesson.vo.response.CourseSharingVO;
import com.lesson.vo.PageResult;
import com.lesson.enums.CourseSharingStatus;
import com.lesson.repository.Tables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSharingServiceImpl implements CourseSharingService {
    
    private final DSLContext dsl;
    private final HttpServletRequest httpServletRequest;
    
    @Override
    @Transactional
    public Long createCourseSharing(CourseSharingRequest request) {
        try {
            log.info("开始创建课程共享，请求参数：{}", request);
            
            // 从token中获取机构ID和校区ID
            Long institutionId = getInstitutionId();
            Long campusId = getCampusId();
            
            // 验证学员是否存在
            boolean studentExists = dsl.selectCount()
                    .from(Tables.EDU_STUDENT)
                    .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
                    .and(Tables.EDU_STUDENT.DELETED.eq(0))
                    .fetchOne(0, Long.class) > 0;
            
            if (!studentExists) {
                throw new RuntimeException("学员不存在或已被删除");
            }
            
            // 验证源课程是否存在
            boolean sourceCourseExists = dsl.selectCount()
                    .from(Tables.EDU_COURSE)
                    .where(Tables.EDU_COURSE.ID.eq(request.getSourceCourseId()))
                    .and(Tables.EDU_COURSE.DELETED.eq(0))
                    .fetchOne(0, Long.class) > 0;
            
            if (!sourceCourseExists) {
                throw new RuntimeException("源课程不存在或已被删除");
            }
            
            // 验证目标课程是否存在
            boolean targetCourseExists = dsl.selectCount()
                    .from(Tables.EDU_COURSE)
                    .where(Tables.EDU_COURSE.ID.eq(request.getTargetCourseId()))
                    .and(Tables.EDU_COURSE.DELETED.eq(0))
                    .fetchOne(0, Long.class) > 0;
            
            if (!targetCourseExists) {
                throw new RuntimeException("目标课程不存在或已被删除");
            }
            
            // 验证教练是否存在（如果指定了教练）
            if (request.getCoachId() != null) {
                boolean coachExists = dsl.selectCount()
                        .from(Tables.SYS_COACH)
                        .where(Tables.SYS_COACH.ID.eq(request.getCoachId()))
                        .and(Tables.SYS_COACH.DELETED.eq(0))
                        .fetchOne(0, Long.class) > 0;
                
                if (!coachExists) {
                    throw new RuntimeException("教练不存在或已被删除");
                }
            }
            
            // 检查是否已存在相同的共享记录
            boolean sharingExists = dsl.selectCount()
                    .from(Tables.EDU_COURSE_SHARING)
                    .where(Tables.EDU_COURSE_SHARING.STUDENT_ID.eq(request.getStudentId()))
                    .and(Tables.EDU_COURSE_SHARING.SOURCE_COURSE_ID.eq(request.getSourceCourseId()))
                    .and(Tables.EDU_COURSE_SHARING.TARGET_COURSE_ID.eq(request.getTargetCourseId()))
                    .and(Tables.EDU_COURSE_SHARING.DELETED.eq(0))
                    .and(Tables.EDU_COURSE_SHARING.STATUS.eq(CourseSharingStatus.ACTIVE.getCode()))
                    .fetchOne(0, Long.class) > 0;
            
            if (sharingExists) {
                throw new RuntimeException("该学员在此课程间已存在有效的共享记录");
            }
            
            // 创建课程共享记录
            int insertedRows = dsl.insertInto(Tables.EDU_COURSE_SHARING)
                    .set(Tables.EDU_COURSE_SHARING.STUDENT_ID, request.getStudentId())
                    .set(Tables.EDU_COURSE_SHARING.SOURCE_COURSE_ID, request.getSourceCourseId())
                    .set(Tables.EDU_COURSE_SHARING.TARGET_COURSE_ID, request.getTargetCourseId())
                    .set(Tables.EDU_COURSE_SHARING.COACH_ID, request.getCoachId())
                    .set(Tables.EDU_COURSE_SHARING.SHARED_HOURS, request.getSharedHours())
                    .set(Tables.EDU_COURSE_SHARING.STATUS, CourseSharingStatus.ACTIVE.getCode())
                    .set(Tables.EDU_COURSE_SHARING.START_DATE, request.getStartDate())
                    .set(Tables.EDU_COURSE_SHARING.END_DATE, request.getEndDate())
                    .set(Tables.EDU_COURSE_SHARING.CAMPUS_ID, campusId)
                    .set(Tables.EDU_COURSE_SHARING.INSTITUTION_ID, institutionId)
                    .set(Tables.EDU_COURSE_SHARING.NOTES, request.getNotes())
                    .set(Tables.EDU_COURSE_SHARING.CREATED_TIME, LocalDateTime.now())
                    .set(Tables.EDU_COURSE_SHARING.UPDATE_TIME, LocalDateTime.now())
                    .set(Tables.EDU_COURSE_SHARING.DELETED, 0)
                    .execute();
            
            if (insertedRows == 0) {
                throw new RuntimeException("创建课程共享记录失败");
            }
            
            // 获取插入的记录ID
            Long sharingId = dsl.lastID().longValue();
            
            log.info("课程共享创建成功，ID：{}", sharingId);
            return sharingId;
            
        } catch (Exception e) {
            log.error("创建课程共享时发生错误：", e);
            throw new RuntimeException("创建课程共享失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public PageResult<CourseSharingVO> listCourseSharings(CourseSharingQueryRequest request) {
        try {
            log.info("开始查询课程共享列表，请求参数：{}", request);
            
            // 构建查询条件
            Condition conditions = Tables.EDU_COURSE_SHARING.DELETED.eq(0);
            
            if (request.getStudentId() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.STUDENT_ID.eq(request.getStudentId()));
            }
            
            if (request.getSourceCourseId() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.SOURCE_COURSE_ID.eq(request.getSourceCourseId()));
            }
            
            if (request.getTargetCourseId() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.TARGET_COURSE_ID.eq(request.getTargetCourseId()));
            }
            
            if (request.getCoachId() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.COACH_ID.eq(request.getCoachId()));
            }
            
            if (request.getStatus() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.STATUS.eq(request.getStatus()));
            }
            
            if (request.getStartDate() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.START_DATE.greaterOrEqual(request.getStartDate()));
            }
            
            if (request.getEndDate() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.END_DATE.lessOrEqual(request.getEndDate()));
            }
            
            if (request.getCampusId() != null) {
                conditions = conditions.and(Tables.EDU_COURSE_SHARING.CAMPUS_ID.eq(request.getCampusId()));
            }
            
            // 获取总记录数
            long total = dsl.selectCount()
                    .from(Tables.EDU_COURSE_SHARING)
                    .where(conditions)
                    .fetchOne(0, Long.class);
            
            // 构建主查询 - 使用简单的查询避免复杂的JOIN
            SelectConditionStep<Record> baseQuery = dsl.select()
                    .from(Tables.EDU_COURSE_SHARING)
                    .where(conditions);
            
            // 排序
            SelectSeekStep1<Record, ?> sortedQuery;
            if (request.getSortField() != null && request.getSortOrder() != null) {
                SortField<?> sortField = buildSortField(request.getSortField(), request.getSortOrder());
                if (sortField != null) {
                    sortedQuery = baseQuery.orderBy(sortField);
                } else {
                    sortedQuery = baseQuery.orderBy(Tables.EDU_COURSE_SHARING.CREATED_TIME.desc());
                }
            } else {
                sortedQuery = baseQuery.orderBy(Tables.EDU_COURSE_SHARING.CREATED_TIME.desc());
            }
            
            // 分页
            List<Record> records = sortedQuery
                    .limit(request.getSize())
                    .offset(request.getOffset())
                    .fetch();
            
            // 转换为VO
            List<CourseSharingVO> voList = records.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            
            log.info("查询课程共享列表成功，总记录数：{}，当前页记录数：{}", total, voList.size());
            
            return PageResult.of(voList, total, request.getPage(), request.getSize());
            
        } catch (Exception e) {
            log.error("查询课程共享列表时发生错误：", e);
            throw new RuntimeException("查询课程共享列表失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public CourseSharingVO getCourseSharingById(Long id) {
        try {
            log.info("开始获取课程共享详情，ID：{}", id);
            
            Record record = dsl.select()
                    .from(Tables.EDU_COURSE_SHARING)
                    .where(Tables.EDU_COURSE_SHARING.ID.eq(id))
                    .and(Tables.EDU_COURSE_SHARING.DELETED.eq(0))
                    .fetchOne();
            
            if (record == null) {
                throw new RuntimeException("课程共享记录不存在或已被删除");
            }
            
            CourseSharingVO vo = convertToVO(record);
            log.info("获取课程共享详情成功，ID：{}", id);
            
            return vo;
            
        } catch (Exception e) {
            log.error("获取课程共享详情时发生错误：", e);
            throw new RuntimeException("获取课程共享详情失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void updateCourseSharingStatus(Long id, String status) {
        try {
            log.info("开始更新课程共享状态，ID：{}，新状态：{}", id, status);
            
            // 验证状态是否有效
            CourseSharingStatus sharingStatus = CourseSharingStatus.fromCode(status);
            if (sharingStatus == null) {
                throw new RuntimeException("无效的状态值：" + status);
            }
            
            // 更新状态
            int updatedRows = dsl.update(Tables.EDU_COURSE_SHARING)
                    .set(Tables.EDU_COURSE_SHARING.STATUS, status)
                    .set(Tables.EDU_COURSE_SHARING.UPDATE_TIME, LocalDateTime.now())
                    .where(Tables.EDU_COURSE_SHARING.ID.eq(id))
                    .and(Tables.EDU_COURSE_SHARING.DELETED.eq(0))
                    .execute();
            
            if (updatedRows == 0) {
                throw new RuntimeException("更新课程共享状态失败，记录不存在或已被删除");
            }
            
            log.info("课程共享状态更新成功，ID：{}，新状态：{}", id, status);
            
        } catch (Exception e) {
            log.error("更新课程共享状态时发生错误：", e);
            throw new RuntimeException("更新课程共享状态失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void deleteCourseSharing(Long id) {
        try {
            log.info("开始删除课程共享，ID：{}", id);
            
            // 逻辑删除
            int deletedRows = dsl.update(Tables.EDU_COURSE_SHARING)
                    .set(Tables.EDU_COURSE_SHARING.DELETED, 1)
                    .set(Tables.EDU_COURSE_SHARING.UPDATE_TIME, LocalDateTime.now())
                    .where(Tables.EDU_COURSE_SHARING.ID.eq(id))
                    .and(Tables.EDU_COURSE_SHARING.DELETED.eq(0))
                    .execute();
            
            if (deletedRows == 0) {
                throw new RuntimeException("删除课程共享失败，记录不存在或已被删除");
            }
            
            log.info("课程共享删除成功，ID：{}", id);
            
        } catch (Exception e) {
            log.error("删除课程共享时发生错误：", e);
            throw new RuntimeException("删除课程共享失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 将数据库记录转换为VO
     */
    private CourseSharingVO convertToVO(Record record) {
        CourseSharingVO vo = new CourseSharingVO();
        
        vo.setId(record.get(Tables.EDU_COURSE_SHARING.ID));
        vo.setStudentId(record.get(Tables.EDU_COURSE_SHARING.STUDENT_ID));
        vo.setSourceCourseId(record.get(Tables.EDU_COURSE_SHARING.SOURCE_COURSE_ID));
        vo.setTargetCourseId(record.get(Tables.EDU_COURSE_SHARING.TARGET_COURSE_ID));
        vo.setCoachId(record.get(Tables.EDU_COURSE_SHARING.COACH_ID));
        vo.setSharedHours(record.get(Tables.EDU_COURSE_SHARING.SHARED_HOURS));
        vo.setStatus(record.get(Tables.EDU_COURSE_SHARING.STATUS));
        
        // 设置状态名称
        CourseSharingStatus status = CourseSharingStatus.fromCode(vo.getStatus());
        vo.setStatusName(status != null ? status.getName() : vo.getStatus());
        
        vo.setStartDate(record.get(Tables.EDU_COURSE_SHARING.START_DATE));
        vo.setEndDate(record.get(Tables.EDU_COURSE_SHARING.END_DATE));
        vo.setCampusId(record.get(Tables.EDU_COURSE_SHARING.CAMPUS_ID));
        vo.setNotes(record.get(Tables.EDU_COURSE_SHARING.NOTES));
        vo.setCreatedTime(record.get(Tables.EDU_COURSE_SHARING.CREATED_TIME));
        vo.setUpdateTime(record.get(Tables.EDU_COURSE_SHARING.UPDATE_TIME));
        
        return vo;
    }
    
    /**
     * 构建排序字段
     */
    private SortField<?> buildSortField(String sortField, String sortOrder) {
        if (sortField == null || sortOrder == null) {
            return null;
        }
        
        if ("desc".equalsIgnoreCase(sortOrder)) {
            switch (sortField.toLowerCase()) {
                case "id":
                    return Tables.EDU_COURSE_SHARING.ID.desc();
                case "sharedhours":
                    return Tables.EDU_COURSE_SHARING.SHARED_HOURS.desc();
                case "status":
                    return Tables.EDU_COURSE_SHARING.STATUS.desc();
                case "startdate":
                    return Tables.EDU_COURSE_SHARING.START_DATE.desc();
                case "createdtime":
                    return Tables.EDU_COURSE_SHARING.CREATED_TIME.desc();
                case "updatetime":
                    return Tables.EDU_COURSE_SHARING.UPDATE_TIME.desc();
                default:
                    return Tables.EDU_COURSE_SHARING.CREATED_TIME.desc();
            }
        } else {
            switch (sortField.toLowerCase()) {
                case "id":
                    return Tables.EDU_COURSE_SHARING.ID.asc();
                case "sharedhours":
                    return Tables.EDU_COURSE_SHARING.SHARED_HOURS.asc();
                case "status":
                    return Tables.EDU_COURSE_SHARING.STATUS.asc();
                case "startdate":
                    return Tables.EDU_COURSE_SHARING.START_DATE.asc();
                case "createdtime":
                    return Tables.EDU_COURSE_SHARING.CREATED_TIME.asc();
                case "updatetime":
                    return Tables.EDU_COURSE_SHARING.UPDATE_TIME.asc();
                default:
                    return Tables.EDU_COURSE_SHARING.CREATED_TIME.asc();
            }
        }
    }
    
    /**
     * 从token中获取机构ID
     */
    private Long getInstitutionId() {
        Object orgId = httpServletRequest.getAttribute("orgId");
        if (orgId == null) {
            throw new RuntimeException("无法获取机构ID");
        }
        return (Long) orgId;
    }
    
    /**
     * 从token中获取校区ID
     */
    private Long getCampusId() {
        Object campusId = httpServletRequest.getAttribute("campusId");
        if (campusId == null) {
            throw new RuntimeException("无法获取校区ID");
        }
        return (Long) campusId;
    }
} 