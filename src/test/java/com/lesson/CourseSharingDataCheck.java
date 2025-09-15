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
public class CourseSharingDataCheck implements CommandLineRunner {

    @Autowired
    private DSLContext dsl;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 检查共享课程数据表 ===");
        
        // 查询总记录数
        Long totalCount = dsl.selectCount()
                .from(EDU_COURSE_SHARING)
                .where(EDU_COURSE_SHARING.DELETED.eq(0))
                .fetchOneInto(Long.class);
        
        System.out.println("共享课程总记录数: " + totalCount);
        
        if (totalCount > 0) {
            // 查询详细数据
            Result<?> records = dsl.select(
                    EDU_COURSE_SHARING.ID,
                    EDU_COURSE_SHARING.STUDENT_ID,
                    EDU_STUDENT.NAME.as("student_name"),
                    EDU_COURSE_SHARING.SOURCE_COURSE_ID,
                    EDU_COURSE.NAME.as("source_course_name"),
                    EDU_COURSE_SHARING.TARGET_COURSE_ID,
                    EDU_COURSE_SHARING.STATUS,
                    EDU_COURSE_SHARING.SHARED_HOURS,
                    EDU_COURSE_SHARING.START_DATE,
                    EDU_COURSE_SHARING.CAMPUS_ID,
                    EDU_COURSE_SHARING.INSTITUTION_ID,
                    EDU_COURSE_SHARING.CREATED_TIME
                )
                .from(EDU_COURSE_SHARING)
                .leftJoin(EDU_STUDENT).on(EDU_COURSE_SHARING.STUDENT_ID.eq(EDU_STUDENT.ID))
                .leftJoin(EDU_COURSE).on(EDU_COURSE_SHARING.SOURCE_COURSE_ID.eq(EDU_COURSE.ID))
                .where(EDU_COURSE_SHARING.DELETED.eq(0))
                .orderBy(EDU_COURSE_SHARING.CREATED_TIME.desc())
                .limit(10)
                .fetch();
            
            System.out.println("\n=== 前10条共享课程记录 ===");
            for (Record record : records) {
                System.out.println(String.format(
                    "ID: %s, 学员: %s(%s), 源课程: %s(%s), 目标课程: %s, 状态: %s, 共享课时: %s, 开始日期: %s, 校区: %s, 机构: %s, 创建时间: %s",
                    record.get(EDU_COURSE_SHARING.ID),
                    record.get("student_name"),
                    record.get(EDU_COURSE_SHARING.STUDENT_ID),
                    record.get("source_course_name"),
                    record.get(EDU_COURSE_SHARING.SOURCE_COURSE_ID),
                    record.get(EDU_COURSE_SHARING.TARGET_COURSE_ID),
                    record.get(EDU_COURSE_SHARING.STATUS),
                    record.get(EDU_COURSE_SHARING.SHARED_HOURS),
                    record.get(EDU_COURSE_SHARING.START_DATE),
                    record.get(EDU_COURSE_SHARING.CAMPUS_ID),
                    record.get(EDU_COURSE_SHARING.INSTITUTION_ID),
                    record.get(EDU_COURSE_SHARING.CREATED_TIME)
                ));
            }
        } else {
            System.out.println("共享课程数据表中没有数据");
        }
        
        // 检查学员课程关系表中的共享标识
        Long sharedStudentCourseCount = dsl.selectCount()
                .from(com.lesson.repository.Tables.EDU_STUDENT_COURSE)
                .where(com.lesson.repository.Tables.EDU_STUDENT_COURSE.IS_SHARED.eq(1))
                .and(com.lesson.repository.Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                .fetchOneInto(Long.class);
        
        System.out.println("\n学员课程关系表中标记为共享的记录数: " + sharedStudentCourseCount);
        
        System.out.println("=== 检查完成 ===");
    }
}
