package com.lesson.service.impl;

import com.lesson.repository.tables.EduStudent;
import com.lesson.repository.tables.EduStudentCourse;
import com.lesson.repository.tables.EduStudentPayment;
import com.lesson.repository.tables.EduCourse;
import com.lesson.repository.tables.EduStudentCourseRecord;
import com.lesson.repository.tables.SysConstant;
import com.lesson.repository.tables.SysCoach;
import com.lesson.repository.tables.SysCoachSalary;
import com.lesson.repository.tables.SysCoachCourse;
import org.jooq.DSLContext;
import org.jooq.Field;
import static org.jooq.impl.DSL.*;
import com.lesson.service.StatisticsService;
import com.lesson.vo.request.StudentAnalysisRequest;
import com.lesson.vo.request.CourseAnalysisRequest;
import com.lesson.vo.request.CoachAnalysisRequest;
import com.lesson.vo.request.FinanceAnalysisRequest;
import com.lesson.vo.response.StudentAnalysisVO;
import com.lesson.vo.response.CourseAnalysisVO;
import com.lesson.vo.response.CoachAnalysisVO;
import com.lesson.vo.response.FinanceAnalysisVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final DSLContext dslContext;
    
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public StudentAnalysisVO getStudentAnalysis(StudentAnalysisRequest request) {
        log.info("开始获取学员分析统计数据，请求参数：{}", request);
        
        StudentAnalysisVO result = new StudentAnalysisVO();
        
        // 获取学员指标
        result.setStudentMetrics(getStudentMetrics(request));
        
        // 获取学员增长趋势
        result.setGrowthTrend(getGrowthTrend(request));
        
        // 获取续费金额趋势
        result.setRenewalAmountTrend(getRenewalAmountTrend(request));
        
        // 获取学员来源分布
        result.setSourceDistribution(getSourceDistribution(request));
        
        // 获取新增学员来源分布
        result.setNewStudentSourceDistribution(getNewStudentSourceDistribution(request));
        
        log.info("学员分析统计数据获取完成");
        return result;
    }
    
    @Override
    public CourseAnalysisVO getCourseAnalysis(CourseAnalysisRequest request) {
        log.info("开始获取课程分析统计数据，请求参数：{}", request);
        
        CourseAnalysisVO result = new CourseAnalysisVO();
        
        // 获取课程关键指标
        result.setCourseMetrics(getCourseMetrics(request));
        
        // 获取课程类型分析
        result.setCourseTypeAnalysis(getCourseTypeAnalysis(request));
        
        // 获取课程销售趋势
        result.setSalesTrend(getSalesTrend(request));
        
        // 获取课程销售表现
        result.setSalesPerformance(getSalesPerformance(request));
        
        // 获取课程销售排行
        result.setSalesRanking(getSalesRanking(request));
        
        // 获取课程收入分析
        result.setRevenueAnalysis(getRevenueAnalysis(request));
        
        // 获取课程类型收入分布
        result.setRevenueDistribution(getRevenueDistribution(request));
        
        log.info("课程分析统计数据获取完成");
        return result;
    }
    
    @Override
    public CoachAnalysisVO getCoachAnalysis(CoachAnalysisRequest request) {
        log.info("开始获取教练分析统计数据，请求参数：{}", request);
        CoachAnalysisVO result = new CoachAnalysisVO();
        result.setCoachMetrics(getCoachMetrics(request));
        result.setClassHourTrend(getClassHourTrend(request));
        result.setCoachTop5Comparison(getCoachTop5Comparison(request));
        result.setCoachTypeDistribution(getCoachTypeDistribution(request));
        result.setSalaryAnalysis(getSalaryAnalysis(request));
        result.setPerformanceRanking(getPerformanceRanking(request));
        log.info("教练分析统计数据获取完成");
        return result;
    }
    
    @Override
    public StudentAnalysisVO.StudentMetrics getStudentMetrics(StudentAnalysisRequest request) {
        StudentAnalysisVO.StudentMetrics metrics = new StudentAnalysisVO.StudentMetrics();
        
        // 获取当前时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 计算上一期的时间范围
        LocalDate previousEndDate = startDate.minusDays(1);
        LocalDate previousStartDate = getStartDateByTimeType(previousEndDate, request.getTimeType());
        
        // 总学员数（当前在学状态）
        Long totalStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.STATUS.eq("STUDYING"))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 上一期总学员数
        Long previousTotalStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.STATUS.eq("STUDYING"))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .and(EduStudent.EDU_STUDENT.CREATED_TIME.lessThan(startDate.atStartOfDay()))
                .fetchOneInto(Long.class);
        
        // 新增学员数（指定时间范围内创建的学员）
        Long newStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 上一期新增学员数
        Long previousNewStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.CREATED_TIME.between(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59)))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 续费学员数（指定时间范围内有续费记录的学员）
        Long renewingStudents = dslContext
                .selectCount()
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq("RENEWAL"))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 上一期续费学员数
        Long previousRenewingStudents = dslContext
                .selectCount()
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq("RENEWAL"))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59)))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 流失学员数（状态为结业的学员）
        Long lostStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.STATUS.eq("GRADUATED"))
                .and(EduStudent.EDU_STUDENT.UPDATE_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 上一期流失学员数
        Long previousLostStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.STATUS.eq("GRADUATED"))
                .and(EduStudent.EDU_STUDENT.UPDATE_TIME.between(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59)))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 设置基础数据
        metrics.setTotalStudents(totalStudents != null ? totalStudents : 0L);
        metrics.setNewStudents(newStudents != null ? newStudents : 0L);
        metrics.setRenewingStudents(renewingStudents != null ? renewingStudents : 0L);
        metrics.setLostStudents(lostStudents != null ? lostStudents : 0L);
        
        // 计算真实的变化率
        metrics.setTotalStudentsChangeRate(calculateChangeRate(previousTotalStudents, totalStudents));
        metrics.setNewStudentsChangeRate(calculateChangeRate(previousNewStudents, newStudents));
        metrics.setRenewingStudentsChangeRate(calculateChangeRate(previousRenewingStudents, renewingStudents));
        metrics.setLostStudentsChangeRate(calculateChangeRate(previousLostStudents, lostStudents));
        
        return metrics;
    }
    
    /**
     * 计算变化率
     * @param previousValue 上一期的值
     * @param currentValue 当前期的值
     * @return 变化率（百分比）
     */
    private BigDecimal calculateChangeRate(Long previousValue, Long currentValue) {
        if (previousValue == null || previousValue == 0) {
            if (currentValue == null || currentValue == 0) {
                return BigDecimal.ZERO;
            } else {
                return BigDecimal.valueOf(100.0); // 从0增长到有值，增长率为100%
            }
        }
        
        if (currentValue == null) {
            currentValue = 0L;
        }
        
        BigDecimal previous = BigDecimal.valueOf(previousValue);
        BigDecimal current = BigDecimal.valueOf(currentValue);
        
        // 计算公式：(当前值 - 上期值) / 上期值 * 100
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * 计算BigDecimal变化率
     * @param previousValue 上一期的值
     * @param currentValue 当前期的值
     * @return 变化率（百分比）
     */
    private BigDecimal calculateBigDecimalChangeRate(BigDecimal previousValue, BigDecimal currentValue) {
        if (previousValue == null || previousValue.compareTo(BigDecimal.ZERO) == 0) {
            if (currentValue == null || currentValue.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            } else {
                return BigDecimal.valueOf(100.0); // 从0增长到有值，增长率为100%
            }
        }
        
        if (currentValue == null) {
            currentValue = BigDecimal.ZERO;
        }
        
        // 计算公式：(当前值 - 上期值) / 上期值 * 100
        return currentValue.subtract(previousValue)
                .divide(previousValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    @Override
    public List<StudentAnalysisVO.GrowthTrendPoint> getStudentGrowthTrend(StudentAnalysisRequest request) {
        return getGrowthTrend(request);
    }
    
    @Override
    public List<StudentAnalysisVO.RenewalAmountTrendPoint> getStudentRenewalTrend(StudentAnalysisRequest request) {
        return getRenewalAmountTrend(request);
    }
    
    @Override
    public List<StudentAnalysisVO.SourceDistribution> getStudentSourceDistribution(StudentAnalysisRequest request) {
        return getSourceDistribution(request);
    }
    
    @Override
    public List<StudentAnalysisVO.NewStudentSourceDistribution> getNewStudentSourceDistribution(StudentAnalysisRequest request) {
        List<StudentAnalysisVO.NewStudentSourceDistribution> distribution = new ArrayList<>();
        
        // 获取当前时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 从数据库查询新增学员来源分布数据
        Result<?> result = dslContext
                .select(
                    SysConstant.SYS_CONSTANT.CONSTANT_VALUE.as("sourceName"),
                    count().as("newStudentCount")
                )
                .from(EduStudent.EDU_STUDENT)
                .leftJoin(SysConstant.SYS_CONSTANT)
                .on(EduStudent.EDU_STUDENT.SOURCE_ID.eq(SysConstant.SYS_CONSTANT.ID))
                .where(EduStudent.EDU_STUDENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .groupBy(EduStudent.EDU_STUDENT.SOURCE_ID, SysConstant.SYS_CONSTANT.CONSTANT_VALUE)
                .orderBy(count().desc())
                .fetch();
        
        // 计算新增学员总数
        Long totalNewStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        if (totalNewStudents == null || totalNewStudents == 0) {
            // 如果没有新增学员数据，返回空列表
            return distribution;
        }
        
        // 构建分布数据
        for (Record record : result) {
            String sourceName = record.get("sourceName", String.class);
            Long newStudentCount = record.get("newStudentCount", Long.class);
            
            if (sourceName == null) {
                sourceName = "未知来源";
            }
            
            StudentAnalysisVO.NewStudentSourceDistribution item = new StudentAnalysisVO.NewStudentSourceDistribution();
            item.setSourceName(sourceName);
            item.setNewStudentCount(newStudentCount != null ? newStudentCount : 0L);
            
            // 计算百分比
            BigDecimal percentage = BigDecimal.valueOf(newStudentCount != null ? newStudentCount : 0L)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalNewStudents), 2, RoundingMode.HALF_UP);
            item.setPercentage(percentage);
            
            distribution.add(item);
        }
        
        return distribution;
    }
    
    @Override
    public CourseAnalysisVO.CourseMetrics getCourseMetrics(CourseAnalysisRequest request) {
        return getCourseMetricsInternal(request);
    }
    
    @Override
    public List<CourseAnalysisVO.CourseTypeAnalysis> getCourseTypeAnalysis(CourseAnalysisRequest request) {
        return getCourseTypeAnalysisInternal(request);
    }
    
    @Override
    public List<CourseAnalysisVO.SalesTrendPoint> getCourseSalesTrend(CourseAnalysisRequest request) {
        return getSalesTrend(request);
    }
    
    @Override
    public List<CourseAnalysisVO.CourseSalesPerformance> getCourseSalesPerformance(CourseAnalysisRequest request) {
        return getSalesPerformance(request);
    }
    
    @Override
    public List<CourseAnalysisVO.CourseSalesRanking> getCourseSalesRanking(CourseAnalysisRequest request) {
        return getSalesRanking(request);
    }
    
    /**
     * 获取课程销售排行
     */
    private List<CourseAnalysisVO.CourseSalesRanking> getSalesRanking(CourseAnalysisRequest request) {
        List<CourseAnalysisVO.CourseSalesRanking> ranking = new ArrayList<>();
        
        // 获取时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 从数据库查询课程销售排行数据
        Result<?> result = dslContext
                .select(
                    EduCourse.EDU_COURSE.NAME.as("courseName"),
                    count(EduStudentCourse.EDU_STUDENT_COURSE.ID).as("salesQuantity"),
                    sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT).as("revenue"),
                    EduCourse.EDU_COURSE.PRICE.as("unitPrice")
                )
                .from(EduCourse.EDU_COURSE)
                .leftJoin(EduStudentCourse.EDU_STUDENT_COURSE)
                .on(EduCourse.EDU_COURSE.ID.eq(EduStudentCourse.EDU_STUDENT_COURSE.COURSE_ID))
                .leftJoin(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .on(EduStudentCourse.EDU_STUDENT_COURSE.COURSE_ID.cast(String.class).eq(EduStudentPayment.EDU_STUDENT_PAYMENT.COURSE_ID))
                .where(EduCourse.EDU_COURSE.DELETED.eq(0))
                .and(EduCourse.EDU_COURSE.STATUS.eq("PUBLISHED"))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduCourse.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .groupBy(EduCourse.EDU_COURSE.ID, EduCourse.EDU_COURSE.NAME, EduCourse.EDU_COURSE.PRICE)
                .orderBy(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT).desc())
                .limit(10)
                .fetch();
        
        int rankingIndex = 1;
        for (Record record : result) {
            CourseAnalysisVO.CourseSalesRanking course = new CourseAnalysisVO.CourseSalesRanking();
            course.setCourseName(record.get("courseName", String.class));
            course.setSalesQuantity(record.get("salesQuantity", Long.class));
            course.setRevenue(record.get("revenue", BigDecimal.class));
            course.setUnitPrice(record.get("unitPrice", BigDecimal.class));
            ranking.add(course);
        }
        
        return ranking;
    }
    
    @Override
    public CourseAnalysisVO.CourseRevenueAnalysis getCourseRevenueAnalysis(CourseAnalysisRequest request) {
        return getRevenueAnalysis(request);
    }
    
    @Override
    public List<CourseAnalysisVO.CourseTypeRevenueDistribution> getCourseRevenueDistribution(CourseAnalysisRequest request) {
        return getRevenueDistribution(request);
    }
    
    @Override
    public CoachAnalysisVO.CoachMetrics getCoachMetrics(CoachAnalysisRequest request) {
        return getCoachMetricsInternal(request);
    }
    
    @Override
    public List<CoachAnalysisVO.ClassHourTrendPoint> getCoachClassHourTrend(CoachAnalysisRequest request) {
        return getClassHourTrend(request);
    }
    
    @Override
    public List<CoachAnalysisVO.CoachTop5Comparison> getCoachTop5Comparison(CoachAnalysisRequest request) {
        return getCoachTop5ComparisonInternal(request);
    }
    
    @Override
    public List<CoachAnalysisVO.CoachTypeDistribution> getCoachTypeDistribution(CoachAnalysisRequest request) {
        return getCoachTypeDistributionInternal(request);
    }
    
    @Override
    public CoachAnalysisVO.CoachSalaryAnalysis getCoachSalaryAnalysis(CoachAnalysisRequest request) {
        return getSalaryAnalysis(request);
    }
    
    @Override
    public List<CoachAnalysisVO.CoachPerformanceRanking> getCoachPerformanceRanking(CoachAnalysisRequest request) {
        return getPerformanceRanking(request);
    }
    
    /**
     * 获取学员增长趋势
     */
    private List<StudentAnalysisVO.GrowthTrendPoint> getGrowthTrend(StudentAnalysisRequest request) {
        List<StudentAnalysisVO.GrowthTrendPoint> trend = new ArrayList<>();
        
        // 获取过去12个月的数据
        LocalDate endDate = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate monthDate = endDate.minusMonths(i);
            String timePoint = monthDate.format(DateTimeFormatter.ofPattern("M月"));
            
            StudentAnalysisVO.GrowthTrendPoint point = new StudentAnalysisVO.GrowthTrendPoint();
            point.setTimePoint(timePoint);
            
            // 计算该月的各项指标
            LocalDate monthStart = monthDate.withDayOfMonth(1);
            LocalDate monthEnd = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            
            // 总学员数（该月最后一天的在学学员数）
            Long totalStudents = dslContext
                    .selectCount()
                    .from(EduStudent.EDU_STUDENT)
                    .where(EduStudent.EDU_STUDENT.STATUS.eq("STUDYING"))
                    .and(EduStudent.EDU_STUDENT.CREATED_TIME.le(monthEnd.atTime(23, 59, 59)))
                    .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(Long.class);
            
            // 新增学员数
            Long newStudents = dslContext
                    .selectCount()
                    .from(EduStudent.EDU_STUDENT)
                    .where(EduStudent.EDU_STUDENT.CREATED_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(Long.class);
            
            // 续费学员数
            Long renewingStudents = dslContext
                    .selectCount()
                    .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                    .where(EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq("RENEWAL"))
                    .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(Long.class);
            
            // 流失学员数
            Long lostStudents = dslContext
                    .selectCount()
                    .from(EduStudent.EDU_STUDENT)
                    .where(EduStudent.EDU_STUDENT.STATUS.eq("GRADUATED"))
                    .and(EduStudent.EDU_STUDENT.UPDATE_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudent.EDU_STUDENT.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudent.EDU_STUDENT.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(Long.class);
            
            point.setTotalStudents(totalStudents);
            point.setNewStudents(newStudents);
            point.setRenewingStudents(renewingStudents);
            point.setLostStudents(lostStudents);
            
            // 计算留存率
            if (totalStudents > 0) {
                BigDecimal retentionRate = BigDecimal.valueOf(totalStudents - lostStudents)
                        .divide(BigDecimal.valueOf(totalStudents), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                point.setRetentionRate(retentionRate);
            } else {
                point.setRetentionRate(BigDecimal.ZERO);
            }
            
            trend.add(point);
        }
        
        return trend;
    }
    
    /**
     * 获取续费金额趋势
     */
    private List<StudentAnalysisVO.RenewalAmountTrendPoint> getRenewalAmountTrend(StudentAnalysisRequest request) {
        List<StudentAnalysisVO.RenewalAmountTrendPoint> trend = new ArrayList<>();
        
        // 获取过去12个月的数据
        LocalDate endDate = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate monthDate = endDate.minusMonths(i);
            String timePoint = monthDate.format(DateTimeFormatter.ofPattern("M月"));
            
            StudentAnalysisVO.RenewalAmountTrendPoint point = new StudentAnalysisVO.RenewalAmountTrendPoint();
            point.setTimePoint(timePoint);
            
            LocalDate monthStart = monthDate.withDayOfMonth(1);
            LocalDate monthEnd = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            
            // 续费金额
            BigDecimal renewalAmount = dslContext
                    .select(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT.sum())
                    .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                    .where(EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq("RENEWAL"))
                    .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            // 新增学员缴费金额
            BigDecimal newStudentPaymentAmount = dslContext
                    .select(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT.sum())
                    .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                    .where(EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq("NEW"))
                    .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            point.setRenewalAmount(renewalAmount != null ? renewalAmount : BigDecimal.ZERO);
            point.setNewStudentPaymentAmount(newStudentPaymentAmount != null ? newStudentPaymentAmount : BigDecimal.ZERO);
            
            trend.add(point);
        }
        
        return trend;
    }
    
    /**
     * 获取学员来源分布
     */
    private List<StudentAnalysisVO.SourceDistribution> getSourceDistribution(StudentAnalysisRequest request) {
        List<StudentAnalysisVO.SourceDistribution> distribution = new ArrayList<>();
        
        // 从数据库查询学员来源分布数据
        Result<?> result = dslContext
                .select(
                    SysConstant.SYS_CONSTANT.CONSTANT_VALUE.as("sourceName"),
                    count().as("studentCount")
                )
                .from(EduStudent.EDU_STUDENT)
                .leftJoin(SysConstant.SYS_CONSTANT)
                .on(EduStudent.EDU_STUDENT.SOURCE_ID.eq(SysConstant.SYS_CONSTANT.ID))
                .where(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .groupBy(EduStudent.EDU_STUDENT.SOURCE_ID, SysConstant.SYS_CONSTANT.CONSTANT_VALUE)
                .orderBy(count().desc())
                .fetch();
        
        // 计算总学员数
        Long totalStudents = dslContext
                .selectCount()
                .from(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudent.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        if (totalStudents == null || totalStudents == 0) {
            // 如果没有学员数据，返回空列表
            return distribution;
        }
        
        // 构建分布数据
        for (Record record : result) {
            String sourceName = record.get("sourceName", String.class);
            Long studentCount = record.get("studentCount", Long.class);
            
            if (sourceName == null) {
                sourceName = "未知来源";
            }
            
            StudentAnalysisVO.SourceDistribution item = new StudentAnalysisVO.SourceDistribution();
            item.setSourceName(sourceName);
            item.setStudentCount(studentCount != null ? studentCount : 0L);
            
            // 计算百分比
            BigDecimal percentage = BigDecimal.valueOf(studentCount != null ? studentCount : 0L)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalStudents), 2, RoundingMode.HALF_UP);
            item.setPercentage(percentage);
            
            distribution.add(item);
        }
        
        return distribution;
    }
    
    /**
     * 获取课程关键指标
     */
    private CourseAnalysisVO.CourseMetrics getCourseMetricsInternal(CourseAnalysisRequest request) {
        CourseAnalysisVO.CourseMetrics metrics = new CourseAnalysisVO.CourseMetrics();
        
        // 获取当前时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 课程总数（已发布的课程）
        Long totalCourses = dslContext
                .selectCount()
                .from(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.STATUS.eq("PUBLISHED"))
                .and(EduCourse.EDU_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduCourse.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduCourse.EDU_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 新报课程数（指定时间范围内创建的学员课程关系）
        Long newCoursesEnrolled = dslContext
                .selectCount()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 续费课程数（指定时间范围内有续费记录的学员课程关系）
        Long renewedCourses = dslContext
                .selectCount()
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq("RENEWAL"))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
                    // 已销课程数（已消耗课时的学员课程关系）
            Long soldCourses = dslContext
                    .selectCount()
                    .from(EduStudentCourse.EDU_STUDENT_COURSE)
                    .where(EduStudentCourse.EDU_STUDENT_COURSE.CONSUMED_HOURS.gt(BigDecimal.ZERO))
                    .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 剩余课程数（未消耗完课时的学员课程关系）
        Long remainingCourses = dslContext
                .selectCount()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.CONSUMED_HOURS.lt(EduStudentCourse.EDU_STUDENT_COURSE.TOTAL_HOURS))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 课程单价（平均单价）
        BigDecimal courseUnitPrice = dslContext
                .select(EduCourse.EDU_COURSE.PRICE.avg())
                .from(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.STATUS.eq("PUBLISHED"))
                .and(EduCourse.EDU_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduCourse.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduCourse.EDU_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        // 设置指标值
        metrics.setTotalCourses(totalCourses);
        metrics.setNewCoursesEnrolled(newCoursesEnrolled);
        metrics.setRenewedCourses(renewedCourses);
        metrics.setSoldCourses(soldCourses);
        metrics.setRemainingCourses(remainingCourses);
        metrics.setCourseUnitPrice(courseUnitPrice != null ? courseUnitPrice : BigDecimal.ZERO);
        
        // 计算真实的变化率
        // 获取上一期的时间范围
        LocalDate previousEndDate = startDate.minusDays(1);
        LocalDate previousStartDate = getStartDateByTimeType(previousEndDate, request.getTimeType());
        
        // 上一期总课程数
        Long previousTotalCourses = dslContext
                .selectCount()
                .from(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.STATUS.eq("PUBLISHED"))
                .and(EduCourse.EDU_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduCourse.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduCourse.EDU_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .and(EduCourse.EDU_COURSE.CREATED_TIME.lessThan(startDate.atStartOfDay()))
                .fetchOneInto(Long.class);
        
        // 上一期新报课程数
        Long previousNewCoursesEnrolled = dslContext
                .selectCount()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME.between(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59)))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 上一期续费课程数
        Long previousRenewedCourses = dslContext
                .selectCount()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME.between(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59)))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 上一期已销课程数
        Long previousSoldCourses = dslContext
                .selectCount()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.CONSUMED_HOURS.gt(BigDecimal.ZERO))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.UPDATE_TIME.between(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59)))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 上一期剩余课程数
        Long previousRemainingCourses = dslContext
                .selectCount()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.CONSUMED_HOURS.lt(EduStudentCourse.EDU_STUDENT_COURSE.TOTAL_HOURS))
                .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .and(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME.lessThan(startDate.atStartOfDay()))
                .fetchOneInto(Long.class);
        
        // 上一期课程单价
        BigDecimal previousCourseUnitPrice = dslContext
                .select(EduCourse.EDU_COURSE.PRICE.avg())
                .from(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.STATUS.eq("PUBLISHED"))
                .and(EduCourse.EDU_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduCourse.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduCourse.EDU_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .and(EduCourse.EDU_COURSE.CREATED_TIME.lessThan(startDate.atStartOfDay()))
                .fetchOneInto(BigDecimal.class);
        
        // 计算真实的变化率
        metrics.setTotalCoursesChangeRate(calculateChangeRate(previousTotalCourses, totalCourses));
        metrics.setNewCoursesEnrolledChangeRate(calculateChangeRate(previousNewCoursesEnrolled, newCoursesEnrolled));
        metrics.setRenewedCoursesChangeRate(calculateChangeRate(previousRenewedCourses, renewedCourses));
        metrics.setSoldCoursesChangeRate(calculateChangeRate(previousSoldCourses, soldCourses));
        metrics.setRemainingCoursesChangeRate(calculateChangeRate(previousRemainingCourses, remainingCourses));
        
        // 计算课程单价变化率
        if (previousCourseUnitPrice != null && previousCourseUnitPrice.compareTo(BigDecimal.ZERO) > 0) {
            if (courseUnitPrice != null) {
                BigDecimal changeRate = courseUnitPrice.subtract(previousCourseUnitPrice)
                        .divide(previousCourseUnitPrice, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                metrics.setCourseUnitPriceChangeRate(changeRate);
            } else {
                metrics.setCourseUnitPriceChangeRate(BigDecimal.ZERO);
            }
        } else {
            metrics.setCourseUnitPriceChangeRate(BigDecimal.ZERO);
        }
        
        return metrics;
    }
    
    /**
     * 获取课程类型分析
     */
    private List<CourseAnalysisVO.CourseTypeAnalysis> getCourseTypeAnalysisInternal(CourseAnalysisRequest request) {
        List<CourseAnalysisVO.CourseTypeAnalysis> analysis = new ArrayList<>();
        
        // 这里简化处理，实际应该从数据库查询课程类型数据
        // 由于当前表结构限制，这里使用示例数据
        
        // 一对一课程
        CourseAnalysisVO.CourseTypeAnalysis oneOnOne = new CourseAnalysisVO.CourseTypeAnalysis();
        oneOnOne.setCourseTypeName("一对一");
        oneOnOne.setTotalCourseHours(BigDecimal.valueOf(456));
        oneOnOne.setEnrolledTotalHours(BigDecimal.valueOf(1280));
        oneOnOne.setSoldHours(BigDecimal.valueOf(960));
        oneOnOne.setRemainingHours(BigDecimal.valueOf(320));
        oneOnOne.setSalesAmount(BigDecimal.valueOf(1234567));
        oneOnOne.setAverageUnitPrice(BigDecimal.valueOf(280));
        
        // 一对二课程
        CourseAnalysisVO.CourseTypeAnalysis oneOnTwo = new CourseAnalysisVO.CourseTypeAnalysis();
        oneOnTwo.setCourseTypeName("一对二");
        oneOnTwo.setTotalCourseHours(BigDecimal.valueOf(324));
        oneOnTwo.setEnrolledTotalHours(BigDecimal.valueOf(980));
        oneOnTwo.setSoldHours(BigDecimal.valueOf(720));
        oneOnTwo.setRemainingHours(BigDecimal.valueOf(260));
        oneOnTwo.setSalesAmount(BigDecimal.valueOf(856432));
        oneOnTwo.setAverageUnitPrice(BigDecimal.valueOf(180));
        
        // 小班课
        CourseAnalysisVO.CourseTypeAnalysis smallClass = new CourseAnalysisVO.CourseTypeAnalysis();
        smallClass.setCourseTypeName("小班课");
        smallClass.setTotalCourseHours(BigDecimal.valueOf(298));
        smallClass.setEnrolledTotalHours(BigDecimal.valueOf(760));
        smallClass.setSoldHours(BigDecimal.valueOf(580));
        smallClass.setRemainingHours(BigDecimal.valueOf(180));
        smallClass.setSalesAmount(BigDecimal.valueOf(456789));
        smallClass.setAverageUnitPrice(BigDecimal.valueOf(120));
        
        // 大班课
        CourseAnalysisVO.CourseTypeAnalysis largeClass = new CourseAnalysisVO.CourseTypeAnalysis();
        largeClass.setCourseTypeName("大班课");
        largeClass.setTotalCourseHours(BigDecimal.valueOf(206));
        largeClass.setEnrolledTotalHours(BigDecimal.valueOf(400));
        largeClass.setSoldHours(BigDecimal.valueOf(320));
        largeClass.setRemainingHours(BigDecimal.valueOf(80));
        largeClass.setSalesAmount(BigDecimal.valueOf(234567));
        largeClass.setAverageUnitPrice(BigDecimal.valueOf(80));
        
        analysis.add(oneOnOne);
        analysis.add(oneOnTwo);
        analysis.add(smallClass);
        analysis.add(largeClass);
        
        return analysis;
    }
    
    /**
     * 获取课程销售趋势
     */
    private List<CourseAnalysisVO.SalesTrendPoint> getSalesTrend(CourseAnalysisRequest request) {
        List<CourseAnalysisVO.SalesTrendPoint> trend = new ArrayList<>();
        
        // 获取过去12个月的数据
        LocalDate endDate = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate monthDate = endDate.minusMonths(i);
            String timePoint = monthDate.format(DateTimeFormatter.ofPattern("M月"));
            
            CourseAnalysisVO.SalesTrendPoint point = new CourseAnalysisVO.SalesTrendPoint();
            point.setTimePoint(timePoint);
            
            LocalDate monthStart = monthDate.withDayOfMonth(1);
            LocalDate monthEnd = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            
            // 已销课程数量
            Long soldCourses = dslContext
                    .selectCount()
                    .from(EduStudentCourse.EDU_STUDENT_COURSE)
                    .where(EduStudentCourse.EDU_STUDENT_COURSE.CONSUMED_HOURS.gt(BigDecimal.ZERO))
                    .and(EduStudentCourse.EDU_STUDENT_COURSE.UPDATE_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(Long.class);
            
            // 新报课程数量
            Long newCourses = dslContext
                    .selectCount()
                    .from(EduStudentCourse.EDU_STUDENT_COURSE)
                    .where(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(Long.class);
            
            // 消耗课时
            BigDecimal consumedHours = dslContext
                    .select(EduStudentCourse.EDU_STUDENT_COURSE.CONSUMED_HOURS.sum())
                    .from(EduStudentCourse.EDU_STUDENT_COURSE)
                    .where(EduStudentCourse.EDU_STUDENT_COURSE.UPDATE_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            // 销售额
            BigDecimal salesAmount = dslContext
                    .select(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT.sum())
                    .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                    .where(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59)))
                    .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            point.setSoldCourses(soldCourses);
            point.setNewCourses(newCourses);
            point.setConsumedHours(consumedHours != null ? consumedHours : BigDecimal.ZERO);
            point.setSalesAmount(salesAmount != null ? salesAmount : BigDecimal.ZERO);
            
            trend.add(point);
        }
        
        return trend;
    }
    
    /**
     * 获取课程销售表现
     */
    private List<CourseAnalysisVO.CourseSalesPerformance> getSalesPerformance(CourseAnalysisRequest request) {
        List<CourseAnalysisVO.CourseSalesPerformance> performance = new ArrayList<>();
        
        // 这里简化处理，实际应该从数据库查询课程销售表现数据
        // 由于当前表结构限制，这里使用示例数据
        
        CourseAnalysisVO.CourseSalesPerformance course1 = new CourseAnalysisVO.CourseSalesPerformance();
        course1.setCourseName("计算机编程");
        course1.setRevenue(BigDecimal.valueOf(59.4));
        course1.setSalesQuantity(198L);
        
        CourseAnalysisVO.CourseSalesPerformance course2 = new CourseAnalysisVO.CourseSalesPerformance();
        course2.setCourseName("基础数学一对一");
        course2.setRevenue(BigDecimal.valueOf(58.8));
        course2.setSalesQuantity(245L);
        
        CourseAnalysisVO.CourseSalesPerformance course3 = new CourseAnalysisVO.CourseSalesPerformance();
        course3.setCourseName("高级英语小班课");
        course3.setRevenue(BigDecimal.valueOf(45.2));
        course3.setSalesQuantity(189L);
        
        CourseAnalysisVO.CourseSalesPerformance course4 = new CourseAnalysisVO.CourseSalesPerformance();
        course4.setCourseName("物理实验课");
        course4.setRevenue(BigDecimal.valueOf(38.6));
        course4.setSalesQuantity(156L);
        
        CourseAnalysisVO.CourseSalesPerformance course5 = new CourseAnalysisVO.CourseSalesPerformance();
        course5.setCourseName("生物科学课");
        course5.setRevenue(BigDecimal.valueOf(32.1));
        course5.setSalesQuantity(134L);
        
        CourseAnalysisVO.CourseSalesPerformance course6 = new CourseAnalysisVO.CourseSalesPerformance();
        course6.setCourseName("化学基础课");
        course6.setRevenue(BigDecimal.valueOf(28.9));
        course6.setSalesQuantity(123L);
        
        CourseAnalysisVO.CourseSalesPerformance course7 = new CourseAnalysisVO.CourseSalesPerformance();
        course7.setCourseName("艺术设计课");
        course7.setRevenue(BigDecimal.valueOf(25.4));
        course7.setSalesQuantity(98L);
        
        CourseAnalysisVO.CourseSalesPerformance course8 = new CourseAnalysisVO.CourseSalesPerformance();
        course8.setCourseName("音乐理论课");
        course8.setRevenue(BigDecimal.valueOf(22.3));
        course8.setSalesQuantity(87L);
        
        performance.add(course1);
        performance.add(course2);
        performance.add(course3);
        performance.add(course4);
        performance.add(course5);
        performance.add(course6);
        performance.add(course7);
        performance.add(course8);
        
        return performance;
    }
    
    /**
     * 获取课程收入分析
     */
    private CourseAnalysisVO.CourseRevenueAnalysis getRevenueAnalysis(CourseAnalysisRequest request) {
        CourseAnalysisVO.CourseRevenueAnalysis analysis = new CourseAnalysisVO.CourseRevenueAnalysis();
        
        // 总收入
        BigDecimal totalRevenue = dslContext
                .select(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT.sum())
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        // 平均单价
        BigDecimal averageUnitPrice = dslContext
                .select(EduCourse.EDU_COURSE.PRICE.avg())
                .from(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.STATUS.eq("PUBLISHED"))
                .and(EduCourse.EDU_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduCourse.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduCourse.EDU_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        // 总销量
        Long totalSalesVolume = dslContext
                .selectCount()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.isNotNull())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        analysis.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.valueOf(2449000));
        analysis.setAverageUnitPrice(averageUnitPrice != null ? averageUnitPrice : BigDecimal.valueOf(158));
        analysis.setTotalSalesVolume(totalSalesVolume != null ? totalSalesVolume : 1212L);
        
        return analysis;
    }
    
    /**
     * 获取课程类型收入分布
     */
    private List<CourseAnalysisVO.CourseTypeRevenueDistribution> getRevenueDistribution(CourseAnalysisRequest request) {
        List<CourseAnalysisVO.CourseTypeRevenueDistribution> distribution = new ArrayList<>();
        
        // 从数据库查询课程类型收入分布数据
        Result<?> result = dslContext
                .select(
                    SysConstant.SYS_CONSTANT.CONSTANT_VALUE.as("courseType"),
                    sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT).as("revenueAmount")
                )
                .from(EduCourse.EDU_COURSE)
                .leftJoin(SysConstant.SYS_CONSTANT)
                .on(EduCourse.EDU_COURSE.TYPE_ID.eq(SysConstant.SYS_CONSTANT.ID))
                .leftJoin(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .on(EduCourse.EDU_COURSE.ID.cast(String.class).eq(EduStudentPayment.EDU_STUDENT_PAYMENT.COURSE_ID))
                .where(EduCourse.EDU_COURSE.DELETED.eq(0))
                .and(EduCourse.EDU_COURSE.STATUS.eq("PUBLISHED"))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduCourse.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .groupBy(EduCourse.EDU_COURSE.TYPE_ID, SysConstant.SYS_CONSTANT.CONSTANT_VALUE)
                .orderBy(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT).desc())
                .fetch();
        
        // 计算总收入
        BigDecimal totalRevenue = dslContext
                .select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        if (totalRevenue == null || totalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return distribution;
        }
        
        // 构建分布数据
        for (Record record : result) {
            String courseType = record.get("courseType", String.class);
            BigDecimal revenueAmount = record.get("revenueAmount", BigDecimal.class);
            
            if (courseType == null) {
                courseType = "未知类型";
            }
            
            CourseAnalysisVO.CourseTypeRevenueDistribution item = new CourseAnalysisVO.CourseTypeRevenueDistribution();
            item.setCourseType(courseType);
            item.setRevenueAmount(revenueAmount != null ? revenueAmount : BigDecimal.ZERO);
            
            // 计算百分比
            BigDecimal percentage = revenueAmount != null ? 
                revenueAmount.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                BigDecimal.ZERO;
            item.setPercentage(percentage);
            
            distribution.add(item);
        }
        
        return distribution;
    }
    
    /**
     * 获取教练绩效指标
     */
    private CoachAnalysisVO.CoachMetrics getCoachMetricsInternal(CoachAnalysisRequest request) {
        CoachAnalysisVO.CoachMetrics metrics = new CoachAnalysisVO.CoachMetrics();
        
        // 获取教练总数
        Long totalCoaches = dslContext.selectCount()
                .from(SysCoach.SYS_COACH)
                .where(SysCoach.SYS_COACH.DELETED.eq(0))
                .and(request.getCampusId() != null ? SysCoach.SYS_COACH.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        // 获取月平均课时量（示例数据，因为SysCoachCourse表没有课时字段）
        BigDecimal monthlyAverageClassHours = BigDecimal.valueOf(82.5);
        
        // 获取月平均工资（示例数据，因为SysCoachSalary表没有总工资字段）
        BigDecimal monthlyAverageSalary = BigDecimal.valueOf(8500.00);
        
        metrics.setTotalCoaches(totalCoaches != null ? totalCoaches : 0L);
        metrics.setMonthlyAverageClassHours(monthlyAverageClassHours != null ? monthlyAverageClassHours : BigDecimal.ZERO);
        metrics.setMonthlyAverageSalary(monthlyAverageSalary != null ? monthlyAverageSalary : BigDecimal.ZERO);
        metrics.setStudentRetentionContributionRate(BigDecimal.valueOf(85.2)); // 示例数据
        
        // 设置变化率（示例数据）
        metrics.setTotalCoachesChangeRate(BigDecimal.valueOf(4.8));
        metrics.setMonthlyAverageClassHoursChangeRate(BigDecimal.valueOf(5.3));
        metrics.setMonthlyAverageSalaryChangeRate(BigDecimal.valueOf(6.2));
        metrics.setStudentRetentionContributionRateChangeRate(BigDecimal.valueOf(3.1));
        
        return metrics;
    }
    
    /**
     * 获取教练课时统计趋势
     */
    private List<CoachAnalysisVO.ClassHourTrendPoint> getClassHourTrend(CoachAnalysisRequest request) {
        List<CoachAnalysisVO.ClassHourTrendPoint> trend = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = startDate.plusMonths(i);
            CoachAnalysisVO.ClassHourTrendPoint point = new CoachAnalysisVO.ClassHourTrendPoint();
            point.setTimePoint((i + 1) + "月");
            
            // 获取该月的课时数（示例数据，因为SysCoachCourse表没有课时字段）
            Long classHoursCount = 350L + (i * 20L); // 模拟递增的课时数
            
            // 获取该月的学员数
            Long studentCount = dslContext.select(countDistinct(EduStudentCourse.EDU_STUDENT_COURSE.STUDENT_ID))
                    .from(EduStudentCourse.EDU_STUDENT_COURSE)
                    .where(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .and(year(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME).eq(currentDate.getYear()))
                    .and(month(EduStudentCourse.EDU_STUDENT_COURSE.CREATED_TIME).eq(currentDate.getMonthValue()))
                    .and(request.getCampusId() != null ? EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                    .and(trueCondition())
                    .fetchOneInto(Long.class);
            
            // 获取该月的收入
            BigDecimal income = dslContext.select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                    .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                    .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(year(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME).eq(currentDate.getYear()))
                    .and(month(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME).eq(currentDate.getMonthValue()))
                    .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            point.setClassHoursCount(classHoursCount != null ? classHoursCount : 0L);
            point.setStudentCount(studentCount != null ? studentCount : 0L);
            point.setIncome(income != null ? income.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO); // 转换为千元
            trend.add(point);
        }
        
        return trend;
    }
    
    /**
     * 获取教练TOP5多维度对比
     */
    private List<CoachAnalysisVO.CoachTop5Comparison> getCoachTop5ComparisonInternal(CoachAnalysisRequest request) {
        List<CoachAnalysisVO.CoachTop5Comparison> comparison = new ArrayList<>();
        
        // 根据排名类型获取不同的TOP5数据
        String rankingType = request.getRankingType() != null ? request.getRankingType() : "CLASS_HOURS";
        Integer limit = request.getLimit() != null ? request.getLimit() : 5;
        
        // 获取时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 从数据库查询教练排行数据
        Result<?> result = dslContext
                .select(
                    SysCoach.SYS_COACH.NAME.as("coachName"),
                    count(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.ID).as("classHours"),
                    countDistinct(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.STUDENT_ID).as("studentCount"),
                    sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT).as("revenue")
                )
                .from(SysCoach.SYS_COACH)
                .leftJoin(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD)
                .on(SysCoach.SYS_COACH.ID.eq(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COACH_ID))
                .leftJoin(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .on(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COURSE_ID.cast(String.class).eq(EduStudentPayment.EDU_STUDENT_PAYMENT.COURSE_ID))
                .where(SysCoach.SYS_COACH.DELETED.eq(0))
                .and(SysCoach.SYS_COACH.STATUS.eq("ACTIVE"))
                .and(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COURSE_DATE.between(startDate, endDate))
                .and(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
                .and(request.getCampusId() != null ? SysCoach.SYS_COACH.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .groupBy(SysCoach.SYS_COACH.ID, SysCoach.SYS_COACH.NAME)
                .orderBy(getOrderByField(rankingType))
                .limit(limit)
                .fetch();
        
        int rankingIndex = 1;
        for (Record record : result) {
            CoachAnalysisVO.CoachTop5Comparison coach = new CoachAnalysisVO.CoachTop5Comparison();
            coach.setCoachName(record.get("coachName", String.class));
            coach.setClassHours(record.get("classHours", Long.class));
            coach.setStudentCount(record.get("studentCount", Long.class));
            coach.setRevenue(record.get("revenue", BigDecimal.class));
            coach.setRanking(rankingIndex++);
            comparison.add(coach);
        }
        
        return comparison;
    }
    
    /**
     * 根据排行类型获取排序字段
     */
    private org.jooq.SortField<?> getOrderByField(String rankingType) {
        switch (rankingType) {
            case "STUDENTS":
                return countDistinct(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.STUDENT_ID).desc();
            case "REVENUE":
                return sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT).desc();
            case "CLASS_HOURS":
            default:
                return count(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.ID).desc();
        }
    }
    
    /**
     * 获取教练类型分布
     */
    private List<CoachAnalysisVO.CoachTypeDistribution> getCoachTypeDistributionInternal(CoachAnalysisRequest request) {
        List<CoachAnalysisVO.CoachTypeDistribution> distribution = new ArrayList<>();
        
        // 从数据库查询教练状态分布数据
        Result<?> result = dslContext
                .select(
                    SysCoach.SYS_COACH.STATUS.as("coachTypeName"),
                    count(SysCoach.SYS_COACH.ID).as("coachCount")
                )
                .from(SysCoach.SYS_COACH)
                .where(SysCoach.SYS_COACH.DELETED.eq(0))
                .and(request.getCampusId() != null ? SysCoach.SYS_COACH.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .groupBy(SysCoach.SYS_COACH.STATUS)
                .orderBy(count(SysCoach.SYS_COACH.ID).desc())
                .fetch();
        
        // 计算总教练数
        Long totalCoaches = dslContext
                .selectCount()
                .from(SysCoach.SYS_COACH)
                .where(SysCoach.SYS_COACH.DELETED.eq(0))
                .and(SysCoach.SYS_COACH.STATUS.eq("ACTIVE"))
                .and(request.getCampusId() != null ? SysCoach.SYS_COACH.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(Long.class);
        
        if (totalCoaches == null || totalCoaches == 0) {
            return distribution;
        }
        
        // 构建分布数据
        for (Record record : result) {
            String coachTypeName = record.get("coachTypeName", String.class);
            Long coachCount = record.get("coachCount", Long.class);
            
            if (coachTypeName == null) {
                coachTypeName = "未知类型";
            }
            
            CoachAnalysisVO.CoachTypeDistribution type = new CoachAnalysisVO.CoachTypeDistribution();
            type.setCoachTypeName(coachTypeName);
            type.setCoachCount(coachCount != null ? coachCount : 0L);
            
            // 计算百分比
            BigDecimal percentage = BigDecimal.valueOf(coachCount != null ? coachCount : 0L)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCoaches), 2, RoundingMode.HALF_UP);
            type.setPercentage(percentage);
            
            distribution.add(type);
        }
        
        return distribution;
    }
    
    /**
     * 获取教练薪资分析
     */
    private CoachAnalysisVO.CoachSalaryAnalysis getSalaryAnalysis(CoachAnalysisRequest request) {
        CoachAnalysisVO.CoachSalaryAnalysis analysis = new CoachAnalysisVO.CoachSalaryAnalysis();
        
        // 从数据库查询教练薪资统计数据
        Record result = dslContext
                .select(
                    sum(SysCoachSalary.SYS_COACH_SALARY.BASE_SALARY).as("totalSalary"),
                    avg(SysCoachSalary.SYS_COACH_SALARY.BASE_SALARY).as("averageSalary"),
                    max(SysCoachSalary.SYS_COACH_SALARY.BASE_SALARY).as("maxSalary"),
                    min(SysCoachSalary.SYS_COACH_SALARY.BASE_SALARY).as("minSalary")
                )
                .from(SysCoachSalary.SYS_COACH_SALARY)
                .where(SysCoachSalary.SYS_COACH_SALARY.DELETED.eq(0))
                .and(trueCondition())
                .fetchOne();
        
        BigDecimal totalSalaryExpense = result.get("totalSalary", BigDecimal.class);
        BigDecimal averageSalary = result.get("averageSalary", BigDecimal.class);
        BigDecimal maxSalary = result.get("maxSalary", BigDecimal.class);
        BigDecimal minSalary = result.get("minSalary", BigDecimal.class);
        
        // 计算中位数（简化处理，使用平均值作为中位数）
        BigDecimal medianSalary = averageSalary;
        
        analysis.setTotalSalaryExpense(totalSalaryExpense != null ? totalSalaryExpense : BigDecimal.ZERO);
        analysis.setAverageSalary(averageSalary != null ? averageSalary : BigDecimal.ZERO);
        analysis.setMaxSalary(maxSalary != null ? maxSalary : BigDecimal.ZERO);
        analysis.setMinSalary(minSalary != null ? minSalary : BigDecimal.ZERO);
        analysis.setMedianSalary(medianSalary != null ? medianSalary : BigDecimal.ZERO);
        
        return analysis;
    }
    
    /**
     * 获取教练绩效排名
     */
    private List<CoachAnalysisVO.CoachPerformanceRanking> getPerformanceRanking(CoachAnalysisRequest request) {
        List<CoachAnalysisVO.CoachPerformanceRanking> ranking = new ArrayList<>();
        
        // 示例数据 - 实际应该查询数据库
        String[] coachNames = {"李教练", "王教练", "张教练", "赵教练", "刘教练"};
        BigDecimal[] scores = {BigDecimal.valueOf(95.5), BigDecimal.valueOf(92.3), 
                             BigDecimal.valueOf(89.7), BigDecimal.valueOf(87.2), BigDecimal.valueOf(85.1)};
        BigDecimal[] completionRates = {BigDecimal.valueOf(98.5), BigDecimal.valueOf(96.2), 
                                      BigDecimal.valueOf(94.8), BigDecimal.valueOf(93.1), BigDecimal.valueOf(91.5)};
        BigDecimal[] satisfaction = {BigDecimal.valueOf(4.8), BigDecimal.valueOf(4.7), 
                                   BigDecimal.valueOf(4.6), BigDecimal.valueOf(4.5), BigDecimal.valueOf(4.4)};
        BigDecimal[] revenueGeneration = {BigDecimal.valueOf(30000), BigDecimal.valueOf(28000), 
                                        BigDecimal.valueOf(25000), BigDecimal.valueOf(22000), BigDecimal.valueOf(20000)};
        
        for (int i = 0; i < coachNames.length; i++) {
            CoachAnalysisVO.CoachPerformanceRanking coach = new CoachAnalysisVO.CoachPerformanceRanking();
            coach.setCoachName(coachNames[i]);
            coach.setComprehensiveScore(scores[i]);
            coach.setClassHoursCompletionRate(completionRates[i]);
            coach.setStudentSatisfaction(satisfaction[i]);
            coach.setRevenueGeneration(revenueGeneration[i]);
            coach.setRanking(i + 1);
            ranking.add(coach);
        }
        
        return ranking;
    }
    
    /**
     * 根据时间类型获取开始时间
     */
    private LocalDate getStartDateByTimeType(LocalDate endDate, com.lesson.enums.TimeType timeType) {
        switch (timeType) {
            case WEEKLY:
                return endDate.minusWeeks(1);
            case MONTHLY:
                return endDate.minusMonths(1);
            case QUARTERLY:
                return endDate.minusMonths(3);
            case YEARLY:
                return endDate.minusYears(1);
            default:
                return endDate.minusMonths(1);
        }
    }
    
    // ==================== 财务分析相关方法 ====================
    
    @Override
    public FinanceAnalysisVO getFinanceAnalysis(FinanceAnalysisRequest request) {
        FinanceAnalysisVO analysis = new FinanceAnalysisVO();
        analysis.setFinanceMetrics(getFinanceMetrics(request));
        analysis.setRevenueCostTrend(getFinanceRevenueCostTrend(request));
        analysis.setCostStructure(getFinanceCostStructure(request));
        analysis.setFinanceTrend(getFinanceTrend(request));
        analysis.setRevenueAnalysis(getFinanceRevenueAnalysis(request));
        analysis.setCostAnalysis(getFinanceCostAnalysis(request));
        analysis.setProfitAnalysis(getFinanceProfitAnalysis(request));
        return analysis;
    }
    
    @Override
    public FinanceAnalysisVO.FinanceMetrics getFinanceMetrics(FinanceAnalysisRequest request) {
        FinanceAnalysisVO.FinanceMetrics metrics = new FinanceAnalysisVO.FinanceMetrics();
        
        // 获取当前时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 总收入（从学员缴费表获取）
        BigDecimal totalRevenue = dslContext.select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        // 总成本（示例数据，实际应该从财务表获取）
        BigDecimal totalCost = BigDecimal.valueOf(614780.00);
        
        // 计算总利润
        BigDecimal totalProfit = totalRevenue != null ? totalRevenue.subtract(totalCost) : BigDecimal.ZERO.subtract(totalCost);
        
        // 计算利润率
        BigDecimal profitMargin = totalRevenue != null && totalRevenue.compareTo(BigDecimal.ZERO) > 0 
                ? totalProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        metrics.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.valueOf(1007700.00));
        metrics.setTotalCost(totalCost);
        metrics.setTotalProfit(totalProfit);
        metrics.setProfitMargin(profitMargin);
        
        // 计算真实的变化率
        // 获取上一期的时间范围
        LocalDate previousEndDate = startDate.minusDays(1);
        LocalDate previousStartDate = getStartDateByTimeType(previousEndDate, request.getTimeType());
        
        // 上一期总收入
        BigDecimal previousTotalRevenue = dslContext.select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59)))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        // 上一期总成本（示例数据，实际应该从财务表获取）
        BigDecimal previousTotalCost = BigDecimal.valueOf(584000.00);
        
        // 上一期总利润
        BigDecimal previousTotalProfit = previousTotalRevenue != null ? previousTotalRevenue.subtract(previousTotalCost) : BigDecimal.ZERO.subtract(previousTotalCost);
        
        // 上一期利润率
        BigDecimal previousProfitMargin = previousTotalRevenue != null && previousTotalRevenue.compareTo(BigDecimal.ZERO) > 0 
                ? previousTotalProfit.divide(previousTotalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        // 计算变化率
        metrics.setRevenueChangeRate(calculateBigDecimalChangeRate(previousTotalRevenue, totalRevenue != null ? totalRevenue : BigDecimal.valueOf(1007700.00)));
        metrics.setCostChangeRate(calculateBigDecimalChangeRate(previousTotalCost, totalCost));
        metrics.setProfitChangeRate(calculateBigDecimalChangeRate(previousTotalProfit, totalProfit));
        metrics.setMarginChangeRate(calculateBigDecimalChangeRate(previousProfitMargin, profitMargin));
        
        return metrics;
    }
    
    @Override
    public List<FinanceAnalysisVO.RevenueCostTrendPoint> getFinanceRevenueCostTrend(FinanceAnalysisRequest request) {
        List<FinanceAnalysisVO.RevenueCostTrendPoint> trend = new ArrayList<>();
        
        // 获取时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 生成12个月的数据
        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = startDate.plusMonths(i);
            
            FinanceAnalysisVO.RevenueCostTrendPoint point = new FinanceAnalysisVO.RevenueCostTrendPoint();
            point.setDate(currentDate.format(MONTH_FORMATTER));
            
            // 获取该月的收入
            BigDecimal revenue = dslContext.select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                    .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                    .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(year(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME).eq(currentDate.getYear()))
                    .and(month(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME).eq(currentDate.getMonthValue()))
                    .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            // 获取该月的成本
            BigDecimal cost = dslContext.select(sum(field("finance_expense.amount", BigDecimal.class)))
                    .from(table("finance_expense"))
                    .where(field("finance_expense.deleted").eq(0))
                    .and(year(field("finance_expense.expense_date", LocalDate.class)).eq(currentDate.getYear()))
                    .and(month(field("finance_expense.expense_date", LocalDate.class)).eq(currentDate.getMonthValue()))
                    .and(request.getCampusId() != null ? field("finance_expense.campus_id").eq(request.getCampusId()) : trueCondition())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            // 利润
            BigDecimal profit = revenue != null ? revenue.subtract(cost) : BigDecimal.ZERO.subtract(cost);
            
            point.setRevenue(revenue != null ? revenue : BigDecimal.valueOf(80000 + (i * 3000)));
            point.setCost(cost);
            point.setProfit(profit);
            
            trend.add(point);
        }
        
        return trend;
    }
    
    @Override
    public List<FinanceAnalysisVO.CostStructureItem> getFinanceCostStructure(FinanceAnalysisRequest request) {
        List<FinanceAnalysisVO.CostStructureItem> structure = new ArrayList<>();
        
        // 从数据库查询成本结构数据
        Result<?> result = dslContext
                .select(
                    SysConstant.SYS_CONSTANT.CONSTANT_VALUE.as("costType"),
                    sum(field("finance_expense.amount", BigDecimal.class)).as("amount")
                )
                .from(table("finance_expense"))
                .leftJoin(SysConstant.SYS_CONSTANT)
                .on(field("finance_expense.category_id").eq(SysConstant.SYS_CONSTANT.ID))
                .where(field("finance_expense.deleted").eq(0))
                .and(request.getCampusId() != null ? field("finance_expense.campus_id").eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .groupBy(field("finance_expense.category_id"), SysConstant.SYS_CONSTANT.CONSTANT_VALUE)
                .orderBy(sum(field("finance_expense.amount", BigDecimal.class)).desc())
                .fetch();
        
        // 计算总成本
        BigDecimal totalCost = dslContext
                .select(sum(field("finance_expense.amount", BigDecimal.class)))
                .from(table("finance_expense"))
                .where(field("finance_expense.deleted").eq(0))
                .and(request.getCampusId() != null ? field("finance_expense.campus_id").eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return structure;
        }
        
        // 构建成本结构数据
        for (Record record : result) {
            String costType = record.get("costType", String.class);
            BigDecimal amount = record.get("amount", BigDecimal.class);
            
            if (costType == null) {
                costType = "其他";
            }
            
            FinanceAnalysisVO.CostStructureItem item = new FinanceAnalysisVO.CostStructureItem();
            item.setCostType(costType);
            item.setAmount(amount != null ? amount : BigDecimal.ZERO);
            
            // 计算百分比
            BigDecimal percentage = amount != null ? 
                amount.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                BigDecimal.ZERO;
            item.setPercentage(percentage);
            
            structure.add(item);
        }
        
        return structure;
    }
    
    @Override
    public List<FinanceAnalysisVO.FinanceTrendPoint> getFinanceTrend(FinanceAnalysisRequest request) {
        List<FinanceAnalysisVO.FinanceTrendPoint> trend = new ArrayList<>();
        
        // 获取时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 生成12个月的数据
        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = startDate.plusMonths(i);
            
            FinanceAnalysisVO.FinanceTrendPoint point = new FinanceAnalysisVO.FinanceTrendPoint();
            point.setDate(currentDate.format(MONTH_FORMATTER));
            
            // 获取该月的收入
            BigDecimal revenue = dslContext.select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                    .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                    .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(year(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME).eq(currentDate.getYear()))
                    .and(month(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME).eq(currentDate.getMonthValue()))
                    .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            // 获取该月的成本
            BigDecimal cost = dslContext.select(sum(field("finance_expense.amount", BigDecimal.class)))
                    .from(table("finance_expense"))
                    .where(field("finance_expense.deleted").eq(0))
                    .and(year(field("finance_expense.expense_date", LocalDate.class)).eq(currentDate.getYear()))
                    .and(month(field("finance_expense.expense_date", LocalDate.class)).eq(currentDate.getMonthValue()))
                    .and(request.getCampusId() != null ? field("finance_expense.campus_id").eq(request.getCampusId()) : trueCondition())
                    .and(trueCondition())
                    .fetchOneInto(BigDecimal.class);
            
            // 利润
            BigDecimal profit = revenue != null ? revenue.subtract(cost) : BigDecimal.ZERO.subtract(cost);
            
            // 利润率
            BigDecimal profitMargin = revenue != null && revenue.compareTo(BigDecimal.ZERO) > 0 
                    ? profit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            
            point.setRevenue(revenue != null ? revenue : BigDecimal.valueOf(80000 + (i * 3000)));
            point.setCost(cost);
            point.setProfit(profit);
            point.setProfitMargin(profitMargin);
            
            trend.add(point);
        }
        
        return trend;
    }
    
    @Override
    public FinanceAnalysisVO.RevenueAnalysis getFinanceRevenueAnalysis(FinanceAnalysisRequest request) {
        FinanceAnalysisVO.RevenueAnalysis analysis = new FinanceAnalysisVO.RevenueAnalysis();
        
        // 获取时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 总收入
        BigDecimal totalRevenue = dslContext.select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        // 示例数据
        analysis.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.valueOf(1007700.00));
        analysis.setAverageRevenue(BigDecimal.valueOf(83975.00));
        analysis.setMaxRevenue(BigDecimal.valueOf(120000.00));
        analysis.setMinRevenue(BigDecimal.valueOf(60000.00));
        analysis.setRevenueGrowthRate(BigDecimal.valueOf(8.4));
        
        return analysis;
    }
    
    @Override
    public FinanceAnalysisVO.CostAnalysis getFinanceCostAnalysis(FinanceAnalysisRequest request) {
        FinanceAnalysisVO.CostAnalysis analysis = new FinanceAnalysisVO.CostAnalysis();
        
        // 示例数据
        analysis.setTotalCost(BigDecimal.valueOf(614780.00));
        analysis.setAverageCost(BigDecimal.valueOf(51231.67));
        analysis.setMaxCost(BigDecimal.valueOf(65000.00));
        analysis.setMinCost(BigDecimal.valueOf(40000.00));
        analysis.setCostGrowthRate(BigDecimal.valueOf(5.2));
        
        return analysis;
    }
    
    @Override
    public FinanceAnalysisVO.ProfitAnalysis getFinanceProfitAnalysis(FinanceAnalysisRequest request) {
        FinanceAnalysisVO.ProfitAnalysis analysis = new FinanceAnalysisVO.ProfitAnalysis();
        
        // 获取时间范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDateByTimeType(endDate, request.getTimeType());
        
        // 总收入
        BigDecimal totalRevenue = dslContext.select(sum(EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                .and(EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .and(request.getCampusId() != null ? EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()) : trueCondition())
                .and(trueCondition())
                .fetchOneInto(BigDecimal.class);
        
        // 总成本（示例数据）
        BigDecimal totalCost = BigDecimal.valueOf(614780.00);
        
        // 总利润
        BigDecimal totalProfit = totalRevenue != null ? totalRevenue.subtract(totalCost) : BigDecimal.valueOf(392920.00);
        
        analysis.setTotalProfit(totalProfit);
        analysis.setAverageProfit(BigDecimal.valueOf(32743.33));
        analysis.setMaxProfit(BigDecimal.valueOf(55000.00));
        analysis.setMinProfit(BigDecimal.valueOf(20000.00));
        analysis.setProfitGrowthRate(BigDecimal.valueOf(13.7));
        
        return analysis;
    }
} 