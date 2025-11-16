package com.hayden.hap.common.utils.table.impl;

import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.utils.table.ClassToTableUtil;
import com.hayden.hap.common.utils.table.JDBCutil;
import com.hayden.hap.common.utils.table.annotation.Column;
import com.hayden.hap.common.utils.table.annotation.Table;
import com.hayden.hap.common.utils.table.entity.ColumnDTO;
import com.hayden.hap.common.utils.table.itf.IClassToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月19日
 */
public class ClassToTableForAnnotation implements IClassToTable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClassToTableForAnnotation.class);

	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#getTableName(java.lang.Class)
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	@Override
	public String getTableName(Class<? extends BaseVO> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		logger.debug("根据类"+clazz.getName()+"获得表名："+table.value());
		return table.value();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#getColumns(java.lang.Class)
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	@Override
	public List<ColumnDTO> getColumns(Class<? extends BaseVO> clazz) {
		List<ColumnDTO> list = new ArrayList<ColumnDTO>();
		for(Field field : clazz.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if(column!=null) {
				ColumnDTO dto = new ColumnDTO();
				dto.setName(field.getName().toLowerCase());
				dto.setType(column.type().toUpperCase());
				dto.setLength(column.length());
				dto.setAllowNull(column.allowNull());
				dto.setPK(column.isPK());
				if(column.defaultIntValue()!=-100) {
					dto.setDefaultIntValue(column.defaultIntValue());
				}
				if(!"".equals(column.defaultStringValue())) {
					dto.setDefaultStringValue(column.defaultStringValue());
				}
				list.add(dto);
			}
		}
		if(!clazz.equals(BaseVO.class)) {
			Class supClass = clazz.getSuperclass();
			list.addAll(getColumns(supClass));
		}
		
		return list;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#createTableForMysql(java.lang.Class)
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	@Override
	public void createTableForMysql(Class<? extends BaseVO> clazz)
			throws Exception {
		String tableName = getTableName(clazz);
		List<ColumnDTO> list = getColumns(clazz);
		createTableForMysql(tableName, list);
	}
	
	private void createTableForMysql(String tableName,List<ColumnDTO> columns) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("create table IF NOT EXISTS ");
		sb.append(tableName);
		sb.append(" (");
		
		for(ColumnDTO column:columns) {
			sb.append(column.getName());
			sb.append(" ");
			sb.append(column.getType());
			sb.append("(");
			sb.append(column.getLength());
			sb.append(")");
			
			if(!column.isAllowNull()) {
				sb.append(" NOT NULL ");
			}
			
			if(column.getDefaultIntValue()!=null && column.getDefaultStringValue()!=null) {
				throw new Exception(column.getName()+"字段：整形默认值和字符型默认值不能同时出现");
			}else if(column.getDefaultIntValue()!=null) {
				sb.append(" default ");
				sb.append(column.getDefaultIntValue());
			}else if(column.getDefaultStringValue()!=null) {
				sb.append(" default '");
				sb.append(column.getDefaultStringValue());
				sb.append("'");
			}
			
			sb.append(",");
		}
		
		boolean isAssignPK = false;
		for(ColumnDTO column:columns) {
			if(column.isPK()) {
				sb.append("PRIMARY KEY (");
				sb.append(column.getName());
				sb.append(")");
				isAssignPK = true;
			}
		}
		if(!isAssignPK) {
			throw new Exception("没有定义主键");
		}
			
		sb.append(" )");
		String sql = sb.toString();
		logger.info("建表语句："+sql);
		Connection conn = JDBCutil.getConnection();
		conn.setAutoCommit(true);
		execute(sql, conn);
		JDBCutil.closeConn(conn);
	}

	private void execute(String sql,Connection conn) {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#insertColumnTable(java.lang.Class)
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	@Override
	public void insertColumnTable(Class<? extends BaseVO> clazz)
			throws Exception {
		String tableName = getTableName(clazz);
		List<ColumnDTO> list = getColumns(clazz);
		ClassToTableUtil.insertColumnTable(tableName, list);

	}

	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#insertTableDef(java.lang.Class)
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	@Override
	public void insertTableDef(Class<? extends BaseVO> clazz) throws Exception {
		Table table = clazz.getAnnotation(Table.class);
		String sql = "insert into sy_table_def "
				+ "(tabledefid,table_code,modulecode,tbtitle,tbtype,issqllog,tbdesc,classname,"
				+ "created_by,created_dt,updated_by,updated_dt,ver,df) "
				+ "values(?,?,?,?,1,0,?,?,?,?,?,?,?,?)";
		logger.info("插入sy_table_def表sql："+sql);
		Connection conn = JDBCutil.getConnection();
		conn.setAutoCommit(true);
		int startIndex = ClassToTableUtil.getMaxIndexForTabledef(conn);
		PreparedStatement st = conn.prepareStatement(sql); 
		st.setInt(1,startIndex+1);
		st.setString(2, table.value());
		st.setString(3, "sy");
		st.setString(4, table.desc());
		st.setString(5, table.desc());
		st.setString(6, clazz.getName());
		st.setString(7, "1");
		st.setDate(8, new Date(new java.util.Date().getTime()));
		st.setString(9,"1");
		st.setDate(10, new Date(new java.util.Date().getTime()));
		st.setInt(11, 1);
		st.setInt(12, 0);
		st.execute();
		st.close();
		JDBCutil.closeConn(conn);

	}

}
