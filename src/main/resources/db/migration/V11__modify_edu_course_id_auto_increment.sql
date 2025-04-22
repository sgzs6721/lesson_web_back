-- 修改edu_course表的id字段为自增的bigint类型
ALTER TABLE `edu_course` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '课程ID';

-- 注意：需要同步修改相关表中的course_id字段类型
ALTER TABLE `edu_course_record` MODIFY COLUMN `course_id` bigint(20) NOT NULL COMMENT '课程ID';
ALTER TABLE `edu_student_course` MODIFY COLUMN `course_id` bigint(20) NOT NULL COMMENT '课程ID';
ALTER TABLE `edu_student_course_record` MODIFY COLUMN `course_id` bigint(20) NOT NULL COMMENT '课程ID';
ALTER TABLE `edu_student_course_transfer` MODIFY COLUMN `original_course_id` bigint(20) NOT NULL COMMENT '原课程ID';
ALTER TABLE `edu_student_course_transfer` MODIFY COLUMN `target_course_id` bigint(20) NOT NULL COMMENT '目标课程ID';