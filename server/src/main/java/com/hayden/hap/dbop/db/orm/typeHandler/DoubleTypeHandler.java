package com.hayden.hap.dbop.db.orm.typeHandler;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: DoubleTypeHandler 
 * @Description: java.lang.Double与jdbc Double的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:06:23 
 * @version V1.0   
 *
 */
public class DoubleTypeHandler extends AbstractTypeHandler<Double> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Double parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setDouble(i, parameter);
  }

  @Override
  public Double getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    return rs.getDouble(columnName);
  }

  @Override
  public Double getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    return rs.getDouble(columnIndex);
  }

  @Override
  public Double getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return cs.getDouble(columnIndex);
  }

}
