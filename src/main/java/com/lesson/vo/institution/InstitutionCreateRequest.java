package com.lesson.vo.institution;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 教育机构创建请求
 */
@Data
public class InstitutionCreateRequest {
    
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 100, message = "机构名称长度不能超过100个字符")
    private String name;
    
    /**
     * 机构代码
     */
    @NotBlank(message = "机构代码不能为空")
    @Size(max = 50, message = "机构代码长度不能超过50个字符")
    private String code;
    
    /**
     * 机构地址
     */
    @Size(max = 255, message = "机构地址长度不能超过255个字符")
    private String address;
    
    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;
    
    /**
     * 机构描述
     */
    @Size(max = 500, message = "机构描述长度不能超过500个字符")
    private String description;
} 