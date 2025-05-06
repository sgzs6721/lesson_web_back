package com.lesson.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校区管理系统接口文档")
                        .description("校区管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发团队")))
                .components(new Components()
                        .addResponses("200", createSuccessResponse())
                        .addResponses("401", createUnauthorizedResponse())
                        .addResponses("403", createForbiddenResponse())
                        .addResponses("500", createErrorResponse()));
    }
    
    private ApiResponse createSuccessResponse() {
        return new ApiResponse()
                .description("操作成功")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .addExamples("default", new Example()
                                        .value("{\"code\": 200, \"message\": \"操作成功\", \"data\": null}"))));
    }
    
    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
                .description("未授权")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .addExamples("default", new Example()
                                        .value("{\"code\": 401, \"message\": \"未登录或token已过期\", \"data\": null}"))));
    }
    
    private ApiResponse createForbiddenResponse() {
        return new ApiResponse()
                .description("权限不足")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .addExamples("default", new Example()
                                        .value("{\"code\": 403, \"message\": \"权限不足\", \"data\": null}"))));
    }
    
    private ApiResponse createErrorResponse() {
        return new ApiResponse()
                .description("系统异常")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .addExamples("default", new Example()
                                        .value("{\"code\": 500, \"message\": \"系统异常\", \"data\": null}"))));
    }
} 