-- 检查今日打卡记录数据
SELECT 
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
SELECT id, name, price FROM edu_course WHERE deleted = 0 LIMIT 5;

-- 检查学员表数据
SELECT id, name FROM edu_student WHERE deleted = 0 LIMIT 5;

-- 检查教练表数据
SELECT id, name FROM sys_coach WHERE deleted = 0 LIMIT 5;
