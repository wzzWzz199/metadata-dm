package com.hayden.hap.dbop.db.orm.sql;

import com.hayden.hap.common.utils.ModuleDataSrcUtils;

/**
 * 获取sqlBuilder工厂
 * @author zhangfeng
 * @date 2017年7月7日
 */
public class SqlBuilderFactory {
	
	public static SqlBuilder createSqlBuilder() {
		String dbType = ModuleDataSrcUtils.getDbType();
		return new SqlBuilderManager().getSqlBuilder(dbType);
	}
}
