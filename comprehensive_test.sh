#!/bin/bash

# 全面后端功能测试脚本
# 测试目标：两个校区，不同管理员和用户，教练3个，学员10个，课程2个

BASE_URL="http://localhost:8080/lesson"
LOG_FILE="comprehensive_test_results.log"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 初始化日志文件
echo "=== 全面后端功能测试报告 ===" > $LOG_FILE
echo "测试时间: $(date)" >> $LOG_FILE
echo "==========================================" >> $LOG_FILE

# 测试计数器
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 测试函数
test_api() {
    local test_name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local expected_status="$5"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    echo -e "${BLUE}测试: $test_name${NC}"
    echo "测试: $test_name" >> $LOG_FILE
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL$endpoint" -H "Authorization: $TOKEN" -H "Content-Type: application/json")
    else
        response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" -H "Authorization: $TOKEN" -H "Content-Type: application/json" -d "$data")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | sed '$d')
    
    echo "请求: $method $endpoint" >> $LOG_FILE
    echo "数据: $data" >> $LOG_FILE
    echo "响应码: $http_code" >> $LOG_FILE
    echo "响应体: $response_body" >> $LOG_FILE
    
    # 检查响应体是否包含错误信息
    if [ "$http_code" = "$expected_status" ] && [[ ! "$response_body" =~ "code\":500" ]]; then
        echo -e "${GREEN}✓ 通过${NC}"
        echo "结果: 通过" >> $LOG_FILE
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗ 失败 (期望: $expected_status, 实际: $http_code)${NC}"
        echo "结果: 失败 (期望: $expected_status, 实际: $http_code)" >> $LOG_FILE
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
    
    echo "----------------------------------------" >> $LOG_FILE
    echo ""
}

# 等待服务启动
echo "等待服务启动..."
sleep 5

# 检查服务健康状态
echo "检查服务健康状态..."
health_response=$(curl -s http://localhost:8080/lesson/actuator/health)
if [[ $health_response == *"UP"* ]]; then
    echo -e "${GREEN}服务已启动${NC}"
else
    echo -e "${RED}服务未启动，请检查${NC}"
    exit 1
fi

echo "=========================================="
echo "开始全面后端功能测试"
echo "=========================================="

# ================================
# 1. 用户认证测试
# ================================
echo -e "${YELLOW}1. 用户认证测试${NC}"

# 使用现有测试账号登录
echo "使用测试账号登录..."
login_data='{
    "phone": "15811384776",
    "password": "12345678"
}'
login_response=$(curl -s -X POST "$BASE_URL/api/auth/login" -H "Content-Type: application/json" -d "$login_data")
TOKEN=$(echo $login_response | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}登录失败，无法获取token${NC}"
    echo "登录响应: $login_response" >> $LOG_FILE
    exit 1
fi

echo -e "${GREEN}获取到token: ${TOKEN:0:20}...${NC}"

# ================================
# 2. 校区管理测试
# ================================
echo -e "${YELLOW}2. 校区管理测试${NC}"

# 创建校区1
campus1_data='{
    "name": "北京朝阳校区",
    "address": "北京市朝阳区建国路88号",
    "phone": "010-12345678",
    "description": "朝阳区主要校区"
}'
test_api "创建校区1" "POST" "/api/campus/create" "$campus1_data" "200"

# 创建校区2
campus2_data='{
    "name": "北京海淀校区",
    "address": "北京市海淀区中关村大街1号",
    "phone": "010-87654321",
    "description": "海淀区主要校区"
}'
test_api "创建校区2" "POST" "/api/campus/create" "$campus2_data" "200"

# 查询校区列表
test_api "查询校区列表" "GET" "/api/campus/list" "" "200"

# ================================
# 3. 用户管理测试
# ================================
echo -e "${YELLOW}3. 用户管理测试${NC}"

# 创建校区1管理员
campus1_admin_data='{
    "phone": "campus1_admin",
    "password": "admin123",
    "name": "朝阳校区管理员",
    "role": "ADMIN",
    "campusId": 1
}'
test_api "创建校区1管理员" "POST" "/api/user/create" "$campus1_admin_data" "200"

# 创建校区2管理员
campus2_admin_data='{
    "phone": "campus2_admin",
    "password": "admin123",
    "name": "海淀校区管理员",
    "role": "ADMIN",
    "campusId": 2
}'
test_api "创建校区2管理员" "POST" "/api/user/create" "$campus2_admin_data" "200"

# 创建普通用户1
user1_data='{
    "phone": "user001",
    "password": "user123",
    "name": "普通用户1",
    "role": "USER",
    "campusId": 1
}'
test_api "创建普通用户1" "POST" "/api/user/create" "$user1_data" "200"

