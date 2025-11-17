package com.hayden.hap.common.utils.table.impl;

import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.utils.table.ClassToTableUtil;
import com.hayden.hap.common.utils.table.JDBCutil;
import com.hayden.hap.common.utils.table.entity.ColumnDTO;
import com.hayden.hap.common.utils.table.itf.IClassToTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClassToTableForDB implements IClassToTable{

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClassToTableForDB.class);

	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#getTableName(java.lang.Class)
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	@Override
	public abstract String getTableName(Class<? extends BaseVO> clazz) ;

	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#getColumns(java.lang.Class)
	 * @author zhangfeng
	 * @throws Exception 
	 * @date 2015年11月19日
	 */
	@Override
	public List<ColumnDTO> getColumns(Class<? extends BaseVO> clazz) throws Exception {
		String tableName = getTableName(clazz);
		if(tableName==null || tableName.length()==0) {
			throw new Exception("类"+clazz.getName()+"没有定义表名");
		}
		
	    List<ColumnDTO> fields = new ArrayList<ColumnDTO>();
	    
	    Connection conn = JDBCutil.getConnection();	    
	    
	    String tableDefId = ClassToTableUtil.getTableDefId(tableName, conn);
	    
	    ResultSet rs = null;
	    ResultSet rs2 = null;
	    try {
	      DatabaseMetaData metaData = conn.getMetaData();
	      rs = metaData.getColumns(conn.getCatalog(), metaData.getUserName(), tableName, null);
	      while (rs.next()) {
	        String columnName = rs.getString("COLUMN_NAME");
	        int dataType = rs.getInt("DATA_TYPE");
	        String typeName = rs.getString("TYPE_NAME");
	        int columnSize = rs.getInt("COLUMN_SIZE");

	        int nullable = rs.getInt("NULLABLE");

	        String columnDef = rs.getString("COLUMN_DEF");
	        ColumnDTO column = new ColumnDTO();
	        column.setName(columnName.toLowerCase());
//	        column.setDataType(dataType);
	        column.setType(typeName);
	        column.setLength(columnSize);
	        if("BIT".equals(column.getType()) && columnSize==0) {//
	        	column.setType("TINYINT");
	        	column.setLength(1);
	        }
	        column.setAllowNull(nullable != 0);
	        column.setDefaultStringValue(columnDef);
	        
	        column.setTableDefId(tableDefId);
	        fields.add(column);
	        
	        System.out.println(columnName+"*"+typeName+":"+dataType+",长度："+columnSize);
	      }

	      rs2 = metaData.getPrimaryKeys(conn.getCatalog(), metaData.getUserName(), tableName);
	      List<String> keys = new ArrayList<String>();
	      while (rs2.next()) {
	        keys.add(rs2.getString("COLUMN_NAME").toLowerCase());
	      }
	      for (int i = 0; i < fields.size(); i++) {
	    	  ColumnDTO f = (ColumnDTO)fields.get(i);
	        if (keys.contains(f.getName()))
	          f.setPK(true);
	      }
	    }catch(Exception e) {
	    	logger.error(e.getMessage(), e);
	    }finally {
	    	try {
		    	rs.close();
		    	rs2.close();
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    return fields;
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
		throw new Exception("数据库表/视图该是手动已创建好的，不能从这里创建");

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
		String tableName = getTableName(clazz);

		String desc = "";
		Connection conn = JDBCutil.getConnection();	    
		ResultSet rs = null;
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getTables(conn.getCatalog(), metaData.getUserName(), tableName, null);
			while (rs.next()) {
				desc = rs.getString("REMARKS");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		String sql = "insert into sy_table_def "
				+ "(tabledefid,table_code,modulecode,table_name,table_type,issqllog,table_desc,classname,"
				+ "created_by,created_dt,updated_by,updated_dt,ver,df) "
				+ "values(?,?,?,?,1,0,?,?,?,?,?,?,?,?)";
		logger.info("插入sy_table_def表sql："+sql);
		conn.setAutoCommit(true);
		int startIndex = ClassToTableUtil.getMaxIndexForTabledef(conn);
		PreparedStatement st = conn.prepareStatement(sql); 
		st.setInt(1,startIndex+1);
		st.setString(2, tableName);
		st.setString(3, "sy");
		st.setString(4, desc);
		st.setString(5, desc);
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
