package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.*;

/**
 * 
 * @ClassName: ArrayTypeHandler 
 * @Description: java.sql.Array与jdbc Array的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午4:26:49 
 * @version V1.0   
 *
 */
public class ArrayTypeHandler extends AbstractTypeHandler<Object> {

  public ArrayTypeHandler() {
    super();
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
    ps.setArray(i, (Array) parameter);
  }

  @Override
  public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Array array = rs.getArray(columnName);
    return array == null ? null : array.getArray();
  }

  @Override
  public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Array array = rs.getArray(columnIndex);
    return array == null ? null : array.getArray();
  }

  @Override
  public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Array array = cs.getArray(columnIndex);
    return array == null ? null : array.getArray();
  }

}
