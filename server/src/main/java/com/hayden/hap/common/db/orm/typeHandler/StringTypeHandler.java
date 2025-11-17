package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.util.ObjectUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * 
 * @ClassName: StringTypeHandler
 * @Description: java.lang.String与jdbc的转化处理器
 * @author LUYANYING
 * @date 2015年4月17日 下午5:24:25
 * @version V1.0
 * 
 */
public class StringTypeHandler extends AbstractTypeHandler<String> {
	private static final Logger logger = LoggerFactory
			.getLogger(StringTypeHandler.class);
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		try {
			if(jdbcType!=null){
				if(jdbcType.getTypeCode() == Types.DATE){
					//这里从JdbcType的codeLookup取出，首先date和datetime name都映射到date的code，然后依据code取出jdbctype时之取出一种。datetime
					if("datetime".equalsIgnoreCase(jdbcType.getTypeName()) && 
							!parameter.trim().matches("\\d{4}-\\d{1,2}-\\d{1,2}")){
						java.util.Date date = DateUtils.parseDate(parameter.trim(), ObjectUtil.DEFAULT_DATE_FORMAT);
						ps.setTimestamp(i, new Timestamp(date.getTime()));
						return;
					}else{
						java.util.Date date = DateUtils.parseDate(parameter.trim(), ObjectUtil.DEFAULT_DATEONLY_FORMAT);
						ps.setDate(i, new java.sql.Date((date.getTime())));
						return;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		ps.setString(i, parameter);
	}

	@Override
	public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getNullableResult(rs,rs.findColumn(columnName));
	}

	@Override
	public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		try {
			String columnType = rs.getMetaData().getColumnTypeName(columnIndex);
			if("date".equalsIgnoreCase(columnType)){
				java.sql.Date sqlDate = rs.getDate(columnIndex);
				if (sqlDate != null) {
					java.util.Date tmpDate = new java.util.Date(sqlDate.getTime());
					return ObjectUtil.getDateOnlyFormatString(tmpDate);	 
				}
			}else if("datetime".equalsIgnoreCase(columnType)){
				Timestamp sqlTimestamp = rs.getTimestamp(columnIndex);
				if (sqlTimestamp != null) {
					java.util.Date tmpDate = new java.util.Date(sqlTimestamp.getTime());
					return ObjectUtil.getDateFormatString(tmpDate);			
				}
			}	
		} catch (Exception e) {
			logger.error(e.getMessage());
		}			
		return rs.getString(columnIndex);
	}
	
	@Override
	public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getString(columnIndex);
	}
}
