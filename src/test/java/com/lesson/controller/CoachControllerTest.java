package com.lesson.controller;

import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import com.lesson.request.coach.CoachCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CoachControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateCoach() throws Exception {
        // 创建测试数据
        CoachCreateRequest request = new CoachCreateRequest();
        request.setName("测试教练");
        request.setGender(Gender.MALE);
        request.setAge(28);
        request.setPhone("13800138000");
        request.setAvatar("https://example.com/avatar.jpg");
        request.setJobTitle("高级教练");
        request.setHireDate(LocalDate.now());
        request.setExperience(5);
        request.setCertifications(Arrays.asList("健身教练证", "急救证"));
        request.setStatus(CoachStatus.ACTIVE);
        request.setCampusId(1L);
        request.setBaseSalary(new BigDecimal("5000"));
        request.setSocialInsurance(new BigDecimal("1000"));
        request.setClassFee(new BigDecimal("200"));
        request.setPerformanceBonus(new BigDecimal("1000"));
        request.setCommission(new BigDecimal("5"));
        request.setDividend(new BigDecimal("2000"));

        // 执行测试
        mockMvc.perform(post("/api/coaches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
} 