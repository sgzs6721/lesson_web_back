# 统计功能说明文档

## 概述

本系统提供了完整的统计分析功能，包括学员分析、课程分析和教练分析三大模块。每个模块都提供了完整版接口和拆分接口，以满足不同的使用场景。

## 接口设计原则

### 完整版接口
- 一次性返回所有相关数据
- 适用于需要完整统计信息的场景
- 减少前端请求次数

### 拆分接口
- 按功能模块拆分，每个接口返回特定类型的数据
- 提供更灵活的数据获取方式
- 减少数据传输量，提高性能
- 支持按需加载

## 1. 学员分析统计

### 1.1 完整版接口

#### 获取学员分析统计数据（完整版）
- **接口地址**: `POST /api/statistics/student-analysis`
- **功能**: 一次性获取所有学员分析数据
- **返回数据**: 包含指标统计、增长趋势、续费趋势、来源分布等完整信息

### 1.2 拆分接口

#### 1.2.1 学员指标统计
- **接口地址**: `POST /api/statistics/student/metrics`
- **功能**: 获取学员核心指标数据
- **返回数据**: 
  - 学员总数
  - 新增学员数
  - 续费学员数
  - 流失学员数
  - 各指标的变化率

#### 1.2.2 学员增长趋势
- **接口地址**: `POST /api/statistics/student/growth-trend`
- **功能**: 获取学员数量增长趋势
- **返回数据**: 按时间维度的学员数量变化趋势

#### 1.2.3 学员续费金额趋势
- **接口地址**: `POST /api/statistics/student/renewal-trend`
- **功能**: 获取学员续费金额趋势
- **返回数据**: 按时间维度的续费金额变化趋势

#### 1.2.4 学员来源分布
- **接口地址**: `POST /api/statistics/student/source-distribution`
- **功能**: 获取学员来源分布统计
- **返回数据**: 各来源渠道的学员数量和占比

#### 1.2.5 新增学员来源分布
- **接口地址**: `POST /api/statistics/student/new-student-source`
- **功能**: 获取新增学员来源分布
- **返回数据**: 新增学员的来源渠道分布

### 1.3 请求参数

```json
{
  "timeType": "MONTHLY",  // 统计类型：WEEKLY, MONTHLY, QUARTERLY, YEARLY
  "startTime": "2024-01-01",  // 开始时间（可选）
  "endTime": "2024-12-31",    // 结束时间（可选）
  "campusId": 1,              // 校区ID（可选）
  "institutionId": 1          // 机构ID（可选）
}
```

