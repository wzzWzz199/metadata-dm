package com.hayden.hap.common.db.orm.jdbc;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.CommonVO;
import com.hayden.hap.common.db.orm.exception.ResultSetRowMapperException;
import com.hayden.hap.common.db.orm.typeHandler.TypeHandler;
import com.hayden.hap.common.db.orm.typeHandler.TypeHandlerRegistry;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.ReflectUtil;
import com.hayden.hap.common.db.util.ResourceUtil;
import com.hayden.hap.common.reflect.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: VORowMapperResultSetExtractor
 * @Description: ResultSet处理类，用于结果集行转化为实体类对象
 * @author LUYANYING
 * @date 2015年4月22日 上午10:44:12
 * @version V1.0
 * 
 */
public class VORowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

	private final int rowsExpected;

	private Class<?> entityClass = null;
	
	private ClassInfo classInfo = null;

	private TableDefVO tableDefVO = null;

	private TypeHandlerRegistry typeHandlerRegistry = null;
	
	private static final Logger logger = LoggerFactory
			.getLogger(VORowMapperResultSetExtractor.class);

	public VORowMapperResultSetExtractor(TableDefVO tableDefVO, TypeHandlerRegistry typeHandlerRegistry) {
		this(tableDefVO, typeHandlerRegistry, 0);
	}
	public VORowMapperResultSetExtractor(Class<?> entityClass, TableDefVO tableDefVO, TypeHandlerRegistry typeHandlerRegistry) {
		this(entityClass, tableDefVO, typeHandlerRegistry, 0);
	}
	public VORowMapperResultSetExtractor(Class<?> entityClass, TableDefVO tableDefVO, TypeHandlerRegistry typeHandlerRegistry, int rowsExpected) {
		this.entityClass = entityClass;
		if (this.entityClass == null)
			this.entityClass = ReflectUtil.getSuperClassGenricType(getClass());
		try {
			//获取表定义设置的实体类
			//如果当前工程没有引用相关的VO类，创建一个basevo对象，不抛出ClassNotFoundException异常
			if (this.entityClass == null && tableDefVO != null)
				this.entityClass = ResourceUtil.classForName(tableDefVO.getClassname());
		} catch (ClassNotFoundException e) {
			logger.info(tableDefVO.getClassname()+"，类不存在或者当前工程没有引用！将使用BaseVO类");
			this.entityClass = BaseVO.class;
		}
		if(this.entityClass != null)
			classInfo = ClassInfo.forClass(this.entityClass);
		this.rowsExpected = rowsExpected;
		this.tableDefVO = tableDefVO;
		this.typeHandlerRegistry = typeHandlerRegistry;
	}
	public VORowMapperResultSetExtractor(TableDefVO tableDefVO, TypeHandlerRegistry typeHandlerRegistry, int rowsExpected) {
		this(null, tableDefVO, typeHandlerRegistry, rowsExpected);
	}

	@Override
	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = (this.rowsExpected > 0 ? new ArrayList<T>(this.rowsExpected) : new ArrayList<T>());

		int rowNum = 0;
		if (entityClass == null) {
			this.entityClass = BaseVO.class;
		}
		ResultSetWrapper resultSetWrapper = new ResultSetWrapper(rs, typeHandlerRegistry);
		while (rs.next()) {
			results.add(mapRow(resultSetWrapper, rowNum++));
		}
		return results;
	}

	/**
	 * 
	 * @Title: mapRow 
	 * @Description: 处理rs，把每行转化成实体类对象，如果不知道具体实体类，则使用BaseVO
	 * @param resultSetWrapper
	 * @param rowNum
	 * @return 
	 * @throws SQLException
	 * @return T 返回具体实体(或BaseVO)对象
	 * @throws
	 */
	public T mapRow(ResultSetWrapper resultSetWrapper, int rowNum) throws SQLException {
		try {
			final int columnCount = resultSetWrapper.getColumnNames().size();
			if (entityClass == CommonVO.class) {
				CommonVO vo = (CommonVO) entityClass.newInstance();
				vo.setTableName(tableDefVO != null?tableDefVO.getTable_code() : null);
				for (int i = 0; i < columnCount; i++) {
					String metaColName = resultSetWrapper.getColumnNames().get(i);
					final TypeHandler<?> typeHandler = resultSetWrapper.getTypeHandler(Object.class, metaColName);
					final Object value = typeHandler.getResult(resultSetWrapper.getResultSet(), metaColName);
					vo.set(metaColName, value);
				}
				return (T) vo;
			}
			if (entityClass == BaseVO.class) {
				BaseVO vo = (BaseVO) entityClass.newInstance();
				vo.setTableName(tableDefVO != null?tableDefVO.getTable_code() : null);
				for (int i = 0; i < columnCount; i++) {
					String metaColName = resultSetWrapper.getColumnNames().get(i);
					final TypeHandler<?> typeHandler = resultSetWrapper.getTypeHandler(Object.class, metaColName);
					final Object value = typeHandler.getResult(resultSetWrapper.getResultSet(), metaColName);
					vo.set(metaColName, value);
				}
				return (T) vo;
			}
			
			Object entity = ClassInfo.newInstance(entityClass);
			for (int i = 0; i < columnCount; i++) {
				String metaColName = resultSetWrapper.getColumnNames().get(i);
				String propertyName = classInfo.findPropertyName(metaColName.toLowerCase());
				if (classInfo.hasSetter(propertyName)) {
					Class<?> propertyType = classInfo.getSetterType(propertyName);
					if(!typeHandlerRegistry.hasTypeHandler(propertyType))
						continue ;
					final TypeHandler<?> typeHandler = resultSetWrapper.getTypeHandler(propertyType, metaColName);
					final Object value = typeHandler.getResult(resultSetWrapper.getResultSet(), metaColName);
					if (value != null || !propertyType.isPrimitive())
						classInfo.getSetInvoker(propertyName).invoke(entity, new Object[] { value });
				}else {
					final TypeHandler<?> typeHandler = resultSetWrapper.getTypeHandler(Object.class, metaColName);
					final Object value = typeHandler.getResult(resultSetWrapper.getResultSet(), metaColName);
					((AbstractVO)entity).set(metaColName, value);
				}
			}
			return (T) entity;
		} catch (InstantiationException e) {
			throw new ResultSetRowMapperException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ResultSetRowMapperException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ResultSetRowMapperException(e.getMessage(), e);
		}
	}
}