package com.lesson.config;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@Configuration
public class JooqConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DSLContext dslContext() {
        // 使用TransactionAwareDataSourceProxy包装数据源，使其支持Spring事务管理
        TransactionAwareDataSourceProxy transactionAwareDataSource = new TransactionAwareDataSourceProxy(dataSource);
        
        // 创建jOOQ配置
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.set(transactionAwareDataSource);
        configuration.set(SQLDialect.MYSQL);
        
        // 添加设置，可选
        Settings settings = new Settings();
        settings.setRenderNameCase(RenderNameCase.AS_IS); // 保持名称大小写不变
        configuration.setSettings(settings);
        
        // 返回DSLContext
        return new DefaultDSLContext(configuration);
    }
} 