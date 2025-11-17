package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: BooleanTypeHandler
 * @Description: java.lang.Boolean与jdbc boolean的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:50:33
 * @version V1.0
 * 
 */
public class BooleanTypeHandler extends AbstractTypeHandler<Boolean> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
		ps.setBoolean(i, parameter);
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getBoolean(columnIndex);
	}
}
