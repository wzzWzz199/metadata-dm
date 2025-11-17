package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.*;
import java.util.Date;

/**
 * 
 * @ClassName: TimeOnlyTypeHandler
 * @Description: java.util.Date与jdbc Time的转化处理器(只有时间的Time)
 * @author LUYANYING
 * @date 2015年4月17日 下午5:25:15
 * @version V1.0
 * 
 */
public class TimeOnlyTypeHandler extends AbstractTypeHandler<Date> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
		ps.setTime(i, new Time(parameter.getTime()));
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
		java.sql.Time sqlTime = rs.getTime(columnName);
		if (sqlTime != null) {
			return new Date(sqlTime.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		java.sql.Time sqlTime = rs.getTime(columnIndex);
		if (sqlTime != null) {
			return new Date(sqlTime.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		java.sql.Time sqlTime = cs.getTime(columnIndex);
		if (sqlTime != null) {
			return new Date(sqlTime.getTime());
		}
		return null;
	}
}
