package com.hayden.hap.common.db.orm.jdbc;

import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.orm.typeHandler.TypeHandler;
import com.hayden.hap.common.db.orm.typeHandler.TypeHandlerRegistry;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class VOBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
	private List<Object> params = null;
	private List<Integer> paramJdbcTypes = null;
	private TypeHandlerRegistry typeHandlerRegistry = null;
	private int batchSize = 0;
	private JdbcType jdbcTypeForNull = JdbcType.OTHER;
	
	public VOBatchPreparedStatementSetter(List<Object> params, List<Integer> paramJdbcTypes, TypeHandlerRegistry typeHandlerRegistry, int batchSize){
		this.params = params;
		this.paramJdbcTypes = paramJdbcTypes;
		this.typeHandlerRegistry = typeHandlerRegistry;
		this.batchSize = batchSize;
	}

	@Override
	public void setValues(PreparedStatement ps, int index) throws SQLException {
		if(this.params == null || params.isEmpty())
			return ;
		int colCount = params.size()/batchSize;
		for(int i=index*colCount;i<(index+1)*colCount;i++){
			Object paramObject = params.get(i);
			TypeHandler handler = null;
			JdbcType jdbcType = null;
			int prepareParamIndex = i%colCount;
			if(paramJdbcTypes != null && paramJdbcTypes.get(prepareParamIndex) != null){
				jdbcType = JdbcType.forCode(paramJdbcTypes.get(prepareParamIndex).intValue());
			}
			if(paramObject == null){
				handler = typeHandlerRegistry.getTypeHandler(Object.class);
				if(jdbcType == null)
					jdbcType = jdbcTypeForNull;
			}else if(jdbcType != null)
				handler = typeHandlerRegistry.getTypeHandler(paramObject.getClass(), jdbcType);
			else
				handler = typeHandlerRegistry.getTypeHandler(paramObject.getClass());
			handler.setParameter(ps, prepareParamIndex+1, paramObject, jdbcType);
		}
	}

	@Override
	public int getBatchSize() {
		return batchSize;
	}

}
