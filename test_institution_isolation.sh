#!/bin/bash

# 机构隔离测试脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 基础URL
BASE_URL="http://localhost:8080/lesson"

# 日志文件
LOG_FILE="institution_isolation_test.log"

# 计数器
PASSED_TESTS=0
FAILED_TESTS=0

echo "==========================================" > $LOG_FILE
echo "机构隔离测试报告" >> $LOG_FILE
echo "测试时间: $(date)" >> $LOG_FILE
echo "==========================================" >> $LOG_FILE

# 测试函数
test_api() {
    local test_name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local expected_status="$5"
    local token="$6"
    
    echo -e "${YELLOW}测试: $test_name${NC}"
    echo "测试: $test_name" >> $LOG_FILE
    
    # 执行请求
    if [ -n "$data" ]; then
        if [ -n "$token" ]; then
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -H "Content-Type: application/json" -d "$data")
        fi
    else
        if [ -n "$token" ]; then
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -H "Authorization: Bearer $token")
        else
            response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint")
        fi
    fi
    
    # 分离响应体和状态码
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    echo "请求: $method $BASE_URL$endpoint" >> $LOG_FILE
    if [ -n "$data" ]; then
        echo "请求数据: $data" >> $LOG_FILE
    fi
    if [ -n "$token" ]; then
        echo "Token: ${token:0:20}..." >> $LOG_FILE
    fi
    echo "响应状态: $http_code" >> $LOG_FILE
    echo "响应内容: $response_body" >> $LOG_FILE
    
    # 检查结果
    if [ "$http_code" -eq "$expected_status" ]; then
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
echo "开始机构隔离测试"
echo "=========================================="

# ================================
# 1. 注册第一个机构
# ================================
echo -e "${YELLOW}1. 注册第一个机构${NC}"

institution1_data='{
    "name": "北京朝阳校区（已更新）",
    "contactName": "朝阳校区管理员",
    "username": "chaoyang_admin",
    "password": "admin123456"
}'
test_api "注册第一个机构" "POST" "/api/institution/register" "$institution1_data" "200"

# 获取第一个机构的响应，提取机构ID
institution1_response=$(curl -s -X POST "$BASE_URL/api/institution/register" -H "Content-Type: application/json" -d "$institution1_data")
institution1_id=$(echo $institution1_response | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "第一个机构ID: $institution1_id"

# ================================
# 2. 注册第二个机构
# ================================
echo -e "${YELLOW}2. 注册第二个机构${NC}"

institution2_data='{
    "name": "北京海淀校区",
    "contactName": "海淀校区管理员",
    "username": "haidian_admin",
    "password": "admin123456"
}'
test_api "注册第二个机构" "POST" "/api/institution/register" "$institution2_data" "200"

# 获取第二个机构的响应，提取机构ID
institution2_response=$(curl -s -X POST "$BASE_URL/api/institution/register" -H "Content-Type: application/json" -d "$institution2_data")
institution2_id=$(echo $institution2_response | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "第二个机构ID: $institution2_id"

# ================================
# 3. 第一个机构管理员登录
# ================================
echo -e "${YELLOW}3. 第一个机构管理员登录${NC}"

login1_data='{
    "phone": "chaoyang_admin",
    "password": "admin123456"
}'
test_api "第一个机构管理员登录" "POST" "/api/auth/login" "$login1_data" "200"

# 获取第一个机构的token
login1_response=$(curl -s -X POST "$BASE_URL/api/auth/login" -H "Content-Type: application/json" -d "$login1_data")
token1=$(echo $login1_response | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "第一个机构Token: ${token1:0:20}..."

# ================================
# 4. 第二个机构管理员登录
# ================================
echo -e "${YELLOW}4. 第二个机构管理员登录${NC}"

login2_data='{
    "phone": "haidian_admin",
    "password": "admin123456"
}'
test_api "第二个机构管理员登录" "POST" "/api/auth/login" "$login2_data" "200"

# 获取第二个机构的token
login2_response=$(curl -s -X POST "$BASE_URL/api/auth/login" -H "Content-Type: application/json" -d "$login2_data")
token2=$(echo $login2_response | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "第二个机构Token: ${token2:0:20}..."

# ================================
# 5. 为第一个机构创建校区
# ================================
echo -e "${YELLOW}5. 为第一个机构创建校区${NC}"

campus1_data='{
    "name": "朝阳校区A",
    "address": "北京市朝阳区建国路88号",
    "phone": "010-12345678",
    "description": "朝阳区主要校区"
}'
test_api "为第一个机构创建校区" "POST" "/api/campus/create" "$campus1_data" "200" "$token1"

