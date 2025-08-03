-- 添加学员课程记录表的status_id字段
ALTER TABLE edu_student_course_record ADD COLUMN status_id BIGINT NOT NULL DEFAULT 1 COMMENT '状态ID：1-正常，2-异常'; 