### 1.4 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "studentMetrics": {
      "totalStudents": 1250,
      "newStudents": 180,
      "renewalStudents": 95,
      "lostStudents": 25,
      "newStudentChangeRate": 8.4,
      "renewalStudentChangeRate": 12.3,
      "lostStudentChangeRate": -5.2
    },
    "growthTrend": [
      {
        "date": "2024-01",
        "studentCount": 1200,
        "newStudentCount": 150
      }
    ],
    "renewalAmountTrend": [
      {
        "date": "2024-01",
        "renewalAmount": 45000.00,
        "renewalCount": 95
      }
    ],
    "sourceDistribution": [
      {
        "sourceName": "推荐",
        "studentCount": 450,
        "percentage": 36.0
      }
    ],
    "newStudentSourceDistribution": [
      {
        "sourceName": "推荐",
        "newStudentCount": 45,
        "percentage": 30.0
      }
    ]
  }
}
```

## 2. 课程分析统计

### 2.1 完整版接口

#### 获取课程分析统计数据（完整版）
- **接口地址**: `POST /api/statistics/course-analysis`
- **功能**: 一次性获取所有课程分析数据
- **返回数据**: 包含指标统计、类型分析、销售趋势、销售表现、销售排行、收入分析、收入分布等完整信息

### 2.2 拆分接口

#### 2.2.1 课程指标统计
- **接口地址**: `POST /api/statistics/course/metrics`
- **功能**: 获取课程核心指标数据
- **返回数据**: 
  - 课程总数
  - 新报课程数
  - 续费课程数
  - 已销课程数
  - 剩余课程数

#### 2.2.2 课程类型分析
- **接口地址**: `POST /api/statistics/course/type-analysis`
- **功能**: 获取课程类型分布分析
- **返回数据**: 各类型课程的数量、销量、收入等统计

#### 2.2.3 课程销售趋势
- **接口地址**: `POST /api/statistics/course/sales-trend`
- **功能**: 获取课程销售趋势
- **返回数据**: 按时间维度的课程销售数量变化趋势

#### 2.2.4 课程销售表现
- **接口地址**: `POST /api/statistics/course/sales-performance`
- **功能**: 获取课程销售表现统计
- **返回数据**: 各课程的销售数量、收入、转化率等表现数据

#### 2.2.5 课程销售排行
- **接口地址**: `POST /api/statistics/course/sales-ranking`
- **功能**: 获取课程销售排行榜
- **返回数据**: 按销量、收入等维度排序的课程排行

#### 2.2.6 课程收入分析
- **接口地址**: `POST /api/statistics/course/revenue-analysis`
- **功能**: 获取课程收入分析
- **返回数据**: 总收入、平均收入、最高收入、最低收入等统计

#### 2.2.7 课程收入分布
- **接口地址**: `POST /api/statistics/course/revenue-distribution`
- **功能**: 获取课程收入分布
- **返回数据**: 各类型课程的收入分布情况

### 2.3 请求参数

```json
{
  "timeType": "MONTHLY",  // 统计类型：WEEKLY, MONTHLY, QUARTERLY, YEARLY
  "startTime": "2024-01-01",  // 开始时间（可选）
  "endTime": "2024-12-31",    // 结束时间（可选）
  "campusId": 1,              // 校区ID（可选）
  "institutionId": 1,         // 机构ID（可选）
  "rankingType": "REVENUE",   // 排行类型：REVENUE, SALES_QUANTITY, UNIT_PRICE（可选）
  "limit": 10                 // 排行数量限制（可选）
}
```

### 2.4 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "courseMetrics": {
      "totalCourses": 85,
      "newCoursesEnrolled": 180,
      "renewedCourses": 95,
      "soldCourses": 320,
      "remainingCourses": 450
    },
    "courseTypeAnalysis": [
      {
        "courseType": "游泳",
        "courseCount": 25,
        "salesQuantity": 180,
        "revenue": 450000.00,
        "percentage": 35.3
      }
    ],
    "salesTrend": [
      {
        "date": "2024-01",
        "salesQuantity": 180,
        "revenue": 450000.00
      }
    ],
    "salesPerformance": [
      {
        "courseName": "游泳基础班",
        "salesQuantity": 45,
        "revenue": 112500.00,
        "conversionRate": 85.7
      }
    ],
    "salesRanking": [
      {
        "courseName": "游泳基础班",
        "salesQuantity": 45,
        "revenue": 112500.00,
        "ranking": 1
      }
    ],
    "revenueAnalysis": {
      "totalRevenue": 1275000.00,
      "averageRevenue": 15000.00,
      "maxRevenue": 45000.00,
      "minRevenue": 5000.00
    },
    "revenueDistribution": [
      {
        "courseType": "游泳",
        "revenue": 450000.00,
        "percentage": 35.3
      }
    ]
  }
}
```

## 3. 教练分析统计

### 3.1 完整版接口

#### 获取教练分析统计数据（完整版）
- **接口地址**: `POST /api/statistics/coach-analysis`
- **功能**: 一次性获取所有教练分析数据
- **返回数据**: 包含绩效指标、课时趋势、TOP5对比、类型分布、薪资分析、绩效排名等完整信息

### 3.2 拆分接口

#### 3.2.1 教练绩效指标
- **接口地址**: `POST /api/statistics/coach/metrics`
- **功能**: 获取教练核心绩效指标
- **返回数据**: 
  - 教练总数
  - 活跃教练数
  - 平均课时数
  - 平均薪资
  - 各指标的变化率

#### 3.2.2 教练课时统计趋势
- **接口地址**: `POST /api/statistics/coach/class-hour-trend`
- **功能**: 获取教练课时统计趋势
- **返回数据**: 按时间维度的教练课时变化趋势

#### 3.2.3 教练TOP5多维度对比
- **接口地址**: `POST /api/statistics/coach/top5-comparison`
- **功能**: 获取教练TOP5多维度对比
- **返回数据**: 按不同维度排序的TOP5教练对比数据

