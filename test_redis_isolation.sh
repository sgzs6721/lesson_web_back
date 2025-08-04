#!/bin/bash

# Redis环境隔离测试脚本
# 用于验证不同环境的Redis数据隔离效果

BASE_URL="http://localhost:8090/lesson"

echo "=========================================="
echo "Redis环境隔离测试脚本"
echo "=========================================="

# 检查服务是否启动
echo "1. 检查服务状态..."
if curl -s "$BASE_URL/api/redis/info" > /dev/null; then
    echo "✓ 服务正常运行"
else
    echo "✗ 服务未启动，请先启动应用"
    exit 1
fi

echo ""
echo "2. 获取Redis隔离信息..."
curl -s "$BASE_URL/api/redis/info" | jq '.'

echo ""
echo "3. 获取所有环境统计信息..."
curl -s "$BASE_URL/api/redis/stats" | jq '.'

echo ""
echo "4. 获取当前环境统计信息..."
curl -s "$BASE_URL/api/redis/stats/current" | jq '.'

echo ""
echo "=========================================="
echo "测试完成！"
echo ""
echo "使用说明："
echo "1. 启动不同环境时使用不同的profile:"
echo "   - 生产环境: --spring.profiles.active=prod"
echo "   - 测试环境: --spring.profiles.active=test"
echo "   - 开发环境: --spring.profiles.active=dev"
echo ""
echo "2. 管理Redis数据:"
echo "   - 查看统计: GET $BASE_URL/api/redis/stats"
echo "   - 清理当前环境: DELETE $BASE_URL/api/redis/clear/current"
echo "   - 清理指定环境: DELETE $BASE_URL/api/redis/clear/{environment}"
echo "   - 清理所有环境: DELETE $BASE_URL/api/redis/clear/all"
echo ""
echo "3. 每个环境的Redis key都会自动添加前缀:"
echo "   - 生产环境: lesson:prod:"
echo "   - 测试环境: lesson:test:"
echo "   - 开发环境: lesson:dev:"
echo "==========================================" 