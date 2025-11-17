package com.hayden.hap.common.db.sharding.dataSource;

import lombok.Data;

import javax.sql.DataSource;
import java.util.Map;


@Data
public class DataSourceManager {
    private Map<String, DataSource> dataSourceMap = null;
    private String defaultDataSourceId = null;
    private DataSource defaultDataSource = null;


    public DataSource getDataSource(String defaultDataSourceId) {
        return dataSourceMap.get(defaultDataSourceId);
    }
}
