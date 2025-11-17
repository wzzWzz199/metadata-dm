package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: FloatTypeHandler
 * @Description: java.lang.Float与jdbc float的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:07:43
 * @version V1.0
 * 
 */
public class FloatTypeHandler extends AbstractTypeHandler<Float> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Float parameter, JdbcType jdbcType) throws SQLException {
		ps.setFloat(i, parameter);
	}

	@Override
	public Float getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	@Override
	public Float getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	@Override
	public Float getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getFloat(columnIndex);
	}
}
