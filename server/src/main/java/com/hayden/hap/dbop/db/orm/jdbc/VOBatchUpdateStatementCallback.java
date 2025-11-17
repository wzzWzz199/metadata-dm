package com.hayden.hap.dbop.db.orm.jdbc;

import com.hayden.hap.dbop.db.util.DBConstants;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.Statement;

/** 
 * @ClassName: VOBatchUpdateStatementCallback 
 * @Description: 非预编译情况下批量处理回调类 主要用来分批处理sql语句
 * @author LUYANYING
 * @date 2015年6月2日 下午8:47:20 
 * @version V1.0   
 *  
 */
public class VOBatchUpdateStatementCallback implements StatementCallback<int[]>, SqlProvider {
	private String[] batchSqls = null;
	private int perBatchSize = 0;
	
	public VOBatchUpdateStatementCallback(String[] batchSqls){
		this(batchSqls, DBConstants.PER_BATCH_SIZE);
	}
	public VOBatchUpdateStatementCallback(String[] batchSqls, int perBatchSize){
		this.batchSqls = batchSqls;
		this.perBatchSize = perBatchSize;
	}

	private String currSql;

	@Override
	public int[] doInStatement(Statement stmt) throws SQLException, DataAccessException {
		int[] rowsAffected = new int[batchSqls.length];
		if (JdbcUtils.supportsBatchUpdates(stmt.getConnection())) {
			int count = 0;
			for (String sqlStmt : batchSqls) {
				count++;
				this.currSql = appendSql(this.currSql, sqlStmt);
				stmt.addBatch(sqlStmt);
				try {
					if(count % perBatchSize == 0){
						count = 0;
						int[] tmp = stmt.executeBatch();
						rowsAffected = ObjectUtil.addAll(rowsAffected, tmp);
					}
				}
				catch (BatchUpdateException ex) {
					String batchExceptionSql = null;
					for (int i = 0; i < ex.getUpdateCounts().length; i++) {
						if (ex.getUpdateCounts()[i] == Statement.EXECUTE_FAILED) {
							batchExceptionSql = appendSql(batchExceptionSql, batchSqls[i]);
						}
					}
					if (StringUtils.hasLength(batchExceptionSql)) {
						this.currSql = batchExceptionSql;
					}
					throw ex;
				}
			}
			
			if(count != 0){
				try {
					int[] tmp = stmt.executeBatch();
					rowsAffected = ObjectUtil.addAll(rowsAffected, tmp);
				}
				catch (BatchUpdateException ex) {
					String batchExceptionSql = null;
					for (int i = 0; i < ex.getUpdateCounts().length; i++) {
						if (ex.getUpdateCounts()[i] == Statement.EXECUTE_FAILED) {
							batchExceptionSql = appendSql(batchExceptionSql, batchSqls[i]);
						}
					}
					if (StringUtils.hasLength(batchExceptionSql)) {
						this.currSql = batchExceptionSql;
					}
					throw ex;
				}
			}
		}
		else {
			for (int i = 0; i < batchSqls.length; i++) {
				this.currSql = batchSqls[i];
				if (!stmt.execute(batchSqls[i])) {
					rowsAffected[i] = stmt.getUpdateCount();
				}
				else {
					throw new InvalidDataAccessApiUsageException("Invalid batch SQL statement: " + batchSqls[i]);
				}
			}
		}
		return rowsAffected;
	}

	private String appendSql(String sql, String statement) {
		return (StringUtils.isEmpty(sql) ? statement : sql + "; " + statement);
	}

	@Override
	public String getSql() {
		return this.currSql;
	}
}
