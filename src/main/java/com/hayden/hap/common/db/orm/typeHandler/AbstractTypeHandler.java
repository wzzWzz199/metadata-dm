package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.util.ObjectUtil;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: AbstractTypeHandler
 * @Description: 类型处理基类
 * 类型处理器： 预处理语句（PreparedStatement）中设置一个参数时，还是从结果集中取出一个值时，都会用类型处理器将获取的值以合适的方式转换成 Java 类型
 * @author LUYANYING
 * @date 2015年4月17日 下午4:28:33
 * @version V1.0
 * 
 * @param <T>
 */
public abstract class AbstractTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {

	public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			if (jdbcType == null) {
				throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
			}
			try {
				ps.setNull(i, jdbcType.getTypeCode());
			} catch (SQLException e) {
				//再添加下判断，oracle适配时使用JdbcType.OTHER报错
				//改为使用JdbcType.NULL
				try {
					JdbcType jdbcTypeForNull = JdbcType.NULL;
					ps.setNull(i, jdbcTypeForNull.getTypeCode());
				} catch (Exception e1) {
					throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
							+ "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. " + "Cause: "
							+ e, e);
				}
			}
		} else {
			setNonNullParameter(ps, i, parameter, jdbcType);
		}
	}

	public T getResult(ResultSet rs, String columnName) throws SQLException {
		T result = getNullableResult(rs, columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			if(!ObjectUtil.isNotNull(result))
				return null;
			return result;
		}
	}

	public T getResult(ResultSet rs, int columnIndex) throws SQLException {
		T result = getNullableResult(rs, columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
		T result = getNullableResult(cs, columnIndex);
		if (cs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

	public abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

	public abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

	public abstract T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException;

}
