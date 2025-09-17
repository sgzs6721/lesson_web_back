// 测试首页统计查询的调试代码
// 这个文件用于调试首页统计数据为0的问题

import java.time.LocalDate;
import java.math.BigDecimal;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.DSL;
import static com.lesson.repository.tables.Tables.*;

public class DashboardDebugTest {
    
    public static void testTodayStatsQuery(DSLContext dsl) {
        LocalDate today = LocalDate.now();
        
        System.out.println("=== 调试首页统计查询 ===");
        System.out.println("查询日期: " + today);
        
        // 1. 检查今日打卡记录数量
        Integer recordCount = dsl.select(DSL.count())
                .from(EDU_STUDENT_COURSE_RECORD)
                .where(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.eq(today))
                .and(EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
                .fetchOneInto(Integer.class);
        
        System.out.println("今日打卡记录数量: " + recordCount);
        
        // 2. 检查今日打卡记录详情
        var records = dsl.select(
                EDU_STUDENT_COURSE_RECORD.ID,
                EDU_STUDENT_COURSE_RECORD.STUDENT_ID,
                EDU_STUDENT_COURSE_RECORD.COURSE_ID,
                EDU_STUDENT_COURSE_RECORD.COACH_ID,
                EDU_STUDENT_COURSE_RECORD.COURSE_DATE,
                EDU_STUDENT_COURSE_RECORD.STATUS,
                EDU_STUDENT_COURSE_RECORD.HOURS
            )
            .from(EDU_STUDENT_COURSE_RECORD)
            .where(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.eq(today))
            .and(EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
            .fetch();
        
        System.out.println("今日打卡记录详情:");
        for (Record record : records) {
            System.out.println("  ID: " + record.get(EDU_STUDENT_COURSE_RECORD.ID) + 
                             ", 学员ID: " + record.get(EDU_STUDENT_COURSE_RECORD.STUDENT_ID) +
                             ", 课程ID: " + record.get(EDU_STUDENT_COURSE_RECORD.COURSE_ID) +
                             ", 教练ID: " + record.get(EDU_STUDENT_COURSE_RECORD.COACH_ID) +
                             ", 状态: " + record.get(EDU_STUDENT_COURSE_RECORD.STATUS) +
                             ", 课时: " + record.get(EDU_STUDENT_COURSE_RECORD.HOURS));
        }
        
        // 3. 检查课程表数据
        var courses = dsl.select(EDU_COURSE.ID, EDU_COURSE.NAME, EDU_COURSE.PRICE)
                .from(EDU_COURSE)
                .where(EDU_COURSE.DELETED.eq(0))
                .limit(5)
                .fetch();
        
        System.out.println("课程数据:");
        for (Record course : courses) {
            System.out.println("  课程ID: " + course.get(EDU_COURSE.ID) + 
                             ", 名称: " + course.get(EDU_COURSE.NAME) +
                             ", 价格: " + course.get(EDU_COURSE.PRICE));
        }
        
        // 4. 测试完整的统计查询
        Record todayStatsRecord = dsl.select(
                DSL.countDistinct(EDU_STUDENT_COURSE_RECORD.COACH_ID).as("teacher_count"),
                DSL.countDistinct(EDU_STUDENT_COURSE_RECORD.COURSE_ID).as("class_count"),
                DSL.countDistinct(EDU_STUDENT_COURSE_RECORD.STUDENT_ID).as("student_count"),
                DSL.count(EDU_STUDENT_COURSE_RECORD.ID).as("checkin_count"),
                DSL.coalesce(DSL.sum(EDU_STUDENT_COURSE_RECORD.HOURS), BigDecimal.ZERO).as("consumed_hours")
            )
            .from(EDU_STUDENT_COURSE_RECORD)
            .join(EDU_COURSE).on(EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(EDU_COURSE.ID))
            .where(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.eq(today))
            .and(EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
            .fetchOne();
        
        if (todayStatsRecord != null) {
            System.out.println("统计查询结果:");
            System.out.println("  教练数量: " + todayStatsRecord.get("teacher_count"));
            System.out.println("  班级数量: " + todayStatsRecord.get("class_count"));
            System.out.println("  学员数量: " + todayStatsRecord.get("student_count"));
            System.out.println("  打卡次数: " + todayStatsRecord.get("checkin_count"));
            System.out.println("  消耗课时: " + todayStatsRecord.get("consumed_hours"));
        } else {
            System.out.println("统计查询结果为空!");
        }
    }
}
