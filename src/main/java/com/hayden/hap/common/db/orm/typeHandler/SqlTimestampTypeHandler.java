package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.sql.*;
import java.util.Date;

/**
 * 
 * @ClassName: SqlTimestampTypeHandler
 * @Description: java.sql.Timestamp与jdbc Timestamp的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:22:50
 * @version V1.0
 * 
 */
public class SqlTimestampTypeHandler extends AbstractTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setTimestamp(i, (Timestamp) parameter);
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getNullableResult(rs, rs.findColumn(columnName));
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		//获取列数据类型
		String columnTypeName = metaData.getColumnTypeName(columnIndex);
		//获取时间戳
		Timestamp timeStamp = rs.getTimestamp(columnIndex);
		//值不为空时，判断是否存在小数点后纳秒
		if(timeStamp != null){
			//不含纳秒，并且类型为datetime时，转为date类
			if(timeStamp.getNanos()==0 && columnTypeName.equalsIgnoreCase("datetime")){
				return new Date(timeStamp.getTime());
			}
		}
		return timeStamp;
		
	}

	@Override
	public Timestamp getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getTimestamp(columnIndex);
	}

	
}
