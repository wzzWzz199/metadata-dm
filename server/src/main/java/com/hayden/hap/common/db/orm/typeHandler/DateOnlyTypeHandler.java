package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * 
 * @ClassName: DateOnlyTypeHandler
 * @Description: java.util.Date与jdbc Date的转化处理器(只有日期的Date)
 * @author LUYANYING
 * @date 2015年4月17日 下午5:03:35
 * @version V1.0
 * 
 */
public class DateOnlyTypeHandler extends AbstractTypeHandler<Date> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
		ps.setDate(i, new java.sql.Date((parameter.getTime())));
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
		java.sql.Date sqlDate = rs.getDate(columnName);
		if (sqlDate != null) {
			return new java.util.Date(sqlDate.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		java.sql.Date sqlDate = rs.getDate(columnIndex);
		if (sqlDate != null) {
			return new java.util.Date(sqlDate.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		java.sql.Date sqlDate = cs.getDate(columnIndex);
		if (sqlDate != null) {
			return new java.util.Date(sqlDate.getTime());
		}
		return null;
	}

}
