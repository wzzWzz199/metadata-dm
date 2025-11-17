package com.hayden.hap.dbop.db.orm.jdbc;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.hayden.hap.dbop.db.orm.entity.ClobInfoVO;
import com.hayden.hap.dbop.db.orm.entity.SqlParseVO;
import com.hayden.hap.dbop.db.orm.sql.JdbcType;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.tableDef.itf.ITableDefService;
import com.hayden.hap.dbop.db.util.DBConstants;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClobUtil {
	private static ITableDefService tableDefService = 
			(ITableDefService) AppServiceHelper.findBean("tableDefService");
	private static final Logger logger = LoggerFactory
			.getLogger(ClobUtil.class);
	/**
	 * 判断是否包含clob字段，在写oracle库时，如果包含String数据，当超过4000字节长度需要特殊处理
	 * 这里默认字段全部采用预编译或者非预编译，不存在部分字段写在sql语句，部分字段采取赋值。
	 * @param dbType 基础数据库类型
	 * @param sql 
	 * @return 
	 * @author wangyi
	 * @date 2017年12月26日
	 */
	public static ClobInfoVO getClobInfo(String tableName, String sql){
		ClobInfoVO clobInfoVO = new ClobInfoVO();
		//标识是否已查询了表的clob字段
		boolean isQryTable = false;
		//clob字段集
		List<String> clobCols = null;
		//如果表不为空，查询是否包含clob字段
		if(StringUtils.isNotEmpty(tableName)){
			isQryTable = true;
			clobCols = getClobColList(tableName);
			if(ObjectUtil.isEmpty(clobCols)){
				clobInfoVO.setDealClob(false);
				return clobInfoVO;
			}
		}
		//当表名为空或者包含clob字段时，解析sql
		SqlParseVO sqlParseVO = parseSql(sql);			
		//如果之前没有查询过表，当表名不为空时，再去查
		if(!isQryTable){
			//表名赋值
			tableName = sqlParseVO.getTableName();
			if(StringUtils.isNotEmpty(tableName)){
				clobCols = getClobColList(tableName);
				if(ObjectUtil.isEmpty(clobCols)){
					clobInfoVO.setDealClob(false);
					return clobInfoVO;
				}
			}
		}
		//检查sql中的字段是否包含clob字段
		boolean isSqlContainClobCol = false;
		for(String col:clobCols){
			//包含列并且不为空时
			if(sqlParseVO.getColValMap().containsKey(col) && 
					!(sqlParseVO.getColValMap().get(col) instanceof SQLNullExpr)){
				isSqlContainClobCol = true;
				break;
			}
		}
		//如果sql不包含clob字段时，返回
		if(!isSqlContainClobCol){
			clobInfoVO.setDealClob(false);
			return clobInfoVO;
		}
		clobInfoVO.setDealClob(true);
		clobInfoVO.setSqlParseVO(sqlParseVO);
		clobInfoVO.setClobCols(clobCols);
		return clobInfoVO;
	}
	/**
	 * 查询表定义的clob字段
	 * @param tableName
	 * @return 
	 * @author wangyi
	 * @date 2017年12月26日
	 */
	private static List<String> getClobColList(String tableName){
		//类型为clob的字段
		List<String> colList = new ArrayList<String>();
		//查询该表是否包含clob数据
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		if(tableDefVO != null){
			List<TableColumnVO> columnList = tableDefVO.getColumnList();
			for(TableColumnVO column:columnList){
				//比较oracle数据类型值，是否是clob
				if(column.getOra_coltype().equals(JdbcType.CLOB.getTypeName())){
					colList.add(column.getColcode());
				}
			}
		}
		return colList;
	}
	/**
	 * 解析sql，获取表名称、列值映射
	 * @param sql
	 * @return 
	 * @author wangyi
	 * @date 2017年12月26日
	 */
	private static SqlParseVO parseSql(String sql){
		SqlParseVO sqlParseVO = new SqlParseVO();
		try {
			//解析sql
			SQLStatementParser parser = new OracleStatementParser(sql); 
			SQLStatement statement = parser.parseStatement();
			//表名称、列值映射
			String tableName = null;
			Map<String, SQLExpr> colValMap = new LinkedHashMap<String, SQLExpr>();
			//解析插入和更新sql
			if(statement instanceof OracleInsertStatement){
				OracleInsertStatement insertStatement = (OracleInsertStatement) statement;
				//字段和值
				List<SQLExpr> columns = insertStatement.getColumns();
				ValuesClause valuesClause = insertStatement.getValues();
				List<SQLExpr> values = valuesClause.getValues();
				//存储映射
				for(int i=0,length=columns.size();i<length;i++){
					colValMap.put(((SQLName)columns.get(i)).getSimpleName(), values.get(i));
				}
				//表名
				tableName = insertStatement.getTableName().getSimpleName();
			}else if(statement instanceof OracleUpdateStatement){
				OracleUpdateStatement updateStatement = (OracleUpdateStatement) statement;
				List<SQLUpdateSetItem> items = updateStatement.getItems();
				for(int i=0,length=items.size();i<length;i++){
					SQLUpdateSetItem item = items.get(i);
					colValMap.put(((SQLName)item.getColumn()).getSimpleName(), item.getValue());
				}
				//表名
				tableName = updateStatement.getTableName().getSimpleName();
			}		
			sqlParseVO.setTableName(tableName);
			sqlParseVO.setColValMap(colValMap);
		} catch (Exception e) {
			logger.debug(sql+"解析错误！"+e.getMessage());
		}
		return sqlParseVO;
	}
	public static int updateClobData(JdbcTemplate jdbcTemplate, 
			ClobInfoVO clobInfoVO, String dataSourceIdTmp, String sql,
			Object[] preStatementParamTmp, int[] preStatementParamTypeTmp){
		int updateResult = 0;
		List<String> clobCols = clobInfoVO.getClobCols();
		SqlParseVO sqlParseVO = clobInfoVO.getSqlParseVO();
		Map<String, SQLExpr> colValMap = sqlParseVO.getColValMap();
		if (preStatementParamTmp == null){
			final List<String> clobStrList = new ArrayList<String>();
			for(String clobCol:clobCols){
				//处理空值
				if(colValMap.get(clobCol) instanceof SQLNullExpr){
					continue;
				}
				String clobStr = ((SQLCharExpr)colValMap.get(clobCol)).getText();
				if(clobStr.length() >= DBConstants.CLOB_STRING_LENGTH){
					clobStrList.add(clobStr);
					Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");
		            Matcher m = CRLF.matcher(sql);
		            Matcher clobStrM = CRLF.matcher(clobStr);
		            if (m.find()) {
		            	sql = m.replaceAll("\\\\n");
		            	clobStr = clobStrM.replaceAll("\\\\n");
		            }	
		            //单引号加转换
		            clobStr = clobStr.replaceAll("'", "''");
					sql = StringUtils.replace(sql, clobStr, "?");
					sql = StringUtils.replace(sql, "'?'", "?");
				}
			}
			updateResult = jdbcTemplate.update(sql, new ArgumentPreparedStatementSetter(clobStrList.toArray(new String[0])){
				public void setValues(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < clobStrList.size(); i++) {
						String clobStr = clobStrList.get(i);
						Reader clobReader = new StringReader(clobStr); // 将 text转成流形式  
						ps.setCharacterStream(i+1, clobReader, clobStr.length());
					}
				}
			});
		}else if (preStatementParamTypeTmp == null){
			final List<Object> preStatementParamList = new ArrayList<Object>();
			preStatementParamList.addAll(Arrays.asList(preStatementParamTmp));
			//遍历colValMap，检查clob字段位置
			int colIndex = 0;
			final List<Integer> clobColIndex = new ArrayList<Integer>();
			for(Map.Entry<String, SQLExpr> entry:colValMap.entrySet()){
				if(clobCols.contains(entry.getKey())){
					//处理空值
					if(!(entry.getValue() instanceof SQLNullExpr)){
						String clobStr = ((SQLCharExpr)entry.getValue()).getText();
						if(!clobStr.trim().equals("?")){
							if(clobStr.length() >= DBConstants.CLOB_STRING_LENGTH){
								sql = StringUtils.replace(sql, clobStr, "?");
								preStatementParamList.add(colIndex, clobStr);
								clobColIndex.add(Integer.valueOf(colIndex));
							}
						}else {
							clobStr = (String) preStatementParamTmp[colIndex];
							if(clobStr.length() >= DBConstants.CLOB_STRING_LENGTH){
								clobColIndex.add(Integer.valueOf(colIndex));
							}
						}
					}
				}
				colIndex++;
			}				
			updateResult = jdbcTemplate.update(sql, new ArgumentPreparedStatementSetter(preStatementParamList.toArray(new Object[0])){
				public void setValues(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < preStatementParamList.size(); i++) {
						if(clobColIndex.contains(Integer.valueOf(i))){
							String clobStr = (String) preStatementParamList.get(i);
							Reader clobReader = new StringReader(clobStr); // 将 text转成流形式  
							ps.setCharacterStream(i+1, clobReader, clobStr.length());
						}else{
							super.doSetValue(ps, i+1, preStatementParamList.get(i));
						}
					}
				}
			});
		}else {
			final List<Object> preStatementParamList = new ArrayList<Object>();
			preStatementParamList.addAll(Arrays.asList(preStatementParamTmp));
			final List<Integer> preStatementParamTypeList = new ArrayList<Integer>();
			preStatementParamTypeList.addAll(Arrays.asList(ArrayUtils.toObject(preStatementParamTypeTmp)));
			//遍历colValMap，检查clob字段位置
			int colIndex = 0;
			final List<Integer> clobColIndex = new ArrayList<Integer>();
			for(Map.Entry<String, SQLExpr> entry:colValMap.entrySet()){
				if(clobCols.contains(entry.getKey())){
					//处理空值
					if(!(entry.getValue() instanceof SQLNullExpr)){
						String clobStr = ((SQLCharExpr)entry.getValue()).getText();
						if(!clobStr.trim().equals("?")){
							if(clobStr.length() >= DBConstants.CLOB_STRING_LENGTH){
								sql = StringUtils.replace(sql, clobStr, "?");
								preStatementParamList.add(colIndex, clobStr);
								preStatementParamTypeList.add(colIndex, Integer.valueOf(Types.CLOB));
								clobColIndex.add(Integer.valueOf(colIndex));
							}
						}else {
							clobStr = (String) preStatementParamTmp[colIndex];
							if(clobStr.length() >= DBConstants.CLOB_STRING_LENGTH){
								clobColIndex.add(Integer.valueOf(colIndex));
							}
						}
					}
				}
				colIndex++;
			}				
			final int[] argTypes = ArrayUtils.toPrimitive(preStatementParamTypeList.toArray(new Integer[0]));
			updateResult = jdbcTemplate.update(sql, new ArgumentTypePreparedStatementSetter(preStatementParamList.toArray(new Object[0]),
					argTypes){
				public void setValues(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < preStatementParamList.size(); i++) {
						if(clobColIndex.contains(Integer.valueOf(i))){
							String clobStr = (String) preStatementParamList.get(i);
							Reader clobReader = new StringReader(clobStr); // 将 text转成流形式  
							ps.setCharacterStream(i+1, clobReader, clobStr.length());
						}else{
							super.doSetValue(ps, i+1, argTypes[i], preStatementParamList.get(i));
						}
					}
				}
			});
		}
		return updateResult;
	}
}
