package com.hayden.hap.db.dataSource.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 9:55
 */
public class SqlResultVO {
    private List<String> preStatementCols=new ArrayList<>();
    private List<Integer> preStatementParamJdbcTypes=new ArrayList<>();
    private String sql;

    public List<String> getPreStatementCols() {
        return preStatementCols;
    }

    public void setPreStatementCols(List<String> preStatementCols) {
        this.preStatementCols = preStatementCols;
    }

    public List<Integer> getPreStatementParamJdbcTypes() {
        return preStatementParamJdbcTypes;
    }

    public void setPreStatementParamJdbcTypes(List<Integer> preStatementParamJdbcTypes) {
        this.preStatementParamJdbcTypes = preStatementParamJdbcTypes;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
