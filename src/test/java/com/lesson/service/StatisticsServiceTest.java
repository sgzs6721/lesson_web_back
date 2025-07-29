package com.lesson.service;

import com.lesson.repository.tables.EduStudent;
import com.lesson.repository.tables.EduStudentCourse;
import com.lesson.repository.tables.EduStudentPayment;
import com.lesson.repository.tables.EduCourse;
import com.lesson.repository.tables.records.EduStudentRecord;
import com.lesson.repository.tables.records.EduStudentCourseRecord;
import com.lesson.repository.tables.records.EduStudentPaymentRecord;
import com.lesson.repository.tables.records.EduCourseRecord;
import com.lesson.vo.request.StudentAnalysisRequest;
import com.lesson.vo.request.CourseAnalysisRequest;
import com.lesson.vo.response.StudentAnalysisVO;
import com.lesson.vo.response.CourseAnalysisVO;
import com.lesson.enums.TimeType;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统计服务测试类 - 使用真实数据库操作
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("统计服务测试")
@Transactional
class StatisticsServiceTest {
    
    @Autowired
    private com.lesson.service.impl.StatisticsServiceImpl statisticsService;
    
    @Autowired
    private DSLContext dslContext;
    
    private StudentAnalysisRequest studentRequest;
    private CourseAnalysisRequest courseRequest;
    
    @BeforeEach
    void setUp() {
        studentRequest = new StudentAnalysisRequest();
        studentRequest.setTimeType(TimeType.MONTHLY);
        studentRequest.setCampusId(1L);
        
        courseRequest = new CourseAnalysisRequest();
        courseRequest.setTimeType(TimeType.MONTHLY);
        courseRequest.setCampusId(1L);
        
        // 清理测试数据
        cleanupTestData();
        // 插入测试数据
        insertTestData();
    }
    
    /**
     * 清理测试数据
     */
    private void cleanupTestData() {
        dslContext.deleteFrom(EduStudentPayment.EDU_STUDENT_PAYMENT)
                .where(EduStudentPayment.EDU_STUDENT_PAYMENT.STUDENT_ID.eq("TEST_STUDENT_001"))
                .execute();
        
        dslContext.deleteFrom(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.STUDENT_ID.eq(1L))
                .execute();
        
        dslContext.deleteFrom(EduStudent.EDU_STUDENT)
                .where(EduStudent.EDU_STUDENT.ID.eq(1L))
                .execute();
        
        dslContext.deleteFrom(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.ID.eq(1L))
                .execute();
    }
    
