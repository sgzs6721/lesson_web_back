package com.lesson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@ComponentScan(basePackages = {
    "com.lesson.controller",
    "com.lesson.service",
    "com.lesson.model",
    "com.lesson.common",
    "com.lesson.config",
    "com.lesson.interceptor",
    "com.lesson.utils",
    "com.lesson.schedule"
})
public class LessonApplication {
    public static void main(String[] args) {
        SpringApplication.run(LessonApplication.class, args);
    }
} 