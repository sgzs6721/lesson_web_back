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

import java.util.HashMap;
import java.util.Map;

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
                        .addSchemas("ResultSuccess", createResultSuccessSchema())
                        .addSchemas("ResultError", createResultErrorSchema())
                        .addResponses("200", createSuccessResponse())
                        .addResponses("401", createUnauthorizedResponse())
                        .addResponses("403", createForbiddenResponse())
                        .addResponses("500", createErrorResponse()));
    }
    
    private Schema<?> createResultSuccessSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("code", new Schema<>().type("integer").example(200))
                .addProperty("message", new Schema<>().type("string").example("操作成功"))
                .addProperty("data", new Schema<>().type("object"));
    }
    
    private Schema<?> createResultErrorSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("code", new Schema<>().type("integer").example(500))
                .addProperty("message", new Schema<>().type("string").example("系统异常"))
                .addProperty("data", new Schema<>().type("null"));
    }
    
    private ApiResponse createSuccessResponse() {
        Map<String, Object> example = new HashMap<>();
        example.put("code", 200);
        example.put("message", "操作成功");
        example.put("data", null);
        
        return new ApiResponse()
                .description("操作成功")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ResultSuccess"))
                                .addExamples("default", new Example()
                                        .value(example))));
    }
    
    private ApiResponse createUnauthorizedResponse() {
        Map<String, Object> example = new HashMap<>();
        example.put("code", 401);
        example.put("message", "未登录或token已过期");
        example.put("data", null);
        
        return new ApiResponse()
                .description("未授权")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .type("object")
                                        .addProperty("code", new Schema<>().type("integer").example(401))
                                        .addProperty("message", new Schema<>().type("string").example("未登录或token已过期"))
                                        .addProperty("data", new Schema<>().type("null")))
                                .addExamples("default", new Example()
                                        .value(example))));
    }
    
    private ApiResponse createForbiddenResponse() {
        Map<String, Object> example = new HashMap<>();
        example.put("code", 403);
        example.put("message", "权限不足");
        example.put("data", null);
        
        return new ApiResponse()
                .description("权限不足")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .type("object")
                                        .addProperty("code", new Schema<>().type("integer").example(403))
                                        .addProperty("message", new Schema<>().type("string").example("权限不足"))
                                        .addProperty("data", new Schema<>().type("null")))
                                .addExamples("default", new Example()
                                        .value(example))));
    }
    
    private ApiResponse createErrorResponse() {
        Map<String, Object> example = new HashMap<>();
        example.put("code", 500);
        example.put("message", "系统异常");
        example.put("data", null);
        
        return new ApiResponse()
                .description("系统异常")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ResultError"))
                                .addExamples("default", new Example()
                                        .value(example))));
    }
} 