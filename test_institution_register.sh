#!/bin/bash

# 机构注册验证接口测试脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 基础URL
BASE_URL="http://localhost:8080/lesson"

# 日志文件
LOG_FILE="institution_register_test.log"

# 计数器
PASSED_TESTS=0
FAILED_TESTS=0

echo "==========================================" > $LOG_FILE
echo "机构注册验证接口测试报告" >> $LOG_FILE
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
echo "开始机构注册验证接口测试"
echo "=========================================="

# ================================
# 1. 机构注册测试
# ================================
echo -e "${YELLOW}1. 机构注册测试${NC}"

register_data='{
    "name": "北京朝阳校区（已更新）",
    "contactName": "朝阳校区管理员",
    "username": "chaoyang_admin",
    "password": "admin123456"
}'
test_api "机构注册" "POST" "/api/institution/register" "$register_data" "200"

# ================================
# 2. 机构详情查询测试
# ================================
echo -e "${YELLOW}2. 机构详情查询测试${NC}"

test_api "机构详情查询" "GET" "/api/institution/detail?id=1" "" "200"

# ================================
# 3. 超级管理员登录测试
# ================================
echo -e "${YELLOW}3. 超级管理员登录测试${NC}"

login_data='{
    "phone": "chaoyang_admin",
    "password": "admin123456"
}'
test_api "超级管理员登录" "POST" "/api/auth/login" "$login_data" "200"

# ================================
# 4. 重复注册测试
# ================================
echo -e "${YELLOW}4. 重复注册测试${NC}"

duplicate_register_data='{
    "name": "重复注册测试",
    "contactName": "测试管理员",
    "username": "chaoyang_admin",
    "password": "test123456"
}'
test_api "重复注册测试" "POST" "/api/institution/register" "$duplicate_register_data" "400"

# ================================
# 5. 参数验证测试
# ================================
echo -e "${YELLOW}5. 参数验证测试${NC}"

invalid_data='{
    "name": "",
    "contactName": "",
    "username": "",
    "password": ""
}'
test_api "参数验证测试" "POST" "/api/institution/register" "$invalid_data" "400"

# ================================
# 6. 权限验证测试
# ================================
echo -e "${YELLOW}6. 权限验证测试${NC}"

# 先登录获取token
login_response=$(curl -s -X POST "$BASE_URL/api/auth/login" -H "Content-Type: application/json" -d "$login_data")
TOKEN=$(echo $login_response | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo "获取到token: ${TOKEN:0:20}..."
    test_api "权限验证测试" "GET" "/api/user/list" "" "200"
else
    echo -e "${RED}获取token失败，跳过权限验证测试${NC}"
    echo "获取token失败，跳过权限验证测试" >> $LOG_FILE
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
    echo -e "${GREEN}所有测试通过！${NC}"
    echo "所有测试通过！" >> $LOG_FILE
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
echo -e "${YELLOW}超级管理员账号信息:${NC}"
echo -e "${GREEN}用户名: chaoyang_admin${NC}"
echo -e "${GREEN}密码: admin123456${NC}"
echo -e "${GREEN}真实姓名: 朝阳校区管理员${NC}"
echo -e "${GREEN}角色: 超级管理员${NC}"

echo ""
echo "详细测试日志已保存到: $LOG_FILE" 