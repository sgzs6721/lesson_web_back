#!/bin/bash

# 设置基础URL和JWT token
BASE_URL="http://localhost:8080/lesson"
TOKEN="eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjE2LCJvcmdJZCI6NiwiaWF0IjoxNzUzNzE2NTIyLCJleHAiOjE3NTQzMjEzMjJ9.Ylt9p7tBFM35RGDtvHC9a4OmSfg3nipjdykJPQQ9941mCfw2n0h1Onn4oK72Hthg"

echo "开始测试StatisticsController中的所有接口..."
echo "================================================"

# 1. 学员分析统计接口测试

echo "1. 测试学员分析统计接口"
echo "------------------------"

echo "1.1 获取学员分析统计数据（完整版）"
curl -X POST "$BASE_URL/api/statistics/student-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n1.2 获取学员指标统计"
curl -X POST "$BASE_URL/api/statistics/student/metrics" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n1.3 获取学员增长趋势"
curl -X POST "$BASE_URL/api/statistics/student/growth-trend" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n1.4 获取学员续费金额趋势"
curl -X POST "$BASE_URL/api/statistics/student/renewal-trend" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n1.5 获取学员来源分布"
curl -X POST "$BASE_URL/api/statistics/student/source-distribution" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n1.6 获取新增学员来源分布"
curl -X POST "$BASE_URL/api/statistics/student/new-student-source" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

# 2. 课程分析统计接口测试

echo -e "\n\n2. 测试课程分析统计接口"
echo "------------------------"

echo "2.1 获取课程分析统计数据（完整版）"
curl -X POST "$BASE_URL/api/statistics/course-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1,
    "rankingType": "REVENUE",
    "limit": 10
  }'

echo -e "\n\n2.2 获取课程指标统计"
curl -X POST "$BASE_URL/api/statistics/course/metrics" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n2.3 获取课程类型分析"
curl -X POST "$BASE_URL/api/statistics/course/type-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n2.4 获取课程销售趋势"
curl -X POST "$BASE_URL/api/statistics/course/sales-trend" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n2.5 获取课程销售表现"
curl -X POST "$BASE_URL/api/statistics/course/sales-performance" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n2.6 获取课程销售排行"
curl -X POST "$BASE_URL/api/statistics/course/sales-ranking" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1,
    "rankingType": "REVENUE",
    "limit": 10
  }'

echo -e "\n\n2.7 获取课程收入分析"
curl -X POST "$BASE_URL/api/statistics/course/revenue-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n2.8 获取课程收入分布"
curl -X POST "$BASE_URL/api/statistics/course/revenue-distribution" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

# 3. 教练分析统计接口测试

echo -e "\n\n3. 测试教练分析统计接口"
echo "------------------------"

echo "3.1 获取教练分析统计数据（完整版）"
curl -X POST "$BASE_URL/api/statistics/coach-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n3.2 获取教练指标统计"
curl -X POST "$BASE_URL/api/statistics/coach/metrics" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n3.3 获取教练课时趋势"
curl -X POST "$BASE_URL/api/statistics/coach/class-hour-trend" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n3.4 获取教练TOP5对比"
curl -X POST "$BASE_URL/api/statistics/coach/top5-comparison" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n3.5 获取教练类型分布"
curl -X POST "$BASE_URL/api/statistics/coach/type-distribution" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n3.6 获取教练薪资分析"
curl -X POST "$BASE_URL/api/statistics/coach/salary-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n3.7 获取教练绩效排行"
curl -X POST "$BASE_URL/api/statistics/coach/performance-ranking" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

# 4. 财务分析统计接口测试

echo -e "\n\n4. 测试财务分析统计接口"
echo "------------------------"

echo "4.1 获取财务分析统计数据（完整版）"
curl -X POST "$BASE_URL/api/statistics/finance-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n4.2 获取财务指标统计"
curl -X POST "$BASE_URL/api/statistics/finance/metrics" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n4.3 获取财务收支趋势"
curl -X POST "$BASE_URL/api/statistics/finance/revenue-cost-trend" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n4.4 获取财务成本结构"
curl -X POST "$BASE_URL/api/statistics/finance/cost-structure" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n4.5 获取财务趋势"
curl -X POST "$BASE_URL/api/statistics/finance/trend" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n4.6 获取财务收入分析"
curl -X POST "$BASE_URL/api/statistics/finance/revenue-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n4.7 获取财务成本分析"
curl -X POST "$BASE_URL/api/statistics/finance/cost-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

echo -e "\n\n4.8 获取财务利润分析"
curl -X POST "$BASE_URL/api/statistics/finance/profit-analysis" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "timeType": "MONTHLY",
    "campusId": 1
  }'

# 5. 新增统计接口测试

echo -e "\n\n5. 测试新增统计接口"
echo "------------------------"

echo "5.1 获取学员管理页面统计数据"
curl -X GET "$BASE_URL/api/statistics/student-management/summary" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n5.2 刷新统计数据"
curl -X POST "$BASE_URL/api/statistics/refresh-stats" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n5.3 刷新指定校区统计数据"
curl -X POST "$BASE_URL/api/statistics/refresh-stats?campusId=1" \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n\n测试完成！" 