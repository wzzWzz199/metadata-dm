package com.hayden.hap.dbop.db.orm.jdbc;

import com.hayden.hap.dbop.db.orm.sql.JdbcType;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandler;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandlerRegistry;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class VOBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
	private final List<Object> params;
	private final List<Integer> paramJdbcTypes;
	private final TypeHandlerRegistry typeHandlerRegistry;
	private final int batchSize;
	private final JdbcType jdbcTypeForNull = JdbcType.OTHER;
	
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
