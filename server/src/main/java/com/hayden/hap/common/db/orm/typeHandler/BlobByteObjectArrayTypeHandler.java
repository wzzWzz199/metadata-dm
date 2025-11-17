package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.io.ByteArrayInputStream;
import java.sql.*;

/**
 * 
 * @ClassName: BlobByteObjectArrayTypeHandler
 * @Description: Byte[]与Blob的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:47:00
 * @version V1.0
 * 
 */
public class BlobByteObjectArrayTypeHandler extends AbstractTypeHandler<Byte[]> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Byte[] parameter, JdbcType jdbcType) throws SQLException {
		ByteArrayInputStream bis = new ByteArrayInputStream(ByteArrayUtil.convertToPrimitiveArray(parameter));
		ps.setBinaryStream(i, bis, parameter.length);
	}

	@Override
	public Byte[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Blob blob = rs.getBlob(columnName);
		return getBytes(blob);
	}

	@Override
	public Byte[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Blob blob = rs.getBlob(columnIndex);
		return getBytes(blob);
	}

	@Override
	public Byte[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Blob blob = cs.getBlob(columnIndex);
		return getBytes(blob);
	}

	private Byte[] getBytes(Blob blob) throws SQLException {
		Byte[] returnValue = null;
		if (blob != null) {
			returnValue = ByteArrayUtil.convertToObjectArray(blob.getBytes(1, (int) blob.length()));
		}
		return returnValue;
	}
}
