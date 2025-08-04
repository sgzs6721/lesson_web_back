package com.lesson.service;

import com.lesson.enums.CourseStatus;
import com.lesson.model.EduCourseModel;
import com.lesson.model.SysCoachModel;
import com.lesson.model.SysConstantModel;
import com.lesson.service.impl.CourseServiceImpl;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 课程服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("课程服务测试")
class CourseServiceTest {

    @Mock
    private EduCourseModel courseModel;

    @Mock
    private SysConstantModel constantModel;

    @Mock
    private SysCoachModel sysCoachModel;

    @Mock
    private MockHttpServletRequest httpServletRequest;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        // 设置默认的机构ID
        when(httpServletRequest.getAttribute("orgId")).thenReturn(1L);
    }

    @Test
    @DisplayName("测试创建课程 - 使用前端传入的状态")
    void testCreateCourse_UseFrontendStatus() {
        // Given: 准备测试数据
        CourseCreateRequest request = new CourseCreateRequest();
        request.setName("测试课程");
        request.setTypeId(1L);
        request.setStatus(CourseStatus.SUSPENDED); // 前端传入暂停状态
        request.setUnitHours(new BigDecimal("2.0"));
        request.setPrice(new BigDecimal("100.0"));
        request.setCoachFee(new BigDecimal("50.0"));
        request.setCoachIds(Arrays.asList(1L, 2L));
        request.setCampusId(1L);
        request.setDescription("测试课程描述");

        // Mock教练验证
        doNothing().when(sysCoachModel).validateCoach(anyLong(), anyLong(), anyLong());

        // Mock课程创建
        when(courseModel.createCourse(
            eq("测试课程"),
            eq(1L),
            eq(CourseStatus.SUSPENDED), // 验证传入的是前端的状态
            eq(new BigDecimal("2.0")),
            eq(BigDecimal.ZERO),
            eq(new BigDecimal("100.0")),
            eq(new BigDecimal("50.0")),
            eq(1L),
            eq(1L),
            eq("测试课程描述")
        )).thenReturn(100L);

        // Mock教练关联创建
        doNothing().when(courseModel).createCourseCoachRelation(anyLong(), anyLong());

        // When: 执行被测试方法
        Long courseId = courseService.createCourse(request);

        // Then: 验证结果
        assertEquals(100L, courseId);
        
        // 验证课程创建时使用了前端传入的状态
        verify(courseModel).createCourse(
            eq("测试课程"),
            eq(1L),
            eq(CourseStatus.SUSPENDED), // 验证传入的是SUSPENDED状态
            eq(new BigDecimal("2.0")),
            eq(BigDecimal.ZERO),
            eq(new BigDecimal("100.0")),
            eq(new BigDecimal("50.0")),
            eq(1L),
            eq(1L),
            eq("测试课程描述")
        );

        // 验证教练关联被创建
        verify(courseModel).createCourseCoachRelation(100L, 1L);
        verify(courseModel).createCourseCoachRelation(100L, 2L);
    }

    @Test
    @DisplayName("测试创建课程 - 使用已发布状态")
    void testCreateCourse_UsePublishedStatus() {
        // Given: 准备测试数据
        CourseCreateRequest request = new CourseCreateRequest();
        request.setName("已发布课程");
        request.setTypeId(1L);
        request.setStatus(CourseStatus.PUBLISHED); // 前端传入已发布状态
        request.setUnitHours(new BigDecimal("1.5"));
        request.setPrice(new BigDecimal("200.0"));
        request.setCoachFee(new BigDecimal("80.0"));
        request.setCoachIds(Arrays.asList(3L));
        request.setCampusId(1L);

        // Mock教练验证
        doNothing().when(sysCoachModel).validateCoach(anyLong(), anyLong(), anyLong());

        // Mock课程创建
        when(courseModel.createCourse(
            anyString(),
            anyLong(),
            eq(CourseStatus.PUBLISHED), // 验证传入的是PUBLISHED状态
            any(BigDecimal.class),
            any(BigDecimal.class),
            any(BigDecimal.class),
            any(BigDecimal.class),
            anyLong(),
            anyLong(),
            anyString()
        )).thenReturn(101L);

        // Mock教练关联创建
        doNothing().when(courseModel).createCourseCoachRelation(anyLong(), anyLong());

        // When: 执行被测试方法
        Long courseId = courseService.createCourse(request);

        // Then: 验证结果
        assertEquals(101L, courseId);
        
        // 验证课程创建时使用了前端传入的状态
        verify(courseModel).createCourse(
            eq("已发布课程"),
            eq(1L),
            eq(CourseStatus.PUBLISHED), // 验证传入的是PUBLISHED状态
            eq(new BigDecimal("1.5")),
            eq(BigDecimal.ZERO),
            eq(new BigDecimal("200.0")),
            eq(new BigDecimal("80.0")),
            eq(1L),
            eq(1L),
            eq("")
        );
    }

    @Test
    @DisplayName("测试更新课程 - 状态更新")
    void testUpdateCourse_StatusUpdate() {
        // Given: 准备测试数据
        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setId(100L);
        request.setName("更新后的课程");
        request.setTypeId(1L);
        request.setStatus(CourseStatus.SUSPENDED); // 前端传入暂停状态
        request.setUnitHours(new BigDecimal("2.0"));
        request.setPrice(new BigDecimal("150.0"));
        request.setCoachFee(new BigDecimal("60.0"));
        request.setCoachIds(Arrays.asList(1L));
        request.setCampusId(1L);
        request.setDescription("更新后的描述");

        // Mock现有课程信息
        com.lesson.model.record.CourseDetailRecord existingCourse = new com.lesson.model.record.CourseDetailRecord();
        existingCourse.setId(100L);
        existingCourse.setName("原课程");
        existingCourse.setCampusId(1L);
        existingCourse.setTotalHours(new BigDecimal("10.0"));
        when(courseModel.getCourseById(100L)).thenReturn(existingCourse);

        // Mock教练验证
        doNothing().when(sysCoachModel).validateCoach(anyLong(), anyLong(), anyLong());

        // Mock课程更新
        doNothing().when(courseModel).updateCourse(
            eq(100L),
            eq("更新后的课程"),
            eq(1L),
            eq(CourseStatus.SUSPENDED), // 验证传入的是SUSPENDED状态
            eq(new BigDecimal("2.0")),
            eq(new BigDecimal("10.0")),
            eq(new BigDecimal("150.0")),
            eq(new BigDecimal("60.0")),
            eq(1L),
            eq("更新后的描述")
        );

        // Mock教练关联更新
        doNothing().when(courseModel).deleteCourseCoachRelations(anyLong());
        doNothing().when(courseModel).createCourseCoachRelation(anyLong(), anyLong());

        // When: 执行被测试方法
        courseService.updateCourse(request);

        // Then: 验证结果
        // 验证课程更新时使用了前端传入的状态
        verify(courseModel).updateCourse(
            eq(100L),
            eq("更新后的课程"),
            eq(1L),
            eq(CourseStatus.SUSPENDED), // 验证传入的是SUSPENDED状态
            eq(new BigDecimal("2.0")),
            eq(new BigDecimal("10.0")),
            eq(new BigDecimal("150.0")),
            eq(new BigDecimal("60.0")),
            eq(1L),
            eq("更新后的描述")
        );
    }

    @Test
    @DisplayName("测试CourseStatus枚举 - 从字符串转换")
    void testCourseStatus_FromString() {
        // Given & When & Then: 测试从字符串转换
        assertEquals(CourseStatus.PUBLISHED, CourseStatus.fromString("PUBLISHED"));
        assertEquals(CourseStatus.SUSPENDED, CourseStatus.fromString("SUSPENDED"));
        assertEquals(CourseStatus.TERMINATED, CourseStatus.fromString("TERMINATED"));
        
        // 测试无效状态
        assertThrows(IllegalArgumentException.class, () -> {
            CourseStatus.fromString("INVALID_STATUS");
        });
        
        // 测试null值
        assertNull(CourseStatus.fromString(null));
    }

    @Test
    @DisplayName("测试CourseStatus枚举 - 从描述转换")
    void testCourseStatus_FromDescription() {
        // Given & When & Then: 测试从描述转换
        assertEquals(CourseStatus.PUBLISHED, CourseStatus.fromDescription("已发布"));
        assertEquals(CourseStatus.SUSPENDED, CourseStatus.fromDescription("已暂停"));
        assertEquals(CourseStatus.TERMINATED, CourseStatus.fromDescription("已终止"));
        
        // 测试无效描述
        assertThrows(IllegalArgumentException.class, () -> {
            CourseStatus.fromDescription("无效状态");
        });
    }
} 