# ================================
# 6. 为第二个机构创建校区
# ================================
echo -e "${YELLOW}6. 为第二个机构创建校区${NC}"

campus2_data='{
    "name": "海淀校区B",
    "address": "北京市海淀区中关村大街1号",
    "phone": "010-87654321",
    "description": "海淀区主要校区"
}'
test_api "为第二个机构创建校区" "POST" "/api/campus/create" "$campus2_data" "200" "$token2"

# ================================
# 7. 测试机构隔离 - 第一个机构查看校区列表
# ================================
echo -e "${YELLOW}7. 测试机构隔离 - 第一个机构查看校区列表${NC}"

test_api "第一个机构查看校区列表" "GET" "/api/campus/list" "" "200" "$token1"

# 检查第一个机构的校区列表响应
campus_list1_response=$(curl -s -X GET "$BASE_URL/api/campus/list" -H "Authorization: Bearer $token1")
echo "第一个机构校区列表: $campus_list1_response" >> $LOG_FILE

# ================================
# 8. 测试机构隔离 - 第二个机构查看校区列表
# ================================
echo -e "${YELLOW}8. 测试机构隔离 - 第二个机构查看校区列表${NC}"

test_api "第二个机构查看校区列表" "GET" "/api/campus/list" "" "200" "$token2"

# 检查第二个机构的校区列表响应
campus_list2_response=$(curl -s -X GET "$BASE_URL/api/campus/list" -H "Authorization: Bearer $token2")
echo "第二个机构校区列表: $campus_list2_response" >> $LOG_FILE

# ================================
# 9. 验证机构隔离
# ================================
echo -e "${YELLOW}9. 验证机构隔离${NC}"

# 检查第一个机构的校区列表是否只包含自己的校区
if [[ $campus_list1_response == *"朝阳校区A"* ]] && [[ $campus_list1_response != *"海淀校区B"* ]]; then
    echo -e "${GREEN}✓ 第一个机构隔离正确${NC}"
    echo "第一个机构隔离正确" >> $LOG_FILE
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ 第一个机构隔离失败${NC}"
    echo "第一个机构隔离失败" >> $LOG_FILE
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# 检查第二个机构的校区列表是否只包含自己的校区
if [[ $campus_list2_response == *"海淀校区B"* ]] && [[ $campus_list2_response != *"朝阳校区A"* ]]; then
    echo -e "${GREEN}✓ 第二个机构隔离正确${NC}"
    echo "第二个机构隔离正确" >> $LOG_FILE
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ 第二个机构隔离失败${NC}"
    echo "第二个机构隔离失败" >> $LOG_FILE
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# ================================
# 测试结果汇总
# ================================
echo "=========================================="
echo "测试结果汇总"
echo "=========================================="
echo -e "${GREEN}通过测试: $PASSED_TESTS${NC}"
echo -e "${RED}失败测试: $FAILED_TESTS${NC}"
echo -e "${YELLOW}总测试数: $((PASSED_TESTS + FAILED_TESTS))${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}所有测试通过！机构隔离正常工作${NC}"
    echo "所有测试通过！机构隔离正常工作" >> $LOG_FILE
else
    echo -e "${RED}有 $FAILED_TESTS 个测试失败，机构隔离存在问题${NC}"
    echo "有 $FAILED_TESTS 个测试失败，机构隔离存在问题" >> $LOG_FILE
fi

echo "==========================================" >> $LOG_FILE
echo "测试结果汇总" >> $LOG_FILE
echo "通过测试: $PASSED_TESTS" >> $LOG_FILE
echo "失败测试: $FAILED_TESTS" >> $LOG_FILE
echo "总测试数: $((PASSED_TESTS + FAILED_TESTS))" >> $LOG_FILE

echo ""
echo -e "${YELLOW}机构账号信息:${NC}"
echo -e "${GREEN}第一个机构 - 用户名: chaoyang_admin, 密码: admin123456${NC}"
echo -e "${GREEN}第二个机构 - 用户名: haidian_admin, 密码: admin123456${NC}"

echo ""
echo "详细测试日志已保存到: $LOG_FILE" 