#### 3.2.4 教练类型分布
- **接口地址**: `POST /api/statistics/coach/type-distribution`
- **功能**: 获取教练类型分布
- **返回数据**: 各类型教练的数量和占比

#### 3.2.5 教练薪资分析
- **接口地址**: `POST /api/statistics/coach/salary-analysis`
- **功能**: 获取教练薪资分析
- **返回数据**: 薪资总额、平均薪资、最高薪资、最低薪资等统计

#### 3.2.6 教练绩效排名
- **接口地址**: `POST /api/statistics/coach/performance-ranking`
- **功能**: 获取教练绩效排名
- **返回数据**: 按绩效指标排序的教练排名

### 3.3 请求参数

```json
{
  "timeType": "MONTHLY",  // 统计类型：WEEKLY, MONTHLY, QUARTERLY, YEARLY
  "startTime": "2024-01-01",  // 开始时间（可选）
  "endTime": "2024-12-31",    // 结束时间（可选）
  "campusId": 1,              // 校区ID（可选）
  "institutionId": 1,         // 机构ID（可选）
  "coachTypeId": 1,           // 教练类型ID（可选）
  "rankingType": "CLASS_HOURS", // 排行类型：CLASS_HOURS, STUDENTS, REVENUE（可选）
  "limit": 5                  // 排行数量限制（可选）
}
```

### 3.4 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "coachMetrics": {
      "totalCoaches": 45,
      "activeCoaches": 38,
      "monthlyAverageClassHours": 120,
      "monthlyAverageSalary": 8500.00,
      "activeCoachChangeRate": 5.6,
      "classHoursChangeRate": 8.2,
      "salaryChangeRate": 3.1
    },
    "classHourTrend": [
      {
        "date": "2024-01",
        "classHoursCount": 4560,
        "coachCount": 38
      }
    ],
    "coachTop5Comparison": [
      {
        "coachName": "张教练",
        "classHours": 180,
        "studentCount": 25,
        "revenue": 45000.00,
        "ranking": 1
      }
    ],
    "coachTypeDistribution": [
      {
        "coachType": "游泳教练",
        "coachCount": 20,
        "percentage": 44.4
      }
    ],
    "salaryAnalysis": {
      "totalSalaryExpense": 323000.00,
      "averageSalary": 8500.00,
      "maxSalary": 12000.00,
      "minSalary": 6000.00
    },
    "performanceRanking": [
      {
        "coachName": "张教练",
        "classHours": 180,
        "studentCount": 25,
        "revenue": 45000.00,
        "ranking": 1
      }
    ]
  }
}
```

## 4. 使用建议

### 4.1 接口选择建议

1. **完整版接口适用场景**：
   - 页面初始化加载
   - 需要完整统计信息的场景
   - 数据量相对较小的统计

2. **拆分接口适用场景**：
   - 按需加载特定模块数据
   - 需要频繁更新特定统计信息
   - 移动端等网络环境较差的场景
   - 大数据量的统计场景

### 4.2 性能优化建议

1. **合理使用缓存**：对于不经常变化的数据，建议在前端或服务端进行缓存
2. **按需加载**：根据用户操作动态加载相应的统计模块
3. **分页加载**：对于排行类数据，建议使用分页加载
4. **数据压缩**：对于大数据量的接口，建议启用GZIP压缩

### 4.3 错误处理

所有接口都遵循统一的错误处理规范：

```json
{
  "code": 500,
  "message": "获取统计数据失败",
  "data": null
}
```

常见错误码：
- 200: 成功
- 400: 请求参数错误
- 500: 服务器内部错误

## 5. 测试接口

项目根目录下的 `api-test.http` 文件包含了所有统计接口的测试用例，可以直接在支持HTTP文件的IDE中使用。

## 6. 注意事项

1. **时间参数**：如果不指定startTime和endTime，系统会根据timeType自动计算时间范围
2. **权限控制**：所有统计接口都需要相应的权限才能访问
3. **数据范围**：统计数据会根据用户权限自动过滤相应的校区和机构数据
4. **性能考虑**：大数据量的统计查询可能需要较长时间，建议添加适当的超时设置 