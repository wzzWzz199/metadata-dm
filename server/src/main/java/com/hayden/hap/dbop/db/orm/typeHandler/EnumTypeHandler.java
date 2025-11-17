package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: EnumTypeHandler
 * @Description: java.lang.Enum与jdbc的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:34:24
 * @version V1.0
 * 
 * @param <E>
 */
public class EnumTypeHandler<E extends Enum<E>> extends AbstractTypeHandler<E> {

	private Class<E> type;

	public EnumTypeHandler(Class<E> type) {
		if (type == null)
			throw new IllegalArgumentException("Type argument cannot be null");
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		if (jdbcType == null) {
			ps.setString(i, parameter.name());
		} else {
			ps.setObject(i, parameter.name(), jdbcType.getTypeCode());
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return s == null ? null : Enum.valueOf(type, s);
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String s = rs.getString(columnIndex);
		return s == null ? null : Enum.valueOf(type, s);
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String s = cs.getString(columnIndex);
		return s == null ? null : Enum.valueOf(type, s);
	}
}