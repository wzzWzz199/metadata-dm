package com.hayden.hap.dbop.db.orm.jdbc;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;
import com.hayden.hap.dbop.db.orm.typeHandler.ObjectTypeHandler;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandler;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandlerRegistry;
import com.hayden.hap.dbop.db.orm.typeHandler.UnknownTypeHandler;
import com.hayden.hap.dbop.db.util.ResourceUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ResultSetWrapper
 * @Description:ResultSet包装类
 * @author LUYANYING
 * @date 2015年4月21日 下午1:29:42
 * @version V1.0
 * 
 */
public class ResultSetWrapper {
	private final ResultSet resultSet;
	private final TypeHandlerRegistry typeHandlerRegistry;
	private final List<String> columnNames = new ArrayList<String>();
	private final List<String> classNames = new ArrayList<String>();
	private final List<JdbcType> jdbcTypes = new ArrayList<JdbcType>();
//	private final Map<String, Map<Class<?>, TypeHandler<?>>> typeHandlerMap = new HashMap<String, Map<Class<?>, TypeHandler<?>>>();
	private final Map<String, TypeHandler<?>> typeHandlerMap = new HashMap<String, TypeHandler<?>>(); 
	
	public ResultSetWrapper(ResultSet rs, TypeHandlerRegistry typeHandlerRegistry) throws SQLException {
		this.typeHandlerRegistry = typeHandlerRegistry;
		this.resultSet = rs;
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			columnNames.add(metaData.getColumnName(i));
			if(metaData.getColumnTypeName(i).equalsIgnoreCase(JdbcType.TINYINT.getTypeName())&&metaData.getColumnType(i)==-7)
			{
				jdbcTypes.add(JdbcType.forCode(Types.TINYINT));
				classNames.add("java.lang.Integer");
			}else
			{
				jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
				classNames.add(metaData.getColumnClassName(i));
			}

		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public List<String> getColumnNames() {
		return this.columnNames;
	}

	/**
	 * 
	 * @Title: getTypeHandler 
	 * @Description: 获取ResultSet各个字段的TypeHandler，并通过typeHandlerMap缓存
	 * @param propertyType
	 * @param columnName
	 * @return
	 * @return TypeHandler<?>
	 * @throws
	 */
	public TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
		TypeHandler<?> handler = typeHandlerMap.get(columnName);
		if (handler == null) {
			handler = typeHandlerRegistry.getTypeHandler(propertyType);
			if (handler == null || handler instanceof UnknownTypeHandler) {
				final int index = columnNames.indexOf(columnName);
				final JdbcType jdbcType = jdbcTypes.get(index);
				final Class<?> javaType = resolveClass(classNames.get(index));
				if (javaType != null && jdbcType != null) {
					handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
				} else if (javaType != null) {
					handler = typeHandlerRegistry.getTypeHandler(javaType);
				} else if (jdbcType != null) {
					handler = typeHandlerRegistry.getTypeHandler(jdbcType);
				}
			}
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = new ObjectTypeHandler();
			}
		}
		typeHandlerMap.put(columnName, handler);
		return handler;
	}

	private Class<?> resolveClass(String className) {
		try {
			final Class<?> clazz = ResourceUtil.classForName(className);
			return clazz;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
