package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: ByteObjectArrayTypeHandler
 * @Description: Byte[]的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:54:58
 * @version V1.0
 * 
 */
public class ByteObjectArrayTypeHandler extends AbstractTypeHandler<Byte[]> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Byte[] parameter, JdbcType jdbcType) throws SQLException {
		ps.setBytes(i, ByteArrayUtil.convertToPrimitiveArray(parameter));
	}

	@Override
	public Byte[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		byte[] bytes = rs.getBytes(columnName);
		return getBytes(bytes);
	}

	@Override
	public Byte[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		byte[] bytes = rs.getBytes(columnIndex);
		return getBytes(bytes);
	}

	@Override
	public Byte[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		byte[] bytes = cs.getBytes(columnIndex);
		return getBytes(bytes);
	}

	private Byte[] getBytes(byte[] bytes) {
		Byte[] returnValue = null;
		if (bytes != null) {
			returnValue = ByteArrayUtil.convertToObjectArray(bytes);
		}
		return returnValue;
	}

}
