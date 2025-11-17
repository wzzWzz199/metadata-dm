package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.io.ByteArrayInputStream;
import java.sql.*;

/**
 * 
 * @ClassName: BlobTypeHandler
 * @Description: byte[]与Blob的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:48:37
 * @version V1.0
 * 
 */
public class BlobTypeHandler extends AbstractTypeHandler<byte[]> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, byte[] parameter, JdbcType jdbcType) throws SQLException {
		ByteArrayInputStream bis = new ByteArrayInputStream(parameter);
		ps.setBinaryStream(i, bis, parameter.length);
	}

	@Override
	public byte[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Blob blob = rs.getBlob(columnName);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}

	@Override
	public byte[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Blob blob = rs.getBlob(columnIndex);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}

	@Override
	public byte[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Blob blob = cs.getBlob(columnIndex);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}
}