    /**
     * 插入测试数据
     */
    private void insertTestData() {
        // 插入测试课程
        EduCourseRecord courseRecord = dslContext.newRecord(EduCourse.EDU_COURSE);
        courseRecord.setId(1L);
        courseRecord.setName("测试课程");
        courseRecord.setTypeId(1L);
        courseRecord.setStatus("PUBLISHED");
        courseRecord.setUnitHours(BigDecimal.valueOf(1.0));
        courseRecord.setTotalHours(BigDecimal.valueOf(10.0));
        courseRecord.setConsumedHours(BigDecimal.valueOf(0.0));
        courseRecord.setPrice(BigDecimal.valueOf(200.00));
        courseRecord.setCoachFee(BigDecimal.valueOf(50.00));
        courseRecord.setDescription("测试课程描述");
        courseRecord.setCampusId(1L);
        courseRecord.setInstitutionId(1L);
        courseRecord.setCreatedTime(LocalDateTime.now());
        courseRecord.setUpdateTime(LocalDateTime.now());
        courseRecord.setDeleted(0);
        courseRecord.insert();
        
        // 插入测试学员
        EduStudentRecord studentRecord = dslContext.newRecord(EduStudent.EDU_STUDENT);
        studentRecord.setId(1L);
        studentRecord.setName("测试学员");
        studentRecord.setGender("MALE");
        studentRecord.setAge(25);
        studentRecord.setPhone("13800138000");
        studentRecord.setCampusId(1L);
        studentRecord.setInstitutionId(1L);
        studentRecord.setStatus("STUDYING");
        studentRecord.setCreatedTime(LocalDateTime.now());
        studentRecord.setUpdateTime(LocalDateTime.now());
        studentRecord.setDeleted(0);
        studentRecord.insert();
        
        // 插入测试学员课程关系
        EduStudentCourseRecord studentCourseRecord = dslContext.newRecord(EduStudentCourse.EDU_STUDENT_COURSE);
        studentCourseRecord.setId(1L);
        studentCourseRecord.setStudentId(1L);
        studentCourseRecord.setCourseId(1L);
        studentCourseRecord.setTotalHours(BigDecimal.valueOf(10.0));
        studentCourseRecord.setConsumedHours(BigDecimal.valueOf(3.0));
        studentCourseRecord.setStatus("STUDYING");
        studentCourseRecord.setStartDate(LocalDate.now().minusMonths(1));
        studentCourseRecord.setEndDate(LocalDate.now().plusMonths(6));
        studentCourseRecord.setCampusId(1L);
        studentCourseRecord.setInstitutionId(1L);
        studentCourseRecord.setCreatedTime(LocalDateTime.now());
        studentCourseRecord.setUpdateTime(LocalDateTime.now());
        studentCourseRecord.setDeleted(0);
        studentCourseRecord.insert();
        
        // 插入测试缴费记录
        EduStudentPaymentRecord paymentRecord = dslContext.newRecord(EduStudentPayment.EDU_STUDENT_PAYMENT);
        paymentRecord.setId(1L);
        paymentRecord.setStudentId("1");
        paymentRecord.setCourseId("1");
        paymentRecord.setPaymentType("NEW");
        paymentRecord.setAmount(BigDecimal.valueOf(2000.00));
        paymentRecord.setPaymentMethod("WECHAT");
        paymentRecord.setCourseHours(BigDecimal.valueOf(10.0));
        paymentRecord.setGiftHours(BigDecimal.valueOf(2.0));
        paymentRecord.setValidUntil(LocalDate.now().plusMonths(6));
        paymentRecord.setCampusId(1L);
        paymentRecord.setInstitutionId(1L);
        paymentRecord.setCreatedTime(LocalDateTime.now());
        paymentRecord.setUpdateTime(LocalDateTime.now());
        paymentRecord.setDeleted(0);
        paymentRecord.insert();
    }
    
    @Test
    @DisplayName("测试学员分析统计 - 验证总学员数计算逻辑")
    void testGetStudentAnalysis_TotalStudentsCalculation() {
        // When: 执行被测试方法
        StudentAnalysisVO result = statisticsService.getStudentAnalysis(studentRequest);
        
        // Then: 验证业务逻辑
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getStudentMetrics(), "学员指标不应为空");
        
        // 验证总学员数应该等于我们插入的测试数据
        assertTrue(result.getStudentMetrics().getTotalStudents() >= 1L, "总学员数应该至少包含我们插入的测试学员");
        
        // 验证新增学员数应该包含我们插入的测试学员
        assertTrue(result.getStudentMetrics().getNewStudents() >= 1L, "新增学员数应该至少包含我们插入的测试学员");
        
