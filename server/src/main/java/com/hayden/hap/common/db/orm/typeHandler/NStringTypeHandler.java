package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: NStringTypeHandler
 * @Description: java.lang.String与jdbc nstring的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:19:35
 * @version V1.0
 * 
 */
public class NStringTypeHandler extends AbstractTypeHandler<String> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		// ps.setNString(i, ((String) parameter));
		ps.setString(i, parameter);
	}

	@Override
	public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
		// return rs.getNString(columnName);
		return rs.getString(columnName);
	}

	@Override
	public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		// return cs.getNString(columnIndex);
		return cs.getString(columnIndex);
	}

}