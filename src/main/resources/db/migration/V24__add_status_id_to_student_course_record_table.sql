-- 修改学员课程记录表的status_id字段默认值
ALTER TABLE edu_student_course_record MODIFY COLUMN status_id BIGINT NOT NULL DEFAULT 1 COMMENT '状态ID：1-正常，2-异常'; 