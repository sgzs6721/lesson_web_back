-- 修改学员课程表的 end_date 字段为可空
ALTER TABLE `edu_student_course` 
MODIFY COLUMN `end_date` date NULL DEFAULT NULL COMMENT '有效期至'; 