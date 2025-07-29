#!/bin/bash

TOKEN="eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjE2LCJvcmdJZCI6NiwiaWF0IjoxNzUzNzE3MTkxLCJleHAiOjE3NTQzMjE5OTF9.ir0o1YC6q2fmVsSDwLEKDaoVgVMLWQInr35VB2MOsxxlgpDPD5Gdyr8KxVQUm6PK"

echo "测试学员分析统计数据接口"
curl -X POST http://localhost:8080/lesson/api/statistics/student-analysis \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"timeType": "MONTHLY", "campusId": 1}'

echo -e "\n\n测试学员指标统计接口"
curl -X POST http://localhost:8080/lesson/api/statistics/student/metrics \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"timeType": "MONTHLY", "campusId": 1}' 