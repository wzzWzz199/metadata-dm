package com.hayden.hap.common.utils.table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class JDBCutil {
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
		} catch (ClassNotFoundException e) {
			System.out.println("MYSQL驱动加载错误");
			e.printStackTrace();
		}

		try {
			String url = "jdbc:mysql://192.168.6.32:3306/hap_dev?allowMultiQueries=true&rewriteBatchedStatements=true";

			String user = "hap";
			String password = "1";
			conn = DriverManager.getConnection(url,user,password);
		} catch (SQLException e) {
			System.out.println("数据库链接错误");
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public static void closeConn(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
