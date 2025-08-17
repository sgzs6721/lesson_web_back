-- 检查当前数据库表结构
USE lesson_prod;

-- 检查 edu_student_course 表结构
DESCRIBE edu_student_course;

-- 检查 edu_student_payment 表结构  
DESCRIBE edu_student_payment;

-- 检查是否已存在 validity_period_id 字段
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'lesson_prod' 
    AND TABLE_NAME IN ('edu_student_course', 'edu_student_payment')
    AND COLUMN_NAME = 'validity_period_id'; 