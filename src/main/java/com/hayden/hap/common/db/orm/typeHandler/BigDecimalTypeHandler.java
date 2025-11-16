package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: BigDecimalTypeHandler
 * @Description: java.math.BigDecimal的处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:43:24
 * @version V1.0
 * 
 */
public class BigDecimalTypeHandler extends AbstractTypeHandler<BigDecimal> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, BigDecimal parameter, JdbcType jdbcType) throws SQLException {
		ps.setBigDecimal(i, parameter);
	}

	@Override
	public BigDecimal getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

	@Override
	public BigDecimal getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getBigDecimal(columnIndex);
	}
}
