-- 添加教练费用字段到课程表
ALTER TABLE `edu_course` 
ADD COLUMN `coach_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '教练费用(元)' AFTER `price`; 