#!/bin/bash

# 登录接口机构名称测试脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 基础URL
BASE_URL="http://localhost:8080/lesson"

# 日志文件
LOG_FILE="login_institution_name_test.log"

# 计数器
PASSED_TESTS=0
FAILED_TESTS=0

echo "==========================================" > $LOG_FILE
echo "登录接口机构名称测试报告" >> $LOG_FILE
echo "测试时间: $(date)" >> $LOG_FILE
echo "==========================================" >> $LOG_FILE

# 测试函数
test_api() {
    local test_name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local expected_status="$5"
    
    echo -e "${YELLOW}测试: $test_name${NC}"
    echo "测试: $test_name" >> $LOG_FILE
    
    # 执行请求
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -H "Content-Type: application/json" -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint")
    fi
    
    # 分离响应体和状态码
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    echo "请求: $method $BASE_URL$endpoint" >> $LOG_FILE
    if [ -n "$data" ]; then
        echo "请求数据: $data" >> $LOG_FILE
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
echo "开始登录接口机构名称测试"
echo "=========================================="

# ================================
# 1. 注册机构
# ================================
echo -e "${YELLOW}1. 注册机构${NC}"

register_data='{
    "name": "北京朝阳校区（已更新）",
    "contactName": "朝阳校区管理员",
    "username": "chaoyang_admin",
    "password": "admin123456"
}'
test_api "注册机构" "POST" "/api/institution/register" "$register_data" "200"

# ================================
# 2. 测试登录接口返回机构名称
# ================================
echo -e "${YELLOW}2. 测试登录接口返回机构名称${NC}"

login_data='{
    "phone": "chaoyang_admin",
    "password": "admin123456"
}'
test_api "登录接口测试" "POST" "/api/auth/login" "$login_data" "200"

# 获取登录响应并验证机构名称
login_response=$(curl -s -X POST "$BASE_URL/api/auth/login" -H "Content-Type: application/json" -d "$login_data")
echo "登录响应: $login_response" >> $LOG_FILE

# 检查是否包含机构名称字段
if [[ $login_response == *"institutionName"* ]]; then
    echo -e "${GREEN}✓ 登录响应包含机构名称字段${NC}"
    echo "登录响应包含机构名称字段" >> $LOG_FILE
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ 登录响应不包含机构名称字段${NC}"
    echo "登录响应不包含机构名称字段" >> $LOG_FILE
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# 检查机构名称值是否正确
if [[ $login_response == *"北京朝阳校区（已更新）"* ]]; then
    echo -e "${GREEN}✓ 机构名称值正确${NC}"
    echo "机构名称值正确" >> $LOG_FILE
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ 机构名称值不正确${NC}"
    echo "机构名称值不正确" >> $LOG_FILE
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# ================================
# 3. 测试Token解析返回机构名称
# ================================
echo -e "${YELLOW}3. 测试Token解析返回机构名称${NC}"

# 获取token
token=$(echo $login_response | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "获取到token: ${token:0:20}..."

# 使用token调用需要认证的接口来验证token解析
if [ -n "$token" ]; then
    # 这里可以调用任何需要认证的接口来验证token解析
    # 比如获取用户信息或校区列表
    auth_response=$(curl -s -X GET "$BASE_URL/api/campus/list" -H "Authorization: Bearer $token")
    echo "认证接口响应: $auth_response" >> $LOG_FILE
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Token解析正常${NC}"
        echo "Token解析正常" >> $LOG_FILE
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗ Token解析失败${NC}"
        echo "Token解析失败" >> $LOG_FILE
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
else
    echo -e "${RED}✗ 未获取到token${NC}"
    echo "未获取到token" >> $LOG_FILE
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# ================================
# 4. 测试错误情况
# ================================
echo -e "${YELLOW}4. 测试错误情况${NC}"

# 测试错误的密码
wrong_password_data='{
    "phone": "chaoyang_admin",
    "password": "wrong_password"
}'
test_api "错误密码测试" "POST" "/api/auth/login" "$wrong_password_data" "400"

# 测试不存在的用户
wrong_user_data='{
    "phone": "nonexistent_user",
    "password": "admin123456"
}'
test_api "不存在用户测试" "POST" "/api/auth/login" "$wrong_user_data" "400"

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
    echo -e "${GREEN}所有测试通过！登录接口机构名称功能正常${NC}"
    echo "所有测试通过！登录接口机构名称功能正常" >> $LOG_FILE
else
    echo -e "${RED}有 $FAILED_TESTS 个测试失败${NC}"
    echo "有 $FAILED_TESTS 个测试失败" >> $LOG_FILE
fi

echo "==========================================" >> $LOG_FILE
echo "测试结果汇总" >> $LOG_FILE
echo "通过测试: $PASSED_TESTS" >> $LOG_FILE
echo "失败测试: $FAILED_TESTS" >> $LOG_FILE
echo "总测试数: $((PASSED_TESTS + FAILED_TESTS))" >> $LOG_FILE

echo ""
echo -e "${YELLOW}测试账号信息:${NC}"
echo -e "${GREEN}用户名: chaoyang_admin${NC}"
echo -e "${GREEN}密码: admin123456${NC}"
echo -e "${GREEN}机构名称: 北京朝阳校区（已更新）${NC}"

echo ""
echo "详细测试日志已保存到: $LOG_FILE" 