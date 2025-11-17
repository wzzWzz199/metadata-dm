package com.hayden.hap.dbop.db.orm.jdbc;

import com.hayden.hap.dbop.db.util.DBConstants;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: VOBatchPreparedStatementCallback 
 * @Description: 预编译情况下批量处理回调类 主要用来分批处理
 * @author LUYANYING
 * @date 2015年6月2日 下午6:04:42 
 * @version V1.0   
 *  
 */
public class VOBatchPreparedStatementCallback implements PreparedStatementCallback<int[]>{
	private BatchPreparedStatementSetter batchPreparedStatementSetter = null;
	private int perBatchSize = 0;
	
	public VOBatchPreparedStatementCallback(BatchPreparedStatementSetter batchPreparedStatementSetter, int perBatchSize){
		this.batchPreparedStatementSetter = batchPreparedStatementSetter;
		this.perBatchSize = perBatchSize;
	}
	public VOBatchPreparedStatementCallback(BatchPreparedStatementSetter batchPreparedStatementSetter){
		this(batchPreparedStatementSetter, DBConstants.PER_BATCH_SIZE);
	}

	@Override
	public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException {
		try {
			int batchSize = batchPreparedStatementSetter.getBatchSize();
			InterruptibleBatchPreparedStatementSetter ipss =
					(batchPreparedStatementSetter instanceof InterruptibleBatchPreparedStatementSetter ?
					(InterruptibleBatchPreparedStatementSetter) batchPreparedStatementSetter : null);
			if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
				int[] result = null;
				int count = 0;
				for (int i = 0; i < batchSize; i++) {
					batchPreparedStatementSetter.setValues(ps, i);
					count++;
					if (ipss != null && ipss.isBatchExhausted(i)) {
						break;
					}
					ps.addBatch();
					if(count % perBatchSize == 0){
						count = 0;
						int[] tmp = ps.executeBatch();
						result = ObjectUtil.addAll(result, tmp);
					}
				}
				if(count != 0){
					int[] tmp = ps.executeBatch();
					result = ObjectUtil.addAll(result, tmp);
				}
				return result;
			}
			else {
				List<Integer> rowsAffected = new ArrayList<Integer>();
				for (int i = 0; i < batchSize; i++) {
					batchPreparedStatementSetter.setValues(ps, i);
					if (ipss != null && ipss.isBatchExhausted(i)) {
						break;
					}
					rowsAffected.add(ps.executeUpdate());
				}
				int[] rowsAffectedArray = new int[rowsAffected.size()];
				for (int i = 0; i < rowsAffectedArray.length; i++) {
					rowsAffectedArray[i] = rowsAffected.get(i);
				}
				return rowsAffectedArray;
			}
		}
		finally {
			if (batchPreparedStatementSetter instanceof ParameterDisposer) {
				((ParameterDisposer) batchPreparedStatementSetter).cleanupParameters();
			}
		}
	}
	
}
