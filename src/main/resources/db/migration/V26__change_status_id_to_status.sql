-- 1. 新增status字段
ALTER TABLE edu_student_course_record ADD COLUMN status VARCHAR(20) DEFAULT NULL COMMENT '出勤状态（枚举值：NORMAL/LEAVE/ABSENT）';

-- 2. 数据迁移（假设1=正常，2=请假，3=缺席，如有不同请手动调整）
UPDATE edu_student_course_record SET status = 'NORMAL' WHERE status_id = 1;
UPDATE edu_student_course_record SET status = 'LEAVE' WHERE status_id = 2;
UPDATE edu_student_course_record SET status = 'ABSENT' WHERE status_id = 3;

-- 3. 设置status为非空
ALTER TABLE edu_student_course_record MODIFY COLUMN status VARCHAR(20) NOT NULL COMMENT '出勤状态（枚举值：NORMAL/LEAVE/ABSENT）';

-- 4. 删除status_id字段
ALTER TABLE edu_student_course_record DROP COLUMN status_id; 