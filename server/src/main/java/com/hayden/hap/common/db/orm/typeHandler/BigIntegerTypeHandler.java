package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: BigIntegerTypeHandler 
 * @Description: java.math.BigInteger 的处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:44:30 
 * @version V1.0   
 *
 */
public class BigIntegerTypeHandler extends AbstractTypeHandler<BigInteger> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, BigInteger parameter, JdbcType jdbcType) throws SQLException {
    ps.setBigDecimal(i, new BigDecimal(parameter));
  }

  @Override
  public BigInteger getNullableResult(ResultSet rs, String columnName) throws SQLException {
    BigDecimal bigDecimal = rs.getBigDecimal(columnName);
    return bigDecimal == null ? null : bigDecimal.toBigInteger();
  }

  @Override
  public BigInteger getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    BigDecimal bigDecimal = rs.getBigDecimal(columnIndex);
    return bigDecimal == null ? null : bigDecimal.toBigInteger();
  }

  @Override
  public BigInteger getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    BigDecimal bigDecimal = cs.getBigDecimal(columnIndex);
    return bigDecimal == null ? null : bigDecimal.toBigInteger();
  }
}
