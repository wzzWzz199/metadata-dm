package com.hayden.hap.common.db.util;

import javax.sql.DataSource;

/** 
 * @ClassName: JdbcUtil 
 * @Description: 
 * @author LUYANYING
 * @date 2015年3月26日 下午2:35:54 
 * @version V1.0   
 *  
 */
public class JdbcUtil {
	/**
	 * 
	 * @Title: getDbType 
	 * @Description: 通过数据库连接解析数据库类别
	 * @param dbUrl
	 * @return
	 * @return DBType
	 * @throws
	 */
	public static String getDbType(String dbUrl) {
        if (dbUrl == null) {
            return null;
        }
        if (dbUrl.startsWith("jdbc:derby:")) {
        	return DBType.DERBY.getCode();
        } else if (dbUrl.startsWith("jdbc:mysql:")) {
            return DBType.MYSQL.getCode();
        } else if (dbUrl.startsWith("jdbc:dm:")) {
            return DBType.DM.getCode();
        } else if (dbUrl.startsWith("jdbc:mariadb:")) {
            return DBType.MARIADB.getCode();
        } else if (dbUrl.startsWith("jdbc:oracle:")) {
            return DBType.ORACLE.getCode();
        } else if (dbUrl.startsWith("jdbc:microsoft:")) {
            return DBType.SQLSERVER.getCode();
        } else if (dbUrl.startsWith("jdbc:sybase:Tds:")) {
            return DBType.SYBASE.getCode();
        } else if (dbUrl.startsWith("jdbc:postgresql:")) {
            return DBType.POSTGRESQL.getCode();
        } else if (dbUrl.startsWith("jdbc:hsqldb:")) {
            return DBType.HSQL.getCode();
        } else if (dbUrl.startsWith("jdbc:db2:")) {
            return DBType.DB2.getCode();
        }  else if (dbUrl.startsWith("jdbc:sqlserver:")) {
            return DBType.SQLSERVER.getCode();
        } 
        
        else {
            return null;
        }
    }
	/**
	 * 
	 * @Title: getDbType 
	 * @Description: 通过DataSource对象解析数据库类别
	 * @param dataSource
	 * @return
	 * @return String
	 * @throws
	 */
	public static String getDbType(DataSource dataSource) {
        if (dataSource == null) {
            return null;
        }
        String dbUrl = null;
        try {
			dbUrl = (String) ReflectUtil.invokePublicMethod(dataSource, dataSource.getClass(), "getUrl", null, null);
		} catch (Exception e) {
			
		}
        if(dbUrl == null || "".equals(dbUrl))
        	return null;
        if (dbUrl.startsWith("jdbc:derby:")) {
        	return DBType.DERBY.getCode();
        } else if (dbUrl.startsWith("jdbc:mysql:")) {
            return DBType.MYSQL.getCode();
        } else if (dbUrl.startsWith("jdbc:dm:")) {
            return DBType.DM.getCode();
        } else if (dbUrl.startsWith("jdbc:mariadb:")) {
            return DBType.MARIADB.getCode();
        } else if (dbUrl.startsWith("jdbc:oracle:")) {
            return DBType.ORACLE.getCode();
        } else if (dbUrl.startsWith("jdbc:microsoft:")) {
            return DBType.SQLSERVER.getCode();
        } else if (dbUrl.startsWith("jdbc:sybase:Tds:")) {
            return DBType.SYBASE.getCode();
        } else if (dbUrl.startsWith("jdbc:postgresql:")) {
            return DBType.POSTGRESQL.getCode();
        } else if (dbUrl.startsWith("jdbc:hsqldb:")) {
            return DBType.HSQL.getCode();
        } else if (dbUrl.startsWith("jdbc:db2:")) {
            return DBType.DB2.getCode();
        } else if (dbUrl.startsWith("jdbc:sqlserver:")) {
            return DBType.SQLSERVER.getCode();
        } else {
            return null;
        }
    }
	/**
	 * 
	 * @Title: CountBatchUpdateRows 
	 * @Description: 统计批量更新记录数
	 * @param batchUpdateResult jdbc批量更新结果
	 * @return
	 * @return int 批量更新受影响记录数
	 * @throws
	 */
	public static int CountBatchUpdateRows(int[] batchUpdateResult){
		int updateCount = 0;
		for(int commondResultTmp : batchUpdateResult){
			if(commondResultTmp == -2 || commondResultTmp > 1) //oracle在预编译批量更新时会返回-2,目前只是忽略 //mysql在预编译批量插入时返回的数组元素都是插入的记录数
				commondResultTmp = 1;
			updateCount = updateCount + commondResultTmp;
		}
		return updateCount;
	}
}
