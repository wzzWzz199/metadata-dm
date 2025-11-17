package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: IntegerTypeHandler
 * @Description: java.lang.Integer与jdbc integer的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:08:33
 * @version V1.0
 * 
 */
public class IntegerTypeHandler extends AbstractTypeHandler<Integer> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Integer parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter);
	}

	@Override
	public Integer getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	@Override
	public Integer getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public Integer getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getInt(columnIndex);
	}
}
