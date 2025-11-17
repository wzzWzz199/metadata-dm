package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: ShortTypeHandler
 * @Description: java.lang.Short与jdbc转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:21:02
 * @version V1.0
 * 
 */
public class ShortTypeHandler extends AbstractTypeHandler<Short> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Short parameter, JdbcType jdbcType) throws SQLException {
		ps.setShort(i, parameter);
	}

	@Override
	public Short getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getShort(columnName);
	}

	@Override
	public Short getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	@Override
	public Short getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getShort(columnIndex);
	}
}
