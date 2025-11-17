package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: ByteTypeHandler
 * @Description: java.lang.Byte的类型处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:56:05
 * @version V1.0
 * 
 */
public class ByteTypeHandler extends AbstractTypeHandler<Byte> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Byte parameter, JdbcType jdbcType) throws SQLException {
		ps.setByte(i, parameter);
	}

	@Override
	public Byte getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	@Override
	public Byte getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getByte(columnIndex);
	}

	@Override
	public Byte getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getByte(columnIndex);
	}
}
