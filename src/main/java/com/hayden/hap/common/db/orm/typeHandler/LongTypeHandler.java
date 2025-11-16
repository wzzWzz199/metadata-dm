package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: LongTypeHandler
 * @Description: java.lang.Long与jdbc 转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:14:10
 * @version V1.0
 * 
 */
public class LongTypeHandler extends AbstractTypeHandler<Long> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
		ps.setLong(i, parameter);
	}

	@Override
	public Long getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	@Override
	public Long getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	@Override
	public Long getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getLong(columnIndex);
	}
}
