package com.hayden.hap.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.common.db.sharding.route.ShardingRouter;
import com.hayden.hap.common.db.sharding.route.config.support.ShardingRouterXmlFactoryBean;
import com.hayden.hap.common.db.sharding.route.script.DefaultVORouteFunction;
import com.hayden.hap.config.properties.JdbcBaseMycatProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(JdbcBaseMycatProperties.class)
public class DatabaseConfig {

    @Bean(name = "dataSource")
    public DruidXADataSource dataSource(JdbcBaseMycatProperties properties) throws SQLException {
        DruidXADataSource dataSource = new DruidXADataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setInitialSize(properties.getPool().getInitialSize());
        dataSource.setMaxActive(properties.getPool().getMaxActive());
        dataSource.setMinIdle(properties.getPool().getMinIdle());
        dataSource.setValidationQuery(properties.getPool().getValidationQuery());
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(1800);
        dataSource.setLogAbandoned(true);
        dataSource.setDbType(properties.getDbType());
        String filters = "dm".equalsIgnoreCase(properties.getDbType()) ? "stat,slf4j" : "stat,slf4j,wall";
        dataSource.setFilters(filters);
        return dataSource;
    }

    @Bean
    public ShardingRouter shardingRouter() throws Exception {
        Resource resource = new ClassPathResource("conf/common/sharding-rules.xml");
        if (!resource.exists()) {
            return new ShardingRouter();
        }
        Map<String, Object> routeScriptsMap = new HashMap<>();
        routeScriptsMap.put("hash", new DefaultVORouteFunction());
        ShardingRouterXmlFactoryBean factoryBean = new ShardingRouterXmlFactoryBean();
        factoryBean.setRouteScriptsMap(routeScriptsMap);
        factoryBean.setConfigLocations(new Resource[]{resource});
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    public JdbcTemplateSupportDao jdbcTemplateSupportDao(JdbcBaseMycatProperties properties,
                                                         ObjectProvider<ShardingRouter> shardingRouterProvider) {
        JdbcTemplateSupportDao dao = new JdbcTemplateSupportDao();
        dao.setDefaultDataSourceId("dataSource");
        dao.setMultTaskMode(false);
        ShardingRouter router = shardingRouterProvider.getIfAvailable();
        if (router != null && !"dm".equalsIgnoreCase(properties.getDbType())) {
            dao.setShardingRouter(router);
        }
        return dao;
    }
}
