package com.hayden.hap.dbop.db.orm.entity;

import com.alibaba.druid.sql.ast.SQLExpr;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * sql的解析结果，包括表名、列值的映射
 * @author wangyi
 * @date 2018年1月2日
 */
@Data
public class SqlParseVO implements Serializable{
	private String tableName;
	private Map<String, SQLExpr> colValMap;
}