package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.*;

/**
 * 
 * @ClassName: SqlTimeTypeHandler
 * @Description: java.sql.Time与jdbc Time的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:23:37
 * @version V1.0
 * 
 */
public class SqlTimeTypeHandler extends AbstractTypeHandler<Time> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Time parameter, JdbcType jdbcType) throws SQLException {
		ps.setTime(i, parameter);
	}

	@Override
	public Time getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getTime(columnName);
	}

	@Override
	public Time getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTime(columnIndex);
	}

	@Override
	public Time getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getTime(columnIndex);
	}
}
