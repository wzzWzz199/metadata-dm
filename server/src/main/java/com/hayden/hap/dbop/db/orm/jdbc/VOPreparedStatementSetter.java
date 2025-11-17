package com.hayden.hap.dbop.db.orm.jdbc;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandler;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandlerRegistry;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/** 
 * @ClassName: VOPreparedStatementSetter 
 * @Description: 设置预编译参数 当参数为null时需提供参数对应的jdbcType，否则按jdbcTypeForNull指定的默认jdbctype进行处理
 * @author LUYANYING
 * @date 2015年4月22日 下午7:51:17 
 * @version V1.0   
 *  
 */
public class VOPreparedStatementSetter implements PreparedStatementSetter {
	private List<Object> params = null;
	private List<Integer> paramJdbcTypes = null;
	private TypeHandlerRegistry typeHandlerRegistry = null;
	private JdbcType jdbcTypeForNull = JdbcType.OTHER;
	
	public VOPreparedStatementSetter(List<Object> params, List<Integer> paramJdbcTypes, TypeHandlerRegistry typeHandlerRegistry){
		this.paramJdbcTypes = paramJdbcTypes;
		this.params = params;
		this.typeHandlerRegistry = typeHandlerRegistry;
	}
	/**
	 * 
	 * @Title: setValues 
	 * @Description: 设置预编译参数 当参数为null时需提供参数对应的jdbcType，否则按jdbcTypeForNull指定的默认jdbctype进行处理
	 * @param ps
	 * @throws SQLException
	 * @return void
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		if(params == null)
			return ;
		int paramsSize = params.size();
		for(int i=0;i<paramsSize;i++){
			Object paramObject = params.get(i);
			TypeHandler handler = null;
			JdbcType jdbcType = null;
			if(ObjectUtil.isNotEmpty(paramJdbcTypes) && paramJdbcTypes.get(i) != null){
				jdbcType = JdbcType.forCode(paramJdbcTypes.get(i).intValue());
			}
			if(paramObject == null){
				handler = typeHandlerRegistry.getTypeHandler(Object.class);
				if(jdbcType == null)
					jdbcType = jdbcTypeForNull;
			}else if(jdbcType != null)
				handler = typeHandlerRegistry.getTypeHandler(paramObject.getClass(), jdbcType);
			else
				handler = typeHandlerRegistry.getTypeHandler(paramObject.getClass());
			handler.setParameter(ps, i+1, paramObject, jdbcType);
		}
	}

}