# 创建普通用户2
user2_data='{
    "phone": "user002",
    "password": "user123",
    "name": "普通用户2",
    "role": "USER",
    "campusId": 2
}'
test_api "创建普通用户2" "POST" "/api/user/create" "$user2_data" "200"

# 查询用户列表
test_api "查询用户列表" "GET" "/api/user/list" "" "200"

# ================================
# 4. 课程管理测试
# ================================
echo -e "${YELLOW}4. 课程管理测试${NC}"

# 创建课程1 - 舞蹈课
course1_data='{
    "name": "少儿舞蹈基础班",
    "typeId": 1,
    "unitHours": 1.0,
    "price": 2000,
    "coachFee": 200,
    "coachIds": [1],
    "campusId": 1,
    "description": "适合6-12岁儿童的舞蹈基础课程"
}'
test_api "创建课程1" "POST" "/api/courses/create" "$course1_data" "200"

# 创建课程2 - 音乐课
course2_data='{
    "name": "钢琴启蒙班",
    "typeId": 2,
    "unitHours": 1.0,
    "price": 3000,
    "coachFee": 300,
    "coachIds": [2],
    "campusId": 2,
    "description": "适合5-10岁儿童的钢琴启蒙课程"
}'
test_api "创建课程2" "POST" "/api/courses/create" "$course2_data" "200"

# 查询课程列表
test_api "查询课程列表" "GET" "/api/courses/list" "" "200"

# ================================
# 5. 教练管理测试
# ================================
echo -e "${YELLOW}5. 教练管理测试${NC}"

# 创建教练1
coach1_data='{
    "name": "李教练",
    "gender": "女",
    "phone": "13800138001",
    "specialty": "舞蹈",
    "experience": "5年",
    "campusId": 1,
    "salary": 8000
}'
test_api "创建教练1" "POST" "/api/coach/create" "$coach1_data" "200"

# 创建教练2
coach2_data='{
    "name": "王教练",
    "gender": "男",
    "phone": "13800138002",
    "specialty": "音乐",
    "experience": "8年",
    "campusId": 2,
    "salary": 10000
}'
test_api "创建教练2" "POST" "/api/coach/create" "$coach2_data" "200"

# 创建教练3
coach3_data='{
    "name": "张教练",
    "gender": "女",
    "phone": "13800138003",
    "specialty": "舞蹈",
    "experience": "3年",
    "campusId": 1,
    "salary": 6000
}'
test_api "创建教练3" "POST" "/api/coach/create" "$coach3_data" "200"

# 查询教练列表
test_api "查询教练列表" "GET" "/api/coach/list" "" "200"

# ================================
# 6. 固定课表管理测试
# ================================
echo -e "${YELLOW}6. 固定课表管理测试${NC}"

# 查询固定课表列表
test_api "查询固定课表列表" "GET" "/api/fixed-schedule/list?campusId=1" "" "200"

# ================================
# 7. 学员管理测试
# ================================
echo -e "${YELLOW}7. 学员管理测试${NC}"

# 创建学员1-5 (校区1)
for i in {1..5}; do
    student_data="{
        \"studentInfo\": {
            \"name\": \"学员${i}\",
            \"gender\": \"男\",
            \"age\": $((6 + i)),
            \"phone\": \"13800138${i}00\",
            \"campusId\": 1
        },
        \"courseInfoList\": [
            {
                \"courseId\": 1,
                \"enrollDate\": \"2024-01-15\",
                \"status\": \"STUDYING\",
                \"fixedScheduleTimes\": [
                    {
                        \"weekday\": \"周一\",
                        \"from\": \"15:00\",
                        \"to\": \"16:00\"
                    }
                ]
            }
        ]
    }"
    test_api "创建学员${i}" "POST" "/api/student/create" "$student_data" "200"
done

# 创建学员6-10 (校区2)
for i in {6..10}; do
    student_data="{
        \"studentInfo\": {
            \"name\": \"学员${i}\",
            \"gender\": \"女\",
            \"age\": $((5 + i)),
            \"phone\": \"13800138${i}00\",
            \"campusId\": 2
        },
        \"courseInfoList\": [
            {
                \"courseId\": 2,
                \"enrollDate\": \"2024-01-20\",
                \"status\": \"STUDYING\",
                \"fixedScheduleTimes\": [
                    {
                        \"weekday\": \"周二\",
                        \"from\": \"14:00\",
                        \"to\": \"15:00\"
                    }
                ]
            }
        ]
    }"
    test_api "创建学员${i}" "POST" "/api/student/create" "$student_data" "200"
done

# 查询学员列表
test_api "查询学员列表" "POST" "/api/student/list" '{"pageNum": 1, "pageSize": 10}' "200"

# ================================
# 8. 学员考勤测试
# ================================
echo -e "${YELLOW}8. 学员考勤测试${NC}"

