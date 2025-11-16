package com.hayden.hap.common.db.temptable.dao;

import com.hayden.hap.common.common.dao.BaseDaoImpl;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.db.util.DBConstants;
import com.hayden.hap.common.db.util.DBType;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.utils.ModuleDataSrcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository("tempTableDao")
public class TempTableDaoImpl extends BaseDaoImpl {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TempTableDaoImpl.class);

	/**
	 * <p>创建临时表；同时插入数据</p>
	 * <P>example：</br>
	 * 	  String[][] columns = {{"NAME","varchar","800"},{"ID","int","20"}};</br>
	 * 	  createTempTable("STUDENT",columns,true);</br>
	 * @param tempTableName 临时表名称
	 * @param columns 列定义：  为二维数组，格式定义和example一样，一次为：列名称，列字段类型，长度
	 * @param list 数据集合
	 * @param dataSourceId 数据源ID
	 * @return 
	 */
	public <T> void createAndInsertData(String tempTableName, String[][] columns,
			Collection<T> list, String dataSourceId) {
		createTempTable(tempTableName, columns, dataSourceId);
		
		insertTempData(tempTableName,columns,list, dataSourceId);
	}
	private String createTempTable(String tempTableName, String[][] columns, String dataSourceId) {
		/*ObjectUtil.validIsTrue('t'!=Character.toLowerCase(tempTableName.charAt(0)), "临时表表名请以t或T开头");
		ObjectUtil.validIsTrue(columns==null || columns.length<1, "临时表列字段没有定义");*/
		
		//findbug提示会存在空指针异常，修改上面写法。规范写法，不智能。
		if('t'!=Character.toLowerCase(tempTableName.charAt(0)))
			throw new IllegalArgumentException("临时表表名请以t或T开头");
		
		if(columns==null || columns.length==0)
			throw new IllegalArgumentException("临时表列字段没有定义");
			
		String[] newColumns = new String[columns.length];
		//获取当前数据库类型
		String dbType = ModuleDataSrcUtils.getDbType();
		for(int i=0;i<columns.length;i++) {
			String[] column = columns[i];
			ObjectUtil.validIsTrue(column.length<3, "临时表列字段缺少类型或长度定义");
			StringBuilder sb = new StringBuilder();
			sb.append(column[0]);
			sb.append(" ");
			//区分oracle数据库
			if(dbType.equals(DBType.ORACLE.getCode()) || dbType.equals(DBType.DM.getCode())){
				sb.append(column[2]);
			}else{
				sb.append(column[1]);
			}
			if(!column[1].equalsIgnoreCase("DATETIME")){			
				sb.append(" (");
				sb.append(column[3]);
				sb.append(")");
			}
			newColumns[i] = sb.toString();
		}
		try {
			dropTempTable(tempTableName, dataSourceId);
		}catch(Exception e) {
			logger.error("删除临时表"+tempTableName+"出现异常，但应该不影响正常工作");
		}
		return createTempTable(tempTableName, newColumns, dataSourceId);
	}
	
	/**
	 * 
	 * @Title: createTempTable 
	 * @Description: 创建临时表
	 * @param tempTableName 临时表名
	 * @param columns 字段信息
	 * @param dataSourceId 数据源ID
	 * @return
	 * @return String
	 * @throws
	 */
	public String createTempTable(String tempTableName, String[] columns, String dataSourceId) {
		try {
			JdbcTemplate jdbcTemplate = this.getJdbcTemplateSupportDao().getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
			String dbType = this.getJdbcTemplateSupportDao().getDataSourceManager().getDbType(dataSourceId);
			String sql = this.getJdbcTemplateSupportDao().getSqlBuilderManager().getCreateTempTableSql(dbType, tempTableName, columns);
			jdbcTemplate.execute(sql);
		}catch (Exception e) {
			logger.error("创建临时表"+tempTableName+"出现异常，但可能不会影响业务");
		}
		return tempTableName;
	}

	/**
	 * 
	 * @Title: insertTempDataBatch 
	 * @Description: 往临时表插入数据
	 * @param tempTableName 临时表名
	 * @param columns 字段信息
	 * @param valueList 数据
	 * @param dataSourceId 数据源ID
	 * @return void
	 * @throws
	 */
	public void insertTempDataBatch(String tempTableName, String[][] columns, List<Object[]> valueList, String dataSourceId) {
		if(valueList == null || valueList.isEmpty())
			return ;
		StringBuilder sb = new StringBuilder();
		sb.append("insert into " + tempTableName + " (");
		StringBuilder qmSb = new StringBuilder();
		for(String[] column : columns) {
			String columnName = column[0];
			sb.append(columnName);
			sb.append(",");
			qmSb.append("?,");
		}
		sb.deleteCharAt(sb.length()-1);
		qmSb.deleteCharAt(qmSb.length()-1);
		sb.append(") values (" + qmSb.toString()+")");
		String sql = sb.toString();
		JdbcTemplate jdbcTemplate = this.getJdbcTemplateSupportDao().getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
		
		List<Object[]> l = new ArrayList<Object[]>();
		for (Object[] t : valueList) {
			l.add(t);
			if (l.size() == DBConstants.PER_BATCH_SIZE) {
				jdbcTemplate.batchUpdate(sql, l);
				l.clear();
			}
		}
		if (!l.isEmpty()) {
			jdbcTemplate.batchUpdate(sql, l);
		}
	}
	private <T> void insertTempData(String tempTableName,String[][] oldColumns, Collection<T> list, String dataSourceId) {
		ObjectUtil.validNotNull(oldColumns, "没有找到表结构"+tempTableName);
		
		List<Object[]> dataList = new ArrayList<Object[]>();
		for(T t : list){
			Object[] valueArray = new Object[oldColumns.length];
			for(int j=0;j<oldColumns.length;j++) {
				String[] column = oldColumns[j];
				String columnName = column[0];
				if(t instanceof AbstractVO){
					valueArray[j] = ((AbstractVO)t).get(columnName);
				}else{
					valueArray[j] = t;
				}
			}
			dataList.add(valueArray);
		}

		insertTempDataBatch(tempTableName, oldColumns, dataList, dataSourceId);

	}
	/**
	 * 
	 * @Title: dropTempTable 
	 * @Description: 删除临时表
	 * @param tempTableName 临时表名
	 * @param dataSourceId 数据源ID
	 * @return void
	 * @throws
	 */
	public void dropTempTable(String tempTableName, String dataSourceId) {
		JdbcTemplate jdbcTemplate = this.getJdbcTemplateSupportDao().getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
		String dbType = this.getJdbcTemplateSupportDao().getDataSourceManager().getDbType(dataSourceId);
		String sql = this.getJdbcTemplateSupportDao().getSqlBuilderManager().getDropTempTableSql(dbType, tempTableName);
		jdbcTemplate.execute(sql);
	}
}
