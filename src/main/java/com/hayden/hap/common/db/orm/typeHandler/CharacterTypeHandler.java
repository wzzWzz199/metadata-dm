package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: CharacterTypeHandler
 * @Description: java.lang.Character的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:56:58
 * @version V1.0
 * 
 */
public class CharacterTypeHandler extends AbstractTypeHandler<Character> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Character parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.toString());
	}

	@Override
	public Character getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String columnValue = rs.getString(columnName);
		if (columnValue != null) {
			return columnValue.charAt(0);
		} else {
			return null;
		}
	}

	@Override
	public Character getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String columnValue = rs.getString(columnIndex);
		if (columnValue != null) {
			return columnValue.charAt(0);
		} else {
			return null;
		}
	}

	@Override
	public Character getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String columnValue = cs.getString(columnIndex);
		if (columnValue != null) {
			return columnValue.charAt(0);
		} else {
			return null;
		}
	}
}
