package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: ObjectTypeHandler
 * @Description: java.lang.Object与jdbc的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:26:12
 * @version V1.0
 * 
 */
public class ObjectTypeHandler extends AbstractTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, parameter);
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getObject(columnName);
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getObject(columnIndex);
	}
}
