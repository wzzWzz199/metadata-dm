package com.hayden.hap.common.db.orm.jdbc;

import com.hayden.hap.common.db.sharding.dataSource.DataSourceManager;
import com.hayden.hap.common.db.sharding.dataSource.Shard;
import com.hayden.hap.common.db.util.ObjectUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class JdbcTemplateManager {
    private JdbcTemplate defaultJdbcTemplate = null;
    private Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<String, JdbcTemplate>();
    private DataSourceManager dataSourceManager = null;

    public JdbcTemplateManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public void init(Map<String, DataSource> dataSources) {
        ObjectUtil.validNotNull(dataSourceManager, "jdbcTemplateManager config error: dataSourceManager is required.");
        ObjectUtil.validIsTrue(!ObjectUtil.isNotEmpty(dataSources), "can't find any dataSource.");
        this.init(dataSources);
    }

    private void init(Map<String, DataSource> dataSources, boolean multTaskMode, Map<String, Shard> configShards) {
        Map<String, Shard> shards = new HashMap<String, Shard>();
        Iterator<String> iterator = dataSources.keySet().iterator();
        while (iterator.hasNext()) {
            String dataSourceId = iterator.next();
            DataSource dataSource = dataSources.get(dataSourceId);
            jdbcTemplateMap.put(dataSourceId, new JdbcTemplate(dataSource));
        }
        ObjectUtil.validNotNull(dataSourceManager.getDefaultDataSource(), "jdbcTemplateManager config error: defaultDataSourceId isn't in dataSources.");
        dataSourceManager.setShards(shards);
    }


    public JdbcTemplate getJdbcTemplate(String dataSourceId) {
        if (!ObjectUtil.isNotNull(dataSourceId) || !jdbcTemplateMap.containsKey(dataSourceId))
            return null;
        return jdbcTemplateMap.get(dataSourceId);
    }

    public JdbcTemplate getDefaultJdbcTemplate() {
        return defaultJdbcTemplate;
    }

    public void setDefaultJdbcTemplate(JdbcTemplate defaultJdbcTemplate) {
        this.defaultJdbcTemplate = defaultJdbcTemplate;
    }

    public Map<String, JdbcTemplate> getJdbcTemplateMap() {
        return jdbcTemplateMap;
    }

    public void setJdbcTemplateMap(Map<String, JdbcTemplate> jdbcTemplateMap) {
        this.jdbcTemplateMap = jdbcTemplateMap;
    }

    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }


}
