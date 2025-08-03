-- 为学员表添加来源ID字段
ALTER TABLE edu_student ADD COLUMN source_id BIGINT COMMENT '学员来源ID（关联sys_constant表）' AFTER institution_id; 