# 学员签到
checkin_data='{
    "studentId": 1,
    "courseId": 1,
    "checkInTime": "2024-01-15 15:00:00"
}'
test_api "学员签到" "POST" "/api/student/check-in" "$checkin_data" "200"

# 学员请假
leave_data='{
    "studentId": 2,
    "courseId": 1,
    "leaveDate": "2024-01-16",
    "reason": "身体不适"
}'
test_api "学员请假" "POST" "/api/student/leave" "$leave_data" "200"

# 查询考勤记录
attendance_data='{
    "studentId": 1,
    "courseId": 1,
    "pageNum": 1,
    "pageSize": 10
}'
test_api "查询考勤记录" "POST" "/api/student/attendance-list" "$attendance_data" "200"

# ================================
# 9. 缴费管理测试
# ================================
echo -e "${YELLOW}9. 缴费管理测试${NC}"

# 学员缴费
payment_data='{
    "studentId": 1,
    "courseId": 1,
    "amount": 2000,
    "paymentMethod": "现金",
    "paymentDate": "2024-01-15"
}'
test_api "学员缴费" "POST" "/api/student/payment" "$payment_data" "200"

# 查询缴费课时
test_api "查询缴费课时" "GET" "/api/student/payment-hours?studentId=1&courseId=1" "" "200"

# 学员退费
refund_data='{
    "studentId": 1,
    "courseId": 1,
    "refundAmount": 500,
    "refundReason": "课程调整"
}'
test_api "学员退费" "POST" "/api/student/refund" "$refund_data" "200"

# ================================
# 10. 课程调课测试
# ================================
echo -e "${YELLOW}10. 课程调课测试${NC}"

# 学员转课
transfer_data='{
    "studentId": 1,
    "fromCourseId": 1,
    "toCourseId": 2,
    "transferDate": "2024-01-20",
    "reason": "兴趣调整"
}'
test_api "学员转课" "POST" "/api/student/transfer-course" "$transfer_data" "200"

# 同课程内调课
within_transfer_data='{
    "studentId": 2,
    "courseId": 1,
    "fromScheduleId": 1,
    "toScheduleId": 2,
    "transferDate": "2024-01-25",
    "reason": "时间冲突"
}'
test_api "同课程内调课" "POST" "/api/student/transfer-within-course" "$within_transfer_data" "200"

# ================================
# 11. 统计功能测试
# ================================
echo -e "${YELLOW}11. 统计功能测试${NC}"

# 学员统计
student_stats_data='{
    "campusId": 1,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
}'
test_api "学员统计" "POST" "/api/statistics/student/metrics" "$student_stats_data" "200"

# 课程统计
course_stats_data='{
    "campusId": 1,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
}'
test_api "课程统计" "POST" "/api/statistics/course/metrics" "$course_stats_data" "200"

# 教练统计
coach_stats_data='{
    "campusId": 1,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
}'
test_api "教练统计" "POST" "/api/statistics/coach/metrics" "$coach_stats_data" "200"

# 财务统计
finance_stats_data='{
    "campusId": 1,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
}'
test_api "财务统计" "POST" "/api/statistics/finance/metrics" "$finance_stats_data" "200"

# ================================
# 12. 系统管理测试
# ================================
echo -e "${YELLOW}12. 系统管理测试${NC}"

# 查询角色列表
test_api "查询角色列表" "GET" "/api/user/roles" "" "200"

# 更新用户状态
update_status_data='{
    "status": "ACTIVE"
}'
test_api "更新用户状态" "POST" "/api/user/updateStatus?userId=1" "$update_status_data" "200"

# 刷新统计数据
test_api "刷新统计数据" "POST" "/api/statistics/refresh-stats?campusId=1" "" "200"

# ================================
# 测试结果汇总
# ================================
echo "=========================================="
echo -e "${YELLOW}测试结果汇总${NC}"
echo "=========================================="

echo "总测试数: $TOTAL_TESTS" >> $LOG_FILE
echo "通过测试: $PASSED_TESTS" >> $LOG_FILE
echo "失败测试: $FAILED_TESTS" >> $LOG_FILE
echo "成功率: $((PASSED_TESTS * 100 / TOTAL_TESTS))%" >> $LOG_FILE

echo -e "${GREEN}总测试数: $TOTAL_TESTS${NC}"
echo -e "${GREEN}通过测试: $PASSED_TESTS${NC}"
echo -e "${RED}失败测试: $FAILED_TESTS${NC}"
echo -e "${BLUE}成功率: $((PASSED_TESTS * 100 / TOTAL_TESTS))%${NC}"

echo "详细测试结果已保存到: $LOG_FILE"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}所有测试通过！${NC}"
    exit 0
else
    echo -e "${RED}有 $FAILED_TESTS 个测试失败，请查看日志文件${NC}"
    exit 1
fi 