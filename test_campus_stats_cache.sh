#!/bin/bash

# 校区统计数据缓存管理测试脚本

# 配置
BASE_URL="http://localhost:8080/api"
INSTITUTION_ID=1
CAMPUS_ID=1

echo "=== 校区统计数据缓存管理测试 ==="
echo "机构ID: $INSTITUTION_ID"
echo "校区ID: $CAMPUS_ID"
echo ""

# 1. 查看当前校区统计数据
echo "1. 查看当前校区统计数据..."
curl -s -X GET "$BASE_URL/campus/list?pageNum=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" | jq '.data.list[] | {id, name, coachCount, studentCount}'

echo ""
echo "2. 查看Redis中的校区统计数据缓存..."
curl -s -X GET "$BASE_URL/redis/stats" \
  -H "Content-Type: application/json" | jq '.'

echo ""

# 3. 清理校区统计数据缓存
echo "3. 清理校区统计数据缓存..."
curl -s -X DELETE "$BASE_URL/redis/clear-campus-stats/$INSTITUTION_ID" \
  -H "Content-Type: application/json" | jq '.'

echo ""

# 4. 查看清理后的Redis状态
echo "4. 查看清理后的Redis状态..."
curl -s -X GET "$BASE_URL/redis/stats" \
  -H "Content-Type: application/json" | jq '.'

echo ""

# 5. 刷新校区统计数据
echo "5. 刷新校区统计数据..."
curl -s -X POST "$BASE_URL/redis/refresh-campus-stats/$INSTITUTION_ID" \
  -H "Content-Type: application/json" | jq '.'

echo ""

# 6. 查看刷新后的校区统计数据
echo "6. 查看刷新后的校区统计数据..."
curl -s -X GET "$BASE_URL/campus/list?pageNum=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" | jq '.data.list[] | {id, name, coachCount, studentCount}'

echo ""

# 7. 查看刷新后的Redis状态
echo "7. 查看刷新后的Redis状态..."
curl -s -X GET "$BASE_URL/redis/stats" \
  -H "Content-Type: application/json" | jq '.'

echo ""

# 8. 清理并刷新单个校区统计数据
echo "8. 清理并刷新单个校区统计数据..."
curl -s -X POST "$BASE_URL/redis/refresh-single-campus-stats/$INSTITUTION_ID/$CAMPUS_ID" \
  -H "Content-Type: application/json" | jq '.'

echo ""

# 9. 清理并刷新所有校区统计数据
echo "9. 清理并刷新所有校区统计数据..."
curl -s -X POST "$BASE_URL/redis/clear-and-refresh-campus-stats/$INSTITUTION_ID" \
  -H "Content-Type: application/json" | jq '.'

echo ""

echo "=== 测试完成 ==="
echo ""
echo "注意事项："
echo "1. 请确保Redis服务正在运行"
echo "2. 请替换YOUR_TOKEN_HERE为实际的认证token"
echo "3. 教练数量现在只统计在职教练（status='active'）"
echo "4. 学员数量只统计在学学员（status='STUDYING'）"
echo "5. 缓存清理后，下次访问会自动从数据库重新计算并缓存" 