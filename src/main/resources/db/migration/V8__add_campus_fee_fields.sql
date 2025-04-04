-- 添加校区费用相关字段
ALTER TABLE `edu_campus`
    ADD COLUMN `monthly_rent` DECIMAL(10,2) COMMENT '月租金',
    ADD COLUMN `property_fee` DECIMAL(10,2) COMMENT '物业费',
    ADD COLUMN `utility_fee` DECIMAL(10,2) COMMENT '水电费'; 