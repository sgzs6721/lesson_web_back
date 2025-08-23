package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.common.enums.ConstantType;
import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import com.lesson.model.EduCourseModel;
import com.lesson.model.SysCoachModel;
import com.lesson.model.SysConstantModel;
import com.lesson.model.record.CoachDetailRecord;
import com.lesson.model.record.CourseDetailRecord;
import com.lesson.repository.tables.records.SysConstantRecord;
import com.lesson.service.CourseService;
import com.lesson.service.CourseHoursRedisService;
import com.lesson.service.CampusStatsRedisService;
import com.lesson.vo.CourseSimpleVO;
import com.lesson.vo.CourseVO;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseQueryRequest;
import com.lesson.vo.request.CourseUpdateRequest;
import com.lesson.vo.request.CoachFeeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import com.lesson.repository.Tables;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final HttpServletRequest httpServletRequest; // 注入HttpServletRequest
    private final EduCourseModel courseModel;
    private final SysConstantModel constantModel;
    private final SysCoachModel sysCoachModel;
    private final CourseHoursRedisService courseHoursRedisService;
    private final CampusStatsRedisService campusStatsRedisService;
    private final DSLContext dsl;

    @Override
    @Transactional
    public Long createCourse(CourseCreateRequest request) {
        try {
            // 获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                throw new BusinessException("机构ID不能为空");
            }

            // 验证教练列表
            if (Boolean.TRUE.equals(request.getIsMultiTeacher())) {
                // 多教师教学模式
                if (CollectionUtils.isEmpty(request.getCoachFees())) {
                    throw new BusinessException("多教师教学模式下，至少需要选择一个教练");
                }
                
                // 验证教练是否存在且属于当前机构
                for (CoachFeeRequest coachFee : request.getCoachFees()) {
                    sysCoachModel.validateCoach(coachFee.getCoachId(), request.getCampusId(), institutionId);
                }
            } else {
                // 单教师教学模式
                if (CollectionUtils.isEmpty(request.getCoachFees()) || request.getCoachFees().size() != 1) {
                    throw new BusinessException("单教师教学模式下，只能选择一个教练");
                }
                
                // 验证教练是否存在且属于当前机构
                sysCoachModel.validateCoach(request.getCoachFees().get(0).getCoachId(), request.getCampusId(), institutionId);
            }

            // 计算课程层面的教练费用（仅用于课程表的冗余字段）
            BigDecimal courseLevelCoachFee = BigDecimal.ZERO;
            if (!Boolean.TRUE.equals(request.getIsMultiTeacher())) {
                if (request.getCoachFees() != null && request.getCoachFees().size() == 1
                        && request.getCoachFees().get(0).getCoachFee() != null) {
                    courseLevelCoachFee = request.getCoachFees().get(0).getCoachFee();
                }
            }

            // 创建课程基本信息
            Long courseId = courseModel.createCourse(
                request.getName(),
                request.getTypeId(),
                request.getStatus(), // 使用前端传入的状态
                request.getUnitHours(),
                BigDecimal.ZERO, // 总课时默认为0
                request.getPrice(),
                courseLevelCoachFee,
                request.getIsMultiTeacher(),
                request.getCampusId(),
                institutionId,
                request.getDescription()
            );

            // 创建课程-教练关联关系
            for (CoachFeeRequest coachFee : request.getCoachFees()) {
                courseModel.createCourseCoachRelation(courseId, coachFee.getCoachId(), coachFee.getCoachFee());
            }

            List<Long> coachIds = request.getCoachFees().stream()
                .map(CoachFeeRequest::getCoachId)
                .collect(Collectors.toList());
            log.info("课程创建成功: courseId={}, name={}, coachIds={}",
                     courseId, request.getName(), coachIds);

            // 更新Redis统计数据
            campusStatsRedisService.incrementCourseCount(institutionId, request.getCampusId());

            return courseId;
        } catch (BusinessException e) {
            log.warn("创建课程失败: name={}, error={}", request.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建课程异常: name={}", request.getName(), e);
            throw new BusinessException("创建课程失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void updateCourse(CourseUpdateRequest request) {
        try {
            // 验证课程是否存在
            CourseDetailRecord existingCourse = courseModel.getCourseById(request.getId());
            if (existingCourse == null) {
                throw new BusinessException("课程不存在或已删除");
            }

            // 验证教练列表
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (Boolean.TRUE.equals(request.getIsMultiTeacher())) {
                // 多教师教学模式
                if (CollectionUtils.isEmpty(request.getCoachFees())) {
                    throw new BusinessException("多教师教学模式下，至少需要选择一个教练");
                }
                
                // 验证教练是否存在且属于当前机构
                for (CoachFeeRequest coachFee : request.getCoachFees()) {
                    sysCoachModel.validateCoach(coachFee.getCoachId(), request.getCampusId(), institutionId);
                }
            } else {
                // 单教师教学模式
                if (CollectionUtils.isEmpty(request.getCoachFees()) || request.getCoachFees().size() != 1) {
                    throw new BusinessException("单教师教学模式下，只能选择一个教练");
                }
                
                // 验证教练是否存在且属于当前机构
                sysCoachModel.validateCoach(request.getCoachFees().get(0).getCoachId(), request.getCampusId(), institutionId);
            }

            // 计算课程层面的教练费用（仅用于课程表的冗余字段）
            BigDecimal updateCourseLevelCoachFee = BigDecimal.ZERO;
            if (!Boolean.TRUE.equals(request.getIsMultiTeacher())) {
                if (request.getCoachFees() != null && request.getCoachFees().size() == 1
                        && request.getCoachFees().get(0).getCoachFee() != null) {
                    updateCourseLevelCoachFee = request.getCoachFees().get(0).getCoachFee();
                }
            }

            // 更新课程基本信息
            courseModel.updateCourse(
                request.getId(),
                request.getName(),
                request.getTypeId(),
                request.getStatus(), // 传递状态参数
                request.getUnitHours(),
                existingCourse.getTotalHours(), // 保留原有的总课时值
                request.getPrice(),
                updateCourseLevelCoachFee,
                request.getIsMultiTeacher(),
                request.getCampusId(),
                request.getDescription()
            );

            // 更新课程-教练关联关系
            try {
                // 先将所有关联标记为已删除
                courseModel.deleteCourseCoachRelations(request.getId());

                // 然后添加新的关联关系
                for (CoachFeeRequest coachFee : request.getCoachFees()) {
                    try {
                        courseModel.createCourseCoachRelation(request.getId(), coachFee.getCoachId(), coachFee.getCoachFee());
                    } catch (Exception e) {
                        log.error("创建课程-教练关联失败: courseId={}, coachId={}, error={}",
                                 request.getId(), coachFee.getCoachId(), e.getMessage());
                        // 继续处理其他教练关联，而不是直接失败
                    }
                }
            } catch (Exception e) {
                log.error("更新课程-教练关联失败: courseId={}, error={}", request.getId(), e.getMessage(), e);
                // 即使教练关联更新失败，也不应该影响课程基本信息的更新
                // 只记录错误，不抛出异常
            }

            List<Long> coachIds = request.getCoachFees().stream()
                .map(CoachFeeRequest::getCoachId)
                .collect(Collectors.toList());
            log.info("课程更新成功: courseId={}, name={}, coachIds={}",
                     request.getId(), request.getName(), coachIds);

            // 如果校区发生变化，更新Redis统计数据
            if (!existingCourse.getCampusId().equals(request.getCampusId())) {
                campusStatsRedisService.decrementCourseCount(institutionId, existingCourse.getCampusId());
                campusStatsRedisService.incrementCourseCount(institutionId, request.getCampusId());
            }
        } catch (BusinessException e) {
            log.warn("更新课程失败: id={}, error={}", request.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新课程异常: id={}, error={}", request.getId(), e.getMessage(), e);

            // 检查是否是唯一索引约束错误
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry") && e.getMessage().contains("idx_unique_name_campus_institution")) {
                // 获取当前课程信息
                CourseDetailRecord existingCourse = courseModel.getCourseById(request.getId());

                // 检查是否是名称或校区变更导致的错误
                if (existingCourse != null &&
                    (!request.getName().equals(existingCourse.getName()) ||
                     !request.getCampusId().equals(existingCourse.getCampusId()))) {
                    throw new BusinessException("该课程名称在所选校区已存在，请使用不同的课程名称。");
                } else {
                    throw new BusinessException("更新课程失败，可能是由于唯一索引约束冲突。请联系系统管理员。");
                }
            }

            // 其他错误直接抛出，保留原始错误信息
            throw new BusinessException("更新课程失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        // 获取课程信息用于更新统计
        CourseDetailRecord course = courseModel.getCourseById(id);
        if (course != null) {
            // 更新Redis统计数据
            campusStatsRedisService.decrementCourseCount(course.getInstitutionId(), course.getCampusId());
        }
        courseModel.deleteCourse(id);
    }

    @Override
    @Transactional
    public void updateCourseStatus(Long id, CourseStatus status) {
        courseModel.updateCourseStatus(id, status);
    }

    @Override
    public CourseVO getCourseById(Long id) {
        // 获取课程详情
        CourseDetailRecord record = courseModel.getCourseById(id);
        if (record == null) {
            throw new BusinessException("课程不存在或已删除");
        }

        // 从token中获取机构ID和校区ID
        Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
        Long campusId = record.getCampusId();

        // 转换为VO
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(record, vo);
        vo.setId(String.valueOf(record.getId()));

        // 从Redis获取课程总课时，如果Redis中没有，则使用数据库中的值
        BigDecimal totalHoursFromRedis = courseHoursRedisService.getCourseTotalHours(institutionId, campusId, id);
        if (totalHoursFromRedis != null) {
            vo.setTotalHours(totalHoursFromRedis);
            log.info("从Redis获取课程总课时: courseId={}, totalHours={}", id, totalHoursFromRedis);
        } else {
            vo.setTotalHours(record.getTotalHours());
        }

        // 获取课程类型
        if (record.getTypeId() != null) {
            // 获取所有课程类型常量
            List<SysConstantRecord> courseTypes = constantModel.list(Arrays.asList(ConstantType.COURSE_TYPE.getName()));
            SysConstantRecord typeRecord = courseTypes.stream()
                .filter(type -> type.getId().equals(record.getTypeId()))
                .findFirst()
                .orElse(null);
            if (typeRecord != null) {
                vo.setType(typeRecord.getConstantValue());
            }
        }

        // 获取教练信息
        List<CoachDetailRecord> coaches = sysCoachModel.getCoachesByCourseId(id);
        if (coaches != null && !coaches.isEmpty()) {
            List<CourseVO.CoachInfo> coachInfos = coaches.stream()
                .map(coach -> {
                    CourseVO.CoachInfo coachInfo = new CourseVO.CoachInfo();
                    coachInfo.setId(coach.getId());
                    coachInfo.setName(coach.getName());
                    coachInfo.setCoachFee(coach.getCoachFee());
                    return coachInfo;
                })
                .collect(Collectors.toList());
            vo.setCoaches(coachInfos);
        } else {
            vo.setCoaches(new ArrayList<>());
        }

        return vo;
    }

    @Override
    public List<CourseVO> listCourses(CourseQueryRequest request) {
        // 获取课程基本信息列表
        List<CourseDetailRecord> records = courseModel.listCourses(
            request.getKeyword(),
            request.getTypeIds(),
            request.getStatus(),
            request.getCoachIds(),
            request.getCampusId(),
            request.getInstitutionId(),
            request.getSortField(),
            request.getSortOrder(),
            request.getPageNum(),
            request.getPageSize()
        );

        // 获取所有课程类型常量
        List<SysConstantRecord> courseTypes = constantModel.list(Arrays.asList(ConstantType.COURSE_TYPE.getName()));
        Map<Long, String> courseTypeMap = courseTypes.stream()
            .collect(Collectors.toMap(
                SysConstantRecord::getId,
                SysConstantRecord::getConstantValue
            ));

        // 批量获取课程教练信息
        Map<Long, List<CoachDetailRecord>> courseCoachMap = new HashMap<>();
        List<Long> courseIds = records.stream()
            .map(CourseDetailRecord::getId)
            .collect(Collectors.toList());
        if (!courseIds.isEmpty()) {
            courseIds.forEach(courseId -> {
                List<CoachDetailRecord> coaches = sysCoachModel.getCoachesByCourseId(courseId);
                courseCoachMap.put(courseId, coaches);
            });
        }

        // 转换为VO
        return records.stream()
            .map(record -> {
                CourseVO vo = new CourseVO();
                // 复制基本信息
                BeanUtils.copyProperties(record, vo);

                // 设置课程ID
                vo.setId(String.valueOf(record.getId()));

                // 设置课程类型
                if (record.getTypeId() != null) {
                    String typeValue = courseTypeMap.get(record.getTypeId());
                    if (typeValue != null) {
                        vo.setType(typeValue);
                    }
                }

                // 动态计算课程总课时（从学员课程关系中统计）
                BigDecimal actualTotalHours = calculateCourseTotalHours(record.getId());
                vo.setTotalHours(actualTotalHours);

                // 动态计算课程已消耗课时（从学员课程关系中统计）
                BigDecimal actualConsumedHours = calculateCourseConsumedHours(record.getId());
                vo.setConsumedHours(actualConsumedHours);

                // 查询共享课程信息
                queryCourseSharingInfo(vo, record.getId());

                // 设置教练信息
                List<CoachDetailRecord> coaches = courseCoachMap.get(record.getId());
                if (coaches != null && !coaches.isEmpty()) {
                    List<CourseVO.CoachInfo> coachInfos = coaches.stream()
                        .map(coach -> {
                            CourseVO.CoachInfo coachInfo = new CourseVO.CoachInfo();
                            coachInfo.setId(coach.getId());
                            coachInfo.setName(coach.getName());
                            coachInfo.setCoachFee(coach.getCoachFee());
                            return coachInfo;
                        })
                        .collect(Collectors.toList());
                    vo.setCoaches(coachInfos);
                } else {
                    vo.setCoaches(new ArrayList<>());
                }

                return vo;
            })
            .collect(Collectors.toList());
    }

    @Override
    public long countCourses(CourseQueryRequest request) {
        return courseModel.countCourses(
            request.getKeyword(),
            request.getTypeIds(),
            request.getStatus(),
            request.getCoachIds(),
            request.getCampusId(),
            request.getInstitutionId()
        );
    }

    /**
     * 计算课程总课时（从学员课程关系中统计）
     */
    private BigDecimal calculateCourseTotalHours(Long courseId) {
        try {
            // 先检查是否是共享课程
            BigDecimal sharedHours = dsl.select(Tables.EDU_COURSE_SHARING.SHARED_HOURS)
                    .from(Tables.EDU_COURSE_SHARING)
                    .where(Tables.EDU_COURSE_SHARING.TARGET_COURSE_ID.eq(courseId))
                    .and(Tables.EDU_COURSE_SHARING.DELETED.eq(0))
                    .and(Tables.EDU_COURSE_SHARING.STATUS.eq("ACTIVE"))
                    .fetchOneInto(BigDecimal.class);
            
            if (sharedHours != null && sharedHours.compareTo(BigDecimal.ZERO) > 0) {
                // 如果是共享课程，直接返回共享课时
                log.debug("课程{}是共享课程，使用共享课时：{}", courseId, sharedHours);
                return sharedHours;
            }
            
            // 如果不是共享课程，从学员课程关系中统计
            BigDecimal totalHours = dsl.select(DSL.sum(Tables.EDU_STUDENT_COURSE.TOTAL_HOURS))
                    .from(Tables.EDU_STUDENT_COURSE)
                    .where(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(courseId))
                    .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .fetchOneInto(BigDecimal.class);
            
            return totalHours != null ? totalHours : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("计算课程总课时时发生错误: courseId={}, error={}", courseId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算课程已消耗课时（从学员课程关系中统计）
     */
    private BigDecimal calculateCourseConsumedHours(Long courseId) {
        try {
            // 先检查是否是共享课程
            BigDecimal sharedHours = dsl.select(Tables.EDU_COURSE_SHARING.SHARED_HOURS)
                    .from(Tables.EDU_COURSE_SHARING)
                    .where(Tables.EDU_COURSE_SHARING.TARGET_COURSE_ID.eq(courseId))
                    .and(Tables.EDU_COURSE_SHARING.DELETED.eq(0))
                    .and(Tables.EDU_COURSE_SHARING.STATUS.eq("ACTIVE"))
                    .fetchOneInto(BigDecimal.class);
            
            if (sharedHours != null && sharedHours.compareTo(BigDecimal.ZERO) > 0) {
                // 如果是共享课程，从学员课程关系中统计已消耗课时
                BigDecimal consumedHours = dsl.select(DSL.sum(Tables.EDU_STUDENT_COURSE.CONSUMED_HOURS))
                        .from(Tables.EDU_STUDENT_COURSE)
                        .where(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(courseId))
                        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                        .fetchOneInto(BigDecimal.class);
                
                // 确保已消耗课时不超过共享课时
                BigDecimal actualConsumed = consumedHours != null ? consumedHours : BigDecimal.ZERO;
                if (actualConsumed.compareTo(sharedHours) > 0) {
                    log.warn("课程{}已消耗课时({})超过共享课时({})，使用共享课时作为上限", 
                            courseId, actualConsumed, sharedHours);
                    return sharedHours;
                }
                return actualConsumed;
            }
            
            // 如果不是共享课程，从学员课程关系中统计
            BigDecimal consumedHours = dsl.select(DSL.sum(Tables.EDU_STUDENT_COURSE.CONSUMED_HOURS))
                    .from(Tables.EDU_STUDENT_COURSE)
                    .where(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(courseId))
                    .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .fetchOneInto(BigDecimal.class);
            
            return consumedHours != null ? consumedHours : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("计算课程已消耗课时时发生错误: courseId={}, error={}", courseId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 查询课程共享信息
     */
    private void queryCourseSharingInfo(CourseVO vo, Long courseId) {
        try {
            // 查询该课程是否有共享记录
            org.jooq.Record sharingRecord = dsl.select()
                    .from(Tables.EDU_COURSE_SHARING)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_COURSE_SHARING.STUDENT_ID.eq(Tables.EDU_STUDENT.ID))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_COURSE_SHARING.SOURCE_COURSE_ID.eq(Tables.EDU_COURSE.ID))
                    .where(Tables.EDU_COURSE_SHARING.TARGET_COURSE_ID.eq(courseId))
                    .and(Tables.EDU_COURSE_SHARING.DELETED.eq(0))
                    .and(Tables.EDU_COURSE_SHARING.STATUS.eq("ACTIVE"))
                    .fetchOne();
            
            if (sharingRecord != null) {
                vo.setIsShared(true);
                vo.setSharedSourceCourseId(sharingRecord.get(Tables.EDU_COURSE_SHARING.SOURCE_COURSE_ID));
                vo.setSharedSourceCourseName(sharingRecord.get(Tables.EDU_COURSE.NAME));
                vo.setSharedStudentId(sharingRecord.get(Tables.EDU_COURSE_SHARING.STUDENT_ID));
                vo.setSharedStudentName(sharingRecord.get(Tables.EDU_STUDENT.NAME));
                vo.setSharedHours(sharingRecord.get(Tables.EDU_COURSE_SHARING.SHARED_HOURS));
                
                log.debug("课程{}是共享课程，来源课程：{}，学员：{}", 
                        courseId, vo.getSharedSourceCourseName(), vo.getSharedStudentName());
            } else {
                vo.setIsShared(false);
            }
        } catch (Exception e) {
            log.warn("查询课程共享信息时发生错误: courseId={}, error={}", courseId, e.getMessage());
            vo.setIsShared(false);
        }
    }

    private CourseVO convertToVO(CourseDetailRecord record) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(record, vo);
        // 设置是否多教师教学字段
        vo.setIsMultiTeacher(record.getIsMultiTeacher());
        return vo;
    }

    @Override
    public List<CourseSimpleVO> listCourseSimple(Long campusId) {
        try {
            // 从token中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                throw new BusinessException("机构ID不能为空");
            }
            // 获取所有未删除的课程
            List<CourseDetailRecord> courses = courseModel.listAllCourses(campusId, institutionId);
            if (CollectionUtils.isEmpty(courses)) {
                return Collections.emptyList();
            }

            // 获取所有课程类型
            List<SysConstantRecord> courseTypes = constantModel.list(Arrays.asList("COURSE_TYPE"));
            Map<Long, String> courseTypeMap = new HashMap<>();
            for (SysConstantRecord record : courseTypes) {
                courseTypeMap.put(record.getId(), record.getConstantValue());
            }

            // 获取所有教练信息
            Map<Long, List<CoachDetailRecord>> courseCoachMap = new HashMap<>();
            for (CourseDetailRecord course : courses) {
                List<CoachDetailRecord> coaches = sysCoachModel.getCoachesByCourseId(course.getId());
                courseCoachMap.put(course.getId(), coaches);
            }

            // 转换为VO
            return courses.stream().map(course -> {
                CourseSimpleVO vo = new CourseSimpleVO();
                vo.setId(course.getId());
                vo.setName(course.getName());
                vo.setTypeId(course.getTypeId());
                vo.setTypeName(courseTypeMap.getOrDefault(course.getTypeId(), ""));
                vo.setStatus(course.getStatus());

                // 设置教练信息
                List<CoachDetailRecord> coaches = courseCoachMap.getOrDefault(course.getId(), Collections.emptyList());
                if (!CollectionUtils.isEmpty(coaches)) {
                    List<CourseSimpleVO.CoachInfo> coachInfos = coaches.stream().map(coach -> {
                        CourseSimpleVO.CoachInfo coachInfo = new CourseSimpleVO.CoachInfo();
                        coachInfo.setId(coach.getId());
                        coachInfo.setName(coach.getName());
                        return coachInfo;
                    }).collect(Collectors.toList());
                    vo.setCoaches(coachInfos);
                } else {
                    vo.setCoaches(Collections.emptyList());
                }

                return vo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取课程简要信息列表异常", e);
            throw new BusinessException("获取课程简要信息列表失败，请稍后重试");
        }
    }
}
