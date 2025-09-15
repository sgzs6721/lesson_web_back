package com.lesson.service;

import com.lesson.vo.response.CourseDetailVO;
import com.lesson.vo.response.DashboardDataVO;
import com.lesson.vo.response.DashboardOverviewVO;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.lesson.repository.Tables;
import org.jooq.impl.DSL;

/**
 * 首页统计服务
 */
@Slf4j
@Service
public class DashboardService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis缓存Key
    private static final String REDIS_KEY_DASHBOARD_TODAY = "dashboard:today:" + LocalDate.now().toString();
    private static final String REDIS_KEY_DASHBOARD_COURSES = "dashboard:courses:" + LocalDate.now().toString();

    /**
     * 获取今日首页数据
     */
    public DashboardDataVO getTodayDashboardData() {
        // 1. 从Redis获取数据总览
        DashboardOverviewVO overview = getTodayOverview();
        
        // 2. 从Redis获取课程详情
        List<CourseDetailVO> courseDetails = getTodayCourseDetails();
        
        // 3. 组装返回数据
        DashboardDataVO data = new DashboardDataVO();
        data.setOverview(overview);
        data.setCourseDetails(courseDetails);
        
        return data;
    }

    /**
     * 获取今日数据总览
     */
    public DashboardOverviewVO getTodayOverview() {
        // 先从Redis获取
        DashboardOverviewVO overview = (DashboardOverviewVO) redisTemplate.opsForValue().get(REDIS_KEY_DASHBOARD_TODAY);
        
        if (overview == null) {
            // Redis中没有，实时计算
            overview = calculateTodayOverview();
            // 缓存5分钟
            redisTemplate.opsForValue().set(REDIS_KEY_DASHBOARD_TODAY, overview, 5, TimeUnit.MINUTES);
        }
        
        return overview;
    }

    /**
     * 获取今日课程详情
     */
    public List<CourseDetailVO> getTodayCourseDetails() {
        // 先从Redis获取
        @SuppressWarnings("unchecked")
        List<CourseDetailVO> courseDetails = (List<CourseDetailVO>) redisTemplate.opsForValue().get(REDIS_KEY_DASHBOARD_COURSES);
        
        if (courseDetails == null) {
            // Redis中没有，实时计算
            courseDetails = calculateTodayCourseDetails();
            // 缓存5分钟
            redisTemplate.opsForValue().set(REDIS_KEY_DASHBOARD_COURSES, courseDetails, 5, TimeUnit.MINUTES);
        }
        
        return courseDetails;
    }

    /**
     * 计算今日数据总览
     */
    private DashboardOverviewVO calculateTodayOverview() {
        log.info("开始计算今日数据总览");
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        try {
            // 1. 查询今日打卡记录统计
            Record statsRecord = dsl.select(
                    DSL.countDistinct(Tables.EDU_STUDENT_COURSE_RECORD.COACH_ID).as("teacher_count"),
                    DSL.countDistinct(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID).as("class_count"),
                    DSL.countDistinct(Tables.EDU_STUDENT_COURSE_RECORD.STUDENT_ID).as("student_count"),
                    DSL.count(Tables.EDU_STUDENT_COURSE_RECORD.ID).as("checkin_count"),
                    DSL.coalesce(DSL.sum(Tables.EDU_STUDENT_COURSE_RECORD.HOURS), BigDecimal.ZERO).as("consumed_hours"),
                    DSL.sum(DSL.case_()
                            .when(Tables.EDU_STUDENT_COURSE_RECORD.STATUS.eq("LEAVE"), 1)
                            .otherwise(0)
                    ).as("leave_count"),
                    DSL.coalesce(DSL.sum(Tables.EDU_STUDENT_COURSE_RECORD.HOURS.multiply(100)), BigDecimal.ZERO).as("teacher_remuneration"),
                    DSL.coalesce(DSL.sum(Tables.EDU_STUDENT_COURSE_RECORD.HOURS.multiply(150)), BigDecimal.ZERO).as("consumed_fees")
                )
                .from(Tables.EDU_STUDENT_COURSE_RECORD)
                .where(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_DATE.between(today, today))
                .and(Tables.EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
                .fetchOne();

            // 2. 构建返回对象
            DashboardOverviewVO overview = new DashboardOverviewVO();
            if (statsRecord != null) {
                overview.setTeacherCount(statsRecord.get("teacher_count", Integer.class));
                overview.setClassCount(statsRecord.get("class_count", Integer.class));
                overview.setStudentCount(statsRecord.get("student_count", Integer.class));
                overview.setCheckinCount(statsRecord.get("checkin_count", Integer.class));
                overview.setConsumedHours(statsRecord.get("consumed_hours", BigDecimal.class));
                overview.setLeaveCount(statsRecord.get("leave_count", Integer.class));
                overview.setTeacherRemuneration(statsRecord.get("teacher_remuneration", BigDecimal.class));
                overview.setConsumedFees(statsRecord.get("consumed_fees", BigDecimal.class));
            } else {
                // 如果没有数据，设置默认值
                overview.setTeacherCount(0);
                overview.setClassCount(0);
                overview.setStudentCount(0);
                overview.setCheckinCount(0);
                overview.setConsumedHours(BigDecimal.ZERO);
                overview.setLeaveCount(0);
                overview.setTeacherRemuneration(BigDecimal.ZERO);
                overview.setConsumedFees(BigDecimal.ZERO);
            }

            log.info("今日数据总览计算完成: {}", overview);
            return overview;

        } catch (Exception e) {
            log.error("计算今日数据总览失败", e);
            // 返回默认值
            DashboardOverviewVO overview = new DashboardOverviewVO();
            overview.setTeacherCount(0);
            overview.setClassCount(0);
            overview.setStudentCount(0);
            overview.setCheckinCount(0);
            overview.setConsumedHours(BigDecimal.ZERO);
            overview.setLeaveCount(0);
            overview.setTeacherRemuneration(BigDecimal.ZERO);
            overview.setConsumedFees(BigDecimal.ZERO);
            return overview;
        }
    }

    /**
     * 计算今日课程详情
     */
    private List<CourseDetailVO> calculateTodayCourseDetails() {
        log.info("开始计算今日课程详情");
        
        LocalDate today = LocalDate.now();
        List<CourseDetailVO> courseDetails = new ArrayList<>();

        try {
            // 1. 查询今日按教练分组的课程统计
            Result<?> courseStats = dsl.select(
                    Tables.EDU_COURSE.NAME.as("course_name"),
                    Tables.SYS_COACH.NAME.as("coach_name"),
                    DSL.coalesce(DSL.sum(Tables.EDU_STUDENT_COURSE_RECORD.HOURS), BigDecimal.ZERO).as("total_hours"),
                    DSL.coalesce(DSL.sum(Tables.EDU_STUDENT_COURSE_RECORD.HOURS.multiply(100)), BigDecimal.ZERO).as("total_remuneration"),
                    DSL.coalesce(DSL.sum(Tables.EDU_STUDENT_COURSE_RECORD.HOURS.multiply(150)), BigDecimal.ZERO).as("total_sales"),
                    Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID
                )
                .from(Tables.EDU_STUDENT_COURSE_RECORD)
                .join(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(Tables.EDU_COURSE.ID))
                .join(Tables.SYS_COACH).on(Tables.EDU_STUDENT_COURSE_RECORD.COACH_ID.eq(Tables.SYS_COACH.ID))
                .where(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_DATE.eq(today))
                .and(Tables.EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
                .groupBy(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID, Tables.EDU_COURSE.NAME, Tables.SYS_COACH.NAME)
                .orderBy(DSL.sum(Tables.EDU_STUDENT_COURSE_RECORD.HOURS).desc())
                .fetch();

            // 2. 为每个课程查询学员打卡记录
            for (Record courseRecord : courseStats) {
                Long courseId = courseRecord.get("course_id", Long.class);
                String courseName = courseRecord.get("course_name", String.class);
                String coachName = courseRecord.get("coach_name", String.class);
                BigDecimal totalHours = courseRecord.get("total_hours", BigDecimal.class);
                BigDecimal totalRemuneration = courseRecord.get("total_remuneration", BigDecimal.class);
                BigDecimal totalSales = courseRecord.get("total_sales", BigDecimal.class);

                // 查询该课程的学员打卡记录
                List<CourseDetailVO.StudentAttendanceVO> attendances = getCourseStudentAttendances(courseId, today);

                // 构建课程详情
                CourseDetailVO courseDetail = new CourseDetailVO();
                courseDetail.setCourseName(courseName);
                courseDetail.setCoachName(coachName);
                courseDetail.setHours(totalHours);
                courseDetail.setRemuneration(totalRemuneration);
                courseDetail.setSalesAmount(totalSales);
                courseDetail.setStudentAttendances(attendances);

                courseDetails.add(courseDetail);
            }

            log.info("今日课程详情计算完成，共{}个课程", courseDetails.size());
            return courseDetails;

        } catch (Exception e) {
            log.error("计算今日课程详情失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取课程的学员打卡记录
     */
    private List<CourseDetailVO.StudentAttendanceVO> getCourseStudentAttendances(Long courseId, LocalDate courseDate) {
        try {
            Result<?> attendanceRecords = dsl.select(
                    Tables.EDU_STUDENT.NAME.as("student_name"),
                    Tables.EDU_STUDENT_COURSE_RECORD.START_TIME,
                    Tables.EDU_STUDENT_COURSE_RECORD.END_TIME,
                    Tables.EDU_STUDENT_COURSE_RECORD.STATUS
                )
                .from(Tables.EDU_STUDENT_COURSE_RECORD)
                .join(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(Tables.EDU_STUDENT.ID))
                .where(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(courseId))
                .and(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_DATE.eq(courseDate))
                .and(Tables.EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
                .orderBy(Tables.EDU_STUDENT_COURSE_RECORD.START_TIME)
                .fetch();

            List<CourseDetailVO.StudentAttendanceVO> attendances = new ArrayList<>();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (Record record : attendanceRecords) {
                String studentName = record.get("student_name", String.class);
                LocalTime startTime = record.get("start_time", LocalTime.class);
                LocalTime endTime = record.get("end_time", LocalTime.class);
                String status = record.get("status", String.class);

                CourseDetailVO.StudentAttendanceVO attendance = new CourseDetailVO.StudentAttendanceVO();
                attendance.setStudentName(studentName);
                attendance.setTimeSlot(startTime.format(timeFormatter) + "-" + endTime.format(timeFormatter));
                
                // 状态映射
                switch (status) {
                    case "COMPLETED":
                        attendance.setStatus("已完成");
                        break;
                    case "LEAVE":
                        attendance.setStatus("请假");
                        break;
                    default:
                        attendance.setStatus("未打卡");
                        break;
                }

                attendances.add(attendance);
            }

            return attendances;

        } catch (Exception e) {
            log.error("获取课程学员打卡记录失败，courseId: {}", courseId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 清除今日缓存
     */
    public void clearTodayCache() {
        redisTemplate.delete(REDIS_KEY_DASHBOARD_TODAY);
        redisTemplate.delete(REDIS_KEY_DASHBOARD_COURSES);
        log.info("已清除今日首页数据缓存");
    }
}
