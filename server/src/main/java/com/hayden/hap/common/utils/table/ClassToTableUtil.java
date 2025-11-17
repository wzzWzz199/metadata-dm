package com.hayden.hap.common.utils.table;

import com.hayden.hap.common.utils.table.entity.ColumnDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class ClassToTableUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClassToTableUtil.class);
	
	public static int getMaxIndexForColumntable(Connection conn) throws SQLException {
		String sql = "select max(tablecolumnid) as maxid from sy_table_column";
		logger.info("查询sy_table_column表最大主键sql："+sql);
		Statement st = null;
		ResultSet rs = null;
		int index = 0;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()) {
				index = rs.getInt("maxid");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			rs.close();
			st.close();
			//			conn.close();
		}
		logger.info("查询sy_table_column表最大主键结果："+index);
		return index;
	}
	
	public static String getTableDefId(String tableName,Connection conn) throws SQLException {
		String sql = "select tabledefid from sy_table_def where table_code = '"+tableName+"'";
		logger.info("查询sy_table_def表的主键sql："+sql);
		Statement st = null;
		ResultSet rs = null;
		String id = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()) {
				id = rs.getString("tabledefid");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			rs.close();
			st.close();
			//			conn.close();
		}
		logger.info("查询sy_table_def表的"+tableName+"行主键结果："+id);
		return id;
	}
	
	public static int getMaxIndexForTabledef(Connection conn) throws SQLException {
		String sql = "select max(tabledefid) as maxid from sy_table_def";
		logger.info("查询sy_table_def表最大主键sql："+sql);
		Statement st = null;
		ResultSet rs = null;
		int index = 0;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()){
				index = rs.getInt("maxid");
			}
			//			index = rs.getInt("maxid");
		}catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			rs.close();
			st.close();
			//			conn.close();
		}
		logger.info("查询sy_table_def表最大主键结果："+index);
		return index;
	}
	
	public static void insertColumnTable(String tableName,List<ColumnDTO> columns) throws SQLException {
		String sql = "insert into sy_table_column "
				+ "(tablecolumnid,colname,table_code,coltype,collen,colscale,ispk,isnotnull,isautoinc,colorder,created_by,"
				+ "created_dt,updated_by,updated_dt,ver,df,tabledefid,colcode) "
				+ "values(?,?,?,?,?,0,?,0,0,?,?,?,?,?,?,?,?,?)";
		Connection conn = JDBCutil.getConnection();
		conn.setAutoCommit(true);
		int startIndex = ClassToTableUtil.getMaxIndexForColumntable(conn);
		startIndex = (startIndex/100+1)*100;
		PreparedStatement st = conn.prepareStatement(sql); 
		int order = 0;
		for(ColumnDTO dto:columns) {
			startIndex++;
			order++;
			st.setString(1,startIndex+"");
			st.setString(2, dto.getName());
			st.setString(3, tableName);
			st.setString(4, dto.getType());
			st.setInt(5, dto.getLength());
			if(dto.isPK()) {
				st.setInt(6, 1);
			}else {
				st.setInt(6, 0);
			}
			st.setInt(7, order);
			st.setString(8, "1");
			st.setDate(9, new Date(new java.util.Date().getTime()));
			st.setString(10,"1");
			st.setDate(11, new Date(new java.util.Date().getTime()));
			st.setInt(12, 1);
			st.setInt(13, 0);
			st.setString(14, dto.getTableDefId());
			st.setString(15, dto.getName());
			st.addBatch();
		}
		st.executeBatch();
		st.close();
		JDBCutil.closeConn(conn);
	}
}
