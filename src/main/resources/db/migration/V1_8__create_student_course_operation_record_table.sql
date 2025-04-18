CREATE TABLE edu_student_course_operation_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：TRANSFER_COURSE-转课, TRANSFER_CLASS-转班, REFUND-退费',
    before_status VARCHAR(20) NOT NULL COMMENT '操作前状态',
    after_status VARCHAR(20) NOT NULL COMMENT '操作后状态',
    source_course_id BIGINT COMMENT '原课程ID（转课时使用）',
    target_course_id BIGINT COMMENT '目标课程ID（转课时使用）',
    source_class_id BIGINT COMMENT '原班级ID（转班时使用）',
    target_class_id BIGINT COMMENT '目标班级ID（转班时使用）',
    refund_amount DECIMAL(10,2) COMMENT '退费金额（退费时使用）',
    refund_method VARCHAR(20) COMMENT '退费方式：CASH-现金, BANK_TRANSFER-银行转账, WECHAT-微信, ALIPAY-支付宝',
    operation_reason TEXT COMMENT '操作原因',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生课程操作记录表'; 