        // 验证已销课程数应该包含我们插入的测试数据
        assertTrue(result.getStudentMetrics().getRenewingStudents() >= 0L, "续费学员数应该大于等于0");
    }
    
    @Test
    @DisplayName("测试学员分析统计 - 验证时间范围过滤逻辑")
    void testGetStudentAnalysis_TimeRangeFilter() {
        // Given: 设置不同的时间类型
        studentRequest.setTimeType(TimeType.WEEKLY);
        
        // When: 执行被测试方法
        StudentAnalysisVO result = statisticsService.getStudentAnalysis(studentRequest);
        
        // Then: 验证时间范围过滤逻辑
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getStudentMetrics(), "学员指标不应为空");
        
        // 验证增长趋势数据点数量（周度应该有52个数据点）
        assertNotNull(result.getGrowthTrend(), "增长趋势不应为空");
        assertEquals(12, result.getGrowthTrend().size(), "增长趋势应该有12个数据点（过去12个月）");
        
        // 验证每个数据点都有正确的结构
        result.getGrowthTrend().forEach(point -> {
            assertNotNull(point.getTimePoint(), "时间点不应为空");
            assertNotNull(point.getTotalStudents(), "总学员数不应为空");
            assertNotNull(point.getNewStudents(), "新增学员数不应为空");
            assertNotNull(point.getRenewingStudents(), "续费学员数不应为空");
            assertNotNull(point.getLostStudents(), "流失学员数不应为空");
            assertNotNull(point.getRetentionRate(), "留存率不应为空");
        });
    }
    
    @Test
    @DisplayName("测试学员分析统计 - 验证校区过滤逻辑")
    void testGetStudentAnalysis_CampusFilter() {
        // Given: 设置不同的校区
        studentRequest.setCampusId(999L); // 不存在的校区
        
        // When: 执行被测试方法
        StudentAnalysisVO result = statisticsService.getStudentAnalysis(studentRequest);
        
        // Then: 验证校区过滤逻辑
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getStudentMetrics(), "学员指标不应为空");
        
        // 由于设置了不存在的校区，应该返回0或很少的数据
        assertEquals(0L, result.getStudentMetrics().getTotalStudents(), "不存在的校区应该返回0个学员");
    }
    
    @Test
    @DisplayName("测试学员分析统计 - 验证增长趋势数据点数量")
    void testGetStudentAnalysis_GrowthTrendDataPoints() {
        // When: 执行被测试方法
        StudentAnalysisVO result = statisticsService.getStudentAnalysis(studentRequest);
        
        // Then: 验证增长趋势数据点数量（应该是12个月）
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getGrowthTrend(), "增长趋势不应为空");
        assertEquals(12, result.getGrowthTrend().size(), "增长趋势应该有12个数据点（过去12个月）");
        
        // 验证每个数据点都有正确的结构
        result.getGrowthTrend().forEach(point -> {
            assertNotNull(point.getTimePoint(), "时间点不应为空");
            assertNotNull(point.getTotalStudents(), "总学员数不应为空");
            assertNotNull(point.getNewStudents(), "新增学员数不应为空");
            assertNotNull(point.getRenewingStudents(), "续费学员数不应为空");
            assertNotNull(point.getLostStudents(), "流失学员数不应为空");
            assertNotNull(point.getRetentionRate(), "留存率不应为空");
        });
    }
    
    @Test
    @DisplayName("测试课程分析统计 - 验证课程指标计算逻辑")
    void testGetCourseAnalysis_CourseMetricsCalculation() {
        // When: 执行被测试方法
        CourseAnalysisVO result = statisticsService.getCourseAnalysis(courseRequest);
        
        // Then: 验证业务逻辑
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getCourseMetrics(), "课程指标不应为空");
        
        // 验证课程总数应该包含我们插入的测试课程
        assertTrue(result.getCourseMetrics().getTotalCourses() >= 1L, "课程总数应该至少包含我们插入的测试课程");
        
        // 验证课程单价应该等于我们插入的测试数据
        assertEquals(BigDecimal.valueOf(200.00), result.getCourseMetrics().getCourseUnitPrice(), "课程单价应该等于我们插入的测试数据");
        
        // 验证新报课程数应该包含我们插入的测试数据
        assertTrue(result.getCourseMetrics().getNewCoursesEnrolled() >= 1L, "新报课程数应该至少包含我们插入的测试数据");
    }
    
    @Test
    @DisplayName("测试课程分析统计 - 验证课程类型分析数据结构")
    void testGetCourseAnalysis_CourseTypeAnalysisStructure() {
        // When: 执行被测试方法
        CourseAnalysisVO result = statisticsService.getCourseAnalysis(courseRequest);
        
        // Then: 验证课程类型分析数据结构
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getCourseTypeAnalysis(), "课程类型分析不应为空");
        assertEquals(4, result.getCourseTypeAnalysis().size(), "应该有4种课程类型：一对一、一对二、小班课、大班课");
        
        // 验证每种课程类型都有完整的数据结构
        result.getCourseTypeAnalysis().forEach(analysis -> {
            assertNotNull(analysis.getCourseTypeName(), "课程类型名称不应为空");
            assertNotNull(analysis.getTotalCourseHours(), "总课时数不应为空");
            assertNotNull(analysis.getEnrolledTotalHours(), "报名总课时不应为空");
            assertNotNull(analysis.getSoldHours(), "已销课时不应为空");
            assertNotNull(analysis.getRemainingHours(), "剩余课时不应为空");
            assertNotNull(analysis.getSalesAmount(), "销售额不应为空");
            assertNotNull(analysis.getAverageUnitPrice(), "平均课单价不应为空");
        });
    }
    
    @Test
    @DisplayName("测试课程分析统计 - 验证销售趋势数据点数量")
    void testGetCourseAnalysis_SalesTrendDataPoints() {
        // When: 执行被测试方法
        CourseAnalysisVO result = statisticsService.getCourseAnalysis(courseRequest);
        
        // Then: 验证销售趋势数据点数量（应该是12个月）
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getSalesTrend(), "销售趋势不应为空");
        assertEquals(12, result.getSalesTrend().size(), "销售趋势应该有12个数据点（过去12个月）");
        
        // 验证每个数据点都有正确的结构
        result.getSalesTrend().forEach(point -> {
            assertNotNull(point.getTimePoint(), "时间点不应为空");
            assertNotNull(point.getSoldCourses(), "已销课程数量不应为空");
            assertNotNull(point.getNewCourses(), "新报课程数量不应为空");
            assertNotNull(point.getConsumedHours(), "消耗课时不应为空");
            assertNotNull(point.getSalesAmount(), "销售额不应为空");
        });
    }
    
    @Test
    @DisplayName("测试课程分析统计 - 验证销售表现数据结构")
    void testGetCourseAnalysis_SalesPerformanceStructure() {
        // When: 执行被测试方法
        CourseAnalysisVO result = statisticsService.getCourseAnalysis(courseRequest);
        
        // Then: 验证销售表现数据结构
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getSalesPerformance(), "销售表现不应为空");
        assertEquals(8, result.getSalesPerformance().size(), "应该有8个课程的销售表现数据");
        
        // 验证每个销售表现数据都有正确的结构
        result.getSalesPerformance().forEach(performance -> {
            assertNotNull(performance.getCourseName(), "课程名称不应为空");
            assertNotNull(performance.getRevenue(), "收入不应为空");
            assertNotNull(performance.getSalesQuantity(), "销售数量不应为空");
            assertTrue(performance.getRevenue().compareTo(BigDecimal.ZERO) >= 0, "收入应该大于等于0");
            assertTrue(performance.getSalesQuantity() >= 0, "销售数量应该大于等于0");
        });
    }
    
    @Test
    @DisplayName("测试课程分析统计 - 验证收入分析数据结构")
    void testGetCourseAnalysis_RevenueAnalysisStructure() {
        // When: 执行被测试方法
        CourseAnalysisVO result = statisticsService.getCourseAnalysis(courseRequest);
        
        // Then: 验证收入分析数据结构
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getRevenueAnalysis(), "收入分析不应为空");
        assertNotNull(result.getRevenueAnalysis().getTotalRevenue(), "总收入不应为空");
        assertNotNull(result.getRevenueAnalysis().getAverageUnitPrice(), "平均单价不应为空");
        assertNotNull(result.getRevenueAnalysis().getTotalSalesVolume(), "总销量不应为空");
        
        // 验证数值的合理性
        assertTrue(result.getRevenueAnalysis().getTotalRevenue().compareTo(BigDecimal.ZERO) >= 0, "总收入应该大于等于0");
        assertTrue(result.getRevenueAnalysis().getAverageUnitPrice().compareTo(BigDecimal.ZERO) >= 0, "平均单价应该大于等于0");
        assertTrue(result.getRevenueAnalysis().getTotalSalesVolume() >= 0, "总销量应该大于等于0");
    }
    
    @Test
    @DisplayName("测试课程分析统计 - 验证收入分布数据结构")
    void testGetCourseAnalysis_RevenueDistributionStructure() {
        // When: 执行被测试方法
        CourseAnalysisVO result = statisticsService.getCourseAnalysis(courseRequest);
        
        // Then: 验证收入分布数据结构
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getRevenueDistribution(), "收入分布不应为空");
        assertEquals(3, result.getRevenueDistribution().size(), "应该有3种课程类型的收入分布：一对一、小班课、大班课");
        
        // 验证每个收入分布数据都有正确的结构
        result.getRevenueDistribution().forEach(distribution -> {
            assertNotNull(distribution.getCourseType(), "课程类型不应为空");
            assertNotNull(distribution.getRevenueAmount(), "收入金额不应为空");
            assertNotNull(distribution.getPercentage(), "占比不应为空");
            assertTrue(distribution.getRevenueAmount().compareTo(BigDecimal.ZERO) >= 0, "收入金额应该大于等于0");
            assertTrue(distribution.getPercentage().compareTo(BigDecimal.ZERO) >= 0, "占比应该大于等于0");
            assertTrue(distribution.getPercentage().compareTo(BigDecimal.valueOf(100)) <= 0, "占比应该小于等于100");
        });
    }
    
    @Test
    @DisplayName("测试学员分析统计 - 验证续费金额趋势")
    void testGetStudentAnalysis_RenewalAmountTrend() {
        // When: 执行被测试方法
        StudentAnalysisVO result = statisticsService.getStudentAnalysis(studentRequest);
        
        // Then: 验证续费金额趋势
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getRenewalAmountTrend(), "续费金额趋势不应为空");
        assertEquals(12, result.getRenewalAmountTrend().size(), "续费金额趋势应该有12个数据点");
        
        // 验证每个数据点都有正确的结构
        result.getRenewalAmountTrend().forEach(point -> {
            assertNotNull(point.getTimePoint(), "时间点不应为空");
            assertNotNull(point.getRenewalAmount(), "续费金额不应为空");
            assertNotNull(point.getNewStudentPaymentAmount(), "新增学员缴费金额不应为空");
            assertTrue(point.getRenewalAmount().compareTo(BigDecimal.ZERO) >= 0, "续费金额应该大于等于0");
            assertTrue(point.getNewStudentPaymentAmount().compareTo(BigDecimal.ZERO) >= 0, "新增学员缴费金额应该大于等于0");
        });
    }
    
    @Test
    @DisplayName("测试学员分析统计 - 验证来源分布数据结构")
    void testGetStudentAnalysis_SourceDistributionStructure() {
        // When: 执行被测试方法
        StudentAnalysisVO result = statisticsService.getStudentAnalysis(studentRequest);
        
        // Then: 验证来源分布数据结构
        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getSourceDistribution(), "来源分布不应为空");
        assertFalse(result.getSourceDistribution().isEmpty(), "来源分布不应为空");
        
        // 验证每个来源分布数据都有正确的结构
        result.getSourceDistribution().forEach(distribution -> {
            assertNotNull(distribution.getSourceName(), "来源名称不应为空");
            assertNotNull(distribution.getStudentCount(), "学员数量不应为空");
            assertNotNull(distribution.getPercentage(), "占比不应为空");
            assertTrue(distribution.getStudentCount() >= 0, "学员数量应该大于等于0");
            assertTrue(distribution.getPercentage().compareTo(BigDecimal.ZERO) >= 0, "占比应该大于等于0");
        });
    }
} 