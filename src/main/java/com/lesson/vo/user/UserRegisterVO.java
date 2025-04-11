package com.lesson.vo.user;

import com.lesson.common.enums.InstitutionTypeEnum;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户注册响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterVO {
    
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    private String managerName;

    private String institutionName;

    private InstitutionTypeEnum institutionType=InstitutionTypeEnum.SPORTS;

    /**
     * 机构简介
     */
    private String institutionDescription;


    public static UserRegisterVO of(Long userId, String phone, String managerName, String institutionName, InstitutionTypeEnum institutionType, String institutionDescription) {
        return UserRegisterVO.builder()
                .userId(userId)
                .phone(phone)
            .managerName(managerName)
            .institutionName(institutionName)
            .institutionType(institutionType)
            .institutionDescription(institutionDescription)
                .build();
    }
} 