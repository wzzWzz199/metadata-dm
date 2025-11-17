package com.hayden.hap.dbop.db.util;

public interface SqlConvert {

    default String convert(String sql) {
        return sql;
    }

    default String convertDateCompare(String sql) {
        return sql;
    }
}
