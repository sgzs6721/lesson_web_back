-- 添加校区负责人关联字段
ALTER TABLE edu_campus
ADD COLUMN manager_id BIGINT COMMENT '负责人ID'; 