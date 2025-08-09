#!/bin/bash

# 课程创建与更新接口测试脚本
# 使用说明：
# 1) 替换 BASE_URL 与 TOKEN
# 2) 替换下面的示例参数（campusId、typeId、coachId等）为你环境中的真实数据
# 3) 运行: chmod +x test_course_api.sh && ./test_course_api.sh

BASE_URL="http://localhost:8080/api"
TOKEN="Bearer YOUR_JWT_TOKEN"

# 示例参数（请按需修改）
INSTITUTION_ID=1
CAMPUS_ID=1
COURSE_TYPE_ID=1
COACH_ID_A=1
COACH_ID_B=2

_header() {
  echo "\n========== $1 =========="
}

post() {
  local url="$1"; shift
  local body="$1"; shift
  curl -sS -X POST "$url" \
    -H "Content-Type: application/json" \
    -H "Authorization: $TOKEN" \
    -d "$body" | jq .
}

get() {
  local url="$1"; shift
  curl -sS -X GET "$url" \
    -H "Content-Type: application/json" \
    -H "Authorization: $TOKEN" | jq .
}

# 1. 单教师 - 创建课程
_header "1. 单教师 - 创建课程"
CREATE_SINGLE_BODY=$(cat <<JSON
{
  "name": "少儿舞蹈-单教师-$(date +%H%M%S)",
  "typeId": $COURSE_TYPE_ID,
  "status": "PUBLISHED",
  "unitHours": 1.0,
  "price": 199.00,
  "isMultiTeacher": false,
  "coachFees": [
    { "coachId": $COACH_ID_A, "coachFee": 80.00 }
  ],
  "campusId": $CAMPUS_ID,
  "description": "单教师课程自动化创建"
}
JSON
)
CREATE_SINGLE_RESP=$(post "$BASE_URL/courses/create" "$CREATE_SINGLE_BODY")
SINGLE_COURSE_ID=$(echo "$CREATE_SINGLE_RESP" | jq -r '.data // empty')
echo "SINGLE_COURSE_ID=$SINGLE_COURSE_ID"

# 2. 多教师 - 创建课程
_header "2. 多教师 - 创建课程"
CREATE_MULTI_BODY=$(cat <<JSON
{
  "name": "少儿舞蹈-多教师-$(date +%H%M%S)",
  "typeId": $COURSE_TYPE_ID,
  "status": "PUBLISHED",
  "unitHours": 1.5,
  "price": 299.00,
  "isMultiTeacher": true,
  "coachFees": [
    { "coachId": $COACH_ID_A, "coachFee": 90.00 },
    { "coachId": $COACH_ID_B, "coachFee": 100.00 }
  ],
  "campusId": $CAMPUS_ID,
  "description": "多教师课程自动化创建"
}
JSON
)
CREATE_MULTI_RESP=$(post "$BASE_URL/courses/create" "$CREATE_MULTI_BODY")
MULTI_COURSE_ID=$(echo "$CREATE_MULTI_RESP" | jq -r '.data // empty')
echo "MULTI_COURSE_ID=$MULTI_COURSE_ID"

# 3. 查询详情（单教师课程）
if [[ -n "$SINGLE_COURSE_ID" && "$SINGLE_COURSE_ID" != "null" ]]; then
  _header "3. 查询单教师课程详情"
  get "$BASE_URL/courses/detail?id=$SINGLE_COURSE_ID"
fi

# 4. 更新课程（单教师 -> 改价格与课时费）
if [[ -n "$SINGLE_COURSE_ID" && "$SINGLE_COURSE_ID" != "null" ]]; then
  _header "4. 更新单教师课程"
  UPDATE_SINGLE_BODY=$(cat <<JSON
{
  "id": $SINGLE_COURSE_ID,
  "name": "少儿舞蹈-单教师-更新",
  "typeId": $COURSE_TYPE_ID,
  "status": "PUBLISHED",
  "unitHours": 1.0,
  "price": 209.00,
  "isMultiTeacher": false,
  "coachFees": [
    { "coachId": $COACH_ID_A, "coachFee": 88.00 }
  ],
  "campusId": $CAMPUS_ID,
  "description": "单教师课程自动化更新"
}
JSON
  )
  post "$BASE_URL/courses/update" "$UPDATE_SINGLE_BODY"
fi

# 5. 更新课程（多教师 -> 调整教练列表）
if [[ -n "$MULTI_COURSE_ID" && "$MULTI_COURSE_ID" != "null" ]]; then
  _header "5. 更新多教师课程"
  UPDATE_MULTI_BODY=$(cat <<JSON
{
  "id": $MULTI_COURSE_ID,
  "name": "少儿舞蹈-多教师-更新",
  "typeId": $COURSE_TYPE_ID,
  "status": "PUBLISHED",
  "unitHours": 1.5,
  "price": 319.00,
  "isMultiTeacher": true,
  "coachFees": [
    { "coachId": $COACH_ID_B, "coachFee": 110.00 }
  ],
  "campusId": $CAMPUS_ID,
  "description": "多教师课程自动化更新-移除A，只保留B"
}
JSON
  )
  post "$BASE_URL/courses/update" "$UPDATE_MULTI_BODY"
fi

# 6. 列表分页
_header "6. 课程列表（分页）"
get "$BASE_URL/courses/list?pageNum=1&pageSize=10"

# 7. 完成
_header "完成"
echo "请核对返回中的字段：\n- 单教师课程：coaches 列表应为 1 人，coachFee 为 88.00\n- 多教师课程（更新后）：coaches 列表应只剩 1 人且为 B，coachFee 为 110.00\n"