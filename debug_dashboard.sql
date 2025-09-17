-- 检查今日打卡记录数据
SELECT 
    '今日打卡记录统计' as description,
    COUNT(DISTINCT coach_id) as teacher_count,
    COUNT(DISTINCT course_id) as class_count,
    COUNT(DISTINCT student_id) as student_count,
    COUNT(*) as checkin_count,
    COALESCE(SUM(hours), 0) as consumed_hours,
    SUM(CASE WHEN status = 'LEAVE' THEN 1 ELSE 0 END) as leave_count,
    COALESCE(SUM(hours * 100), 0) as teacher_remuneration
FROM edu_student_course_record 
WHERE course_date = CURDATE() 
AND deleted = 0;

-- 检查最近几天的数据
SELECT 
    '最近几天数据' as description,
    course_date,
    COUNT(*) as record_count,
    COUNT(DISTINCT student_id) as student_count,
    COUNT(DISTINCT coach_id) as coach_count
FROM edu_student_course_record 
WHERE course_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
AND deleted = 0
GROUP BY course_date
ORDER BY course_date DESC;

-- 检查课程表数据
SELECT 
    '课程数据' as description,
    id, name, price 
FROM edu_course 
WHERE deleted = 0 
LIMIT 5;

-- 检查学员表数据
SELECT 
    '学员数据' as description,
    id, name 
FROM edu_student 
WHERE deleted = 0 
LIMIT 5;

-- 检查教练表数据
SELECT 
    '教练数据' as description,
    id, name 
FROM sys_coach 
WHERE deleted = 0 
LIMIT 5;

-- 检查学员课程记录表的数据
SELECT 
    '学员课程记录' as description,
    id, student_id, course_id, coach_id, course_date, status, hours
FROM edu_student_course_record 
WHERE course_date >= DATE_SUB(CURDATE(), INTERVAL 3 DAY)
AND deleted = 0
ORDER BY course_date DESC
LIMIT 10;
