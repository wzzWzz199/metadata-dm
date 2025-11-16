package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.*;

/**
 * 
 * @ClassName: SqlDateTypeHandler
 * @Description: java.sql.Date与jdbc date的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:21:55
 * @version V1.0
 * 
 */
public class SqlDateTypeHandler extends AbstractTypeHandler<Date> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
		ps.setDate(i, parameter);
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getDate(columnIndex);
	}
}
