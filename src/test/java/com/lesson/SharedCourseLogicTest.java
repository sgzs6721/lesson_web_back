package com.lesson;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import static com.lesson.repository.Tables.EDU_COURSE_SHARING;
import static com.lesson.repository.Tables.EDU_STUDENT;
import static com.lesson.repository.Tables.EDU_COURSE;

@Component
public class SharedCourseLogicTest implements CommandLineRunner {

    @Autowired
    private DSLContext dsl;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 分析共享课程逻辑 ===");
        
        // 1. 查看共享课程表中的所有数据
        System.out.println("\n1. 共享课程表中的所有数据：");
        Result<Record> allSharings = dsl.select()
                .from(EDU_COURSE_SHARING)
                .where(EDU_COURSE_SHARING.DELETED.eq(0))
                .fetch();
        
        for (Record record : allSharings) {
            System.out.println(String.format(
                "ID: %s, 学员: %s, 源课程: %s, 目标课程: %s, 状态: %s, 共享课时: %s",
                record.get(EDU_COURSE_SHARING.ID),
                record.get(EDU_COURSE_SHARING.STUDENT_ID),
                record.get(EDU_COURSE_SHARING.SOURCE_COURSE_ID),
                record.get(EDU_COURSE_SHARING.TARGET_COURSE_ID),
                record.get(EDU_COURSE_SHARING.STATUS),
                record.get(EDU_COURSE_SHARING.SHARED_HOURS)
            ));
        }
        
        // 2. 分析一个具体的共享记录
        if (!allSharings.isEmpty()) {
            Record firstSharing = allSharings.get(0);
            Long sourceCourseId = firstSharing.get(EDU_COURSE_SHARING.SOURCE_COURSE_ID);
            Long targetCourseId = firstSharing.get(EDU_COURSE_SHARING.TARGET_COURSE_ID);
            Long studentId = firstSharing.get(EDU_COURSE_SHARING.STUDENT_ID);
            
            System.out.println("\n2. 分析第一个共享记录：");
            System.out.println("源课程ID: " + sourceCourseId);
            System.out.println("目标课程ID: " + targetCourseId);
            System.out.println("学员ID: " + studentId);
            
            // 查询源课程信息
            String sourceCourseName = dsl.select(EDU_COURSE.NAME)
                    .from(EDU_COURSE)
                    .where(EDU_COURSE.ID.eq(sourceCourseId))
                    .and(EDU_COURSE.DELETED.eq(0))
                    .fetchOneInto(String.class);
            
            // 查询目标课程信息
            String targetCourseName = dsl.select(EDU_COURSE.NAME)
                    .from(EDU_COURSE)
                    .where(EDU_COURSE.ID.eq(targetCourseId))
                    .and(EDU_COURSE.DELETED.eq(0))
                    .fetchOneInto(String.class);
            
            // 查询学员信息
            String studentName = dsl.select(EDU_STUDENT.NAME)
                    .from(EDU_STUDENT)
                    .where(EDU_STUDENT.ID.eq(studentId))
                    .and(EDU_STUDENT.DELETED.eq(0))
                    .fetchOneInto(String.class);
            
            System.out.println("源课程名称: " + sourceCourseName);
            System.out.println("目标课程名称: " + targetCourseName);
            System.out.println("学员姓名: " + studentName);
            
            System.out.println("\n3. 业务逻辑分析：");
            System.out.println("学员 " + studentName + " 在 " + sourceCourseName + " 课程中");
            System.out.println("共享了 " + firstSharing.get(EDU_COURSE_SHARING.SHARED_HOURS) + " 课时");
            System.out.println("到 " + targetCourseName + " 课程中");
            
            System.out.println("\n4. 查询逻辑分析：");
            System.out.println("如果要查询课程 " + targetCourseName + " 的共享信息：");
            System.out.println("应该查询 WHERE target_course_id = " + targetCourseId);
            System.out.println("如果要查询课程 " + sourceCourseName + " 的共享信息：");
            System.out.println("应该查询 WHERE source_course_id = " + sourceCourseId);
        }
        
        System.out.println("\n=== 分析完成 ===");
    }
}


