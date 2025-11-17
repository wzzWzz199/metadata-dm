package com.hayden.hap.common.spring.test;

import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.dbop.db.sharding.transaction.ShardingTransactionManager;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Legacy base class for integration tests that still rely on manual wiring.
 */
@SpringBootTest
public abstract class BaseSpringTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private JdbcTemplateSupportDao jdbcTemplateSupportDao;

    @Autowired(required = false)
    private ShardingTransactionManager shardingTransactionManager;

    @BeforeEach
    void prepareContextData() {
        AppServiceHelper.setApplicationContext(applicationContext);
        if (applicationContext == null) {
            return;
        }
        Map<String, DataSource> dataSources = applicationContext.getBeansOfType(DataSource.class);
        if (jdbcTemplateSupportDao != null) {
            jdbcTemplateSupportDao.init(dataSources);
        }
        if (shardingTransactionManager != null) {
            shardingTransactionManager.init(dataSources);
        }
    }
}
