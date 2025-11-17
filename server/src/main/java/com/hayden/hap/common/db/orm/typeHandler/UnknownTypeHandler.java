package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.util.ResourceUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: UnknownTypeHandler
 * @Description: 未知类型处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:36:28
 * @version V1.0
 * 
 */
public class UnknownTypeHandler extends AbstractTypeHandler<Object> {

	private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();

	private TypeHandlerRegistry typeHandlerRegistry;

	public UnknownTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
		handler.setParameter(ps, i, parameter, jdbcType);
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		TypeHandler<?> handler = resolveTypeHandler(rs, columnName);
		return handler.getResult(rs, columnName);
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		TypeHandler<?> handler = resolveTypeHandler(rs.getMetaData(), columnIndex);
		return handler.getResult(rs, columnIndex);
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getObject(columnIndex);
	}

	/**
	 * 
	 * @Title: resolveTypeHandler 
	 * @Description: 按照参数和jdbcType返回具体TypeHandler 如果参数为空或找不到具体TypeHandler则返回默认的ObjectTypeHandler
	 * @param parameter
	 * @param jdbcType
	 * @return
	 * @return TypeHandler<? extends Object>
	 * @throws
	 */
	private TypeHandler<? extends Object> resolveTypeHandler(Object parameter, JdbcType jdbcType) {
		TypeHandler<? extends Object> handler;
		if (parameter == null) {
			handler = OBJECT_TYPE_HANDLER;
		} else {
			handler = typeHandlerRegistry.getTypeHandler(parameter.getClass(), jdbcType);
			
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = OBJECT_TYPE_HANDLER;
			}
		}
		return handler;
	}

	/**
	 * 
	 * @Title: resolveTypeHandler 
	 * @Description: 按照字段元数据返回具体TypeHandler实例，如果确定不了具体TypeHandler则使用默认的ObjectTypeHandler
	 * @param rs
	 * @param column
	 * @return
	 * @return TypeHandler<?>
	 * @throws
	 */
	private TypeHandler<?> resolveTypeHandler(ResultSet rs, String column) {
		try {
			Map<String, Integer> columnIndexLookup;
			columnIndexLookup = new HashMap<String, Integer>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			for (int i = 1; i <= count; i++) {
				String name = rsmd.getColumnName(i);
				columnIndexLookup.put(name, i);
			}
			Integer columnIndex = columnIndexLookup.get(column);
			TypeHandler<?> handler = null;
			if (columnIndex != null) {
				handler = resolveTypeHandler(rsmd, columnIndex);
			}
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = OBJECT_TYPE_HANDLER;
			}
			return handler;
		} catch (SQLException e) {
			throw new TypeException("Error determining JDBC type for column " + column + ".  Cause: " + e, e);
		}
	}

	/**
	 * 
	 * @Title: resolveTypeHandler 
	 * @Description: 按照字段元数据返回具体TypeHandler实例，如果确定不了具体TypeHandler则使用默认的ObjectTypeHandler
	 * @param rsmd
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 * @return TypeHandler<?>
	 * @throws
	 */
	private TypeHandler<?> resolveTypeHandler(ResultSetMetaData rsmd, Integer columnIndex) throws SQLException {
		TypeHandler<?> handler = null;
		JdbcType jdbcType = safeGetJdbcTypeForColumn(rsmd, columnIndex);
		Class<?> javaType = safeGetClassForColumn(rsmd, columnIndex);
		if (javaType != null && jdbcType != null) {
			handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
		} else if (javaType != null) {
			handler = typeHandlerRegistry.getTypeHandler(javaType);
		} else if (jdbcType != null) {
			handler = typeHandlerRegistry.getTypeHandler(jdbcType);
		}
		if (handler == null || handler instanceof UnknownTypeHandler) {
			handler = OBJECT_TYPE_HANDLER;
		}
		return handler;
	}

	/**
	 * 
	 * @Title: safeGetJdbcTypeForColumn 
	 * @Description: 根据字段元数据返回对应的jdbcType
	 * @param rsmd
	 * @param columnIndex
	 * @return
	 * @return JdbcType
	 * @throws
	 */
	private JdbcType safeGetJdbcTypeForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
		try {
			return JdbcType.forCode(rsmd.getColumnType(columnIndex));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @Title: safeGetClassForColumn 
	 * @Description: 根据字段元数据的类名返回对应的Class
	 * @param rsmd
	 * @param columnIndex
	 * @return
	 * @return Class<?>
	 * @throws
	 */
	private Class<?> safeGetClassForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
		try {
			return ResourceUtil.classForName(rsmd.getColumnClassName(columnIndex));
		} catch (Exception e) {
			return null;
		}
	}
}
