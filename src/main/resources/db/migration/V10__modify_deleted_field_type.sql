-- 修改edu_course_record表的deleted字段类型
ALTER TABLE `edu_course_record` MODIFY COLUMN `deleted` int NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除';
