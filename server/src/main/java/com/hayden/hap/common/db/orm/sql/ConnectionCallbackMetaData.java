/**
 * Project Name:hap-sy
 * File Name:ReSultSetExtAction.java
 * Package Name:com.hayden.hap.sy.db.orm.sql
 * Date:2015年11月26日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
 */

package com.hayden.hap.common.db.orm.sql;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.hayden.hap.common.db.tableDef.entity.DataTypeUtil;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.util.DBType;
import com.hayden.hap.common.db.util.TableDefDataType;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.ConnectionProxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName:ReSultSetExtAction ().<br/>
 * Date: 2015年11月26日 <br/>
 * 
 * @author ZhangJie
 * @version
 * @param <T>
 * @see
 */
public class ConnectionCallbackMetaData<T> implements
		ConnectionCallback<List<TableColumnVO>> {

	private String dbType = "";
	private String catalog = "";
	private String schema = "";
	private String tableName = "";
	private Long parentid = 0L;
	private Map<String,String> colMap;

	public ConnectionCallbackMetaData(String dbType, String catalog, String schema,
									  String tableName,Long parentid) {
		this.dbType = dbType;
		this.catalog = catalog;
		this.schema = schema;
		this.tableName = tableName;
		this.parentid = parentid;
	}

	@Override
	public List<TableColumnVO> doInConnection(Connection con)
			throws SQLException, DataAccessException {
		//设置显示名称
		if(dbType.equals(DBType.ORACLE.getCode())){
			//Proxy对象
			if(con instanceof ConnectionProxy){
				//获取目标连接对象
				Connection targetConn = ((ConnectionProxy)con).getTargetConnection();
				if(targetConn instanceof DruidPooledConnection){
					//获取关联连接
					Connection linkedConn = ((DruidPooledConnection)targetConn).getConnection();
					if(linkedConn instanceof ConnectionProxyImpl){
						//获取具体连接对象
						Connection connection = ((ConnectionProxyImpl)linkedConn).getConnectionRaw();
						if(connection instanceof oracle.jdbc.driver.OracleConnection){
							((oracle.jdbc.driver.OracleConnection)connection).setRemarksReporting(true);
						}
					}
				}

			}
		}

		java.sql.DatabaseMetaData dbmd = con.getMetaData();
		//primaryKey
		ResultSet rsPk = null;
		if(dbType.equals(DBType.ORACLE.getCode())){
			rsPk = dbmd.getPrimaryKeys(null,"%",
					this.tableName.toUpperCase());
		} else if (dbType.equals(DBType.DM.getCode())) {// 靠链接中限定的
			rsPk = dbmd.getPrimaryKeys(null,this.schema,
					this.tableName.toUpperCase());
		} else{
			rsPk = dbmd.getPrimaryKeys(this.catalog,this.schema,
					this.tableName);
		}
		Map<String,String> keys = new HashMap<String,String>();
		while (rsPk.next()) {
			keys.put(rsPk.getString("COLUMN_NAME").toLowerCase(), rsPk.getString("COLUMN_NAME"));
		}
		ResultSet rs = null;
		if(dbType.equals(DBType.ORACLE.getCode()) || dbType.equals(DBType.DM.getCode())){
			rs = dbmd.getColumns(null,"%",
					this.tableName.toUpperCase(), null);
		}else{
			rs = dbmd.getColumns(this.catalog,this.schema,
					this.tableName, null);
		}
		List<TableColumnVO> voList = new ArrayList<TableColumnVO>();
		TableColumnVO tcv = null;
		while (rs.next()) {
			tcv = new TableColumnVO();
			//自增编号
//			tcv.setTablecolumnid(id++);
			//父表id
			tcv.setTabledefid(parentid);
			// 表编码
			tcv.setTable_code(this.tableName.toLowerCase());
			// 列编码
			tcv.setColcode(rs.getString("COLUMN_NAME").toLowerCase());
			// 数据类型
			if(rs.getInt("DATA_TYPE")==-7) {
				tcv.setColtype(JdbcType.forCode(-6).getTypeName());
			}else if(rs.getInt("DATA_TYPE")==4){
				tcv.setColtype(TableDefDataType.INTEGER.getTypeName());
			}else {
				tcv.setColtype(rs.getString("TYPE_NAME"));
			}
			//添加ora_coltype
			if(dbType.equals(DBType.ORACLE.getCode())){
				tcv.setOra_coltype(tcv.getColtype());
			}else{
				tcv.setOra_coltype(DataTypeUtil.getOracleColType(tcv.getColtype()));
			}
			// 长度
			tcv.setCollen(rs.getInt("COLUMN_SIZE"));
			// 小数位
			tcv.setColscale(rs.getInt("DECIMAL_DIGITS"));
			//重置下长度和小数位
			if(colMap.containsKey(rs.getString("COLUMN_NAME").toLowerCase())){
				String dbColType = colMap.get(rs.getString("COLUMN_NAME").toLowerCase());
				int colTypeLength = getColTypeLength(dbColType);
				int colTypeScale = getColTypeScale(dbColType);
				tcv.setCollen(colTypeLength);
				tcv.setColscale(colTypeScale);
			}
			// 是否主键
			if (keys.containsKey(rs.getString("COLUMN_NAME").toLowerCase())) {
				tcv.setIspk(SyConstant.SY_TRUE);
			} else {
				tcv.setIspk(SyConstant.SY_FALSE);
			}
			// 默认值
			String columnDef = rs.getString("COLUMN_DEF");
			if(StringUtils.isEmpty(columnDef))
				tcv.setColdefault("");
			else{
				//这里判断下，如果是number类型，默认值含有单引号时需要删除下。
				//这种问题是考虑到oracle库（依据工具生成后默认值含有单引号）
				String tmpColumnDef = columnDef.trim();
				if(dbType.equals(DBType.ORACLE.getCode())){
					if(tcv.getColtype().equals("NUMBER")&&
							tmpColumnDef.startsWith("'")&&tmpColumnDef.endsWith("'")&&tmpColumnDef.length()>2)
						//判断如果是oracle，数据类型为数值型，如果默认值被‘’包围，则删除单引号
						tcv.setColdefault(tmpColumnDef.substring(1, tmpColumnDef.length()-1));
					else
						tcv.setColdefault(tmpColumnDef);
				}else
					tcv.setColdefault(tmpColumnDef);
			}
			// 描述
			String remarks = rs.getString("REMARKS");
			String colName = null;
			String colDesc = null;
			if(!StringUtils.isEmpty(remarks)){
				Pattern repSlashPattern = Pattern.compile("^\\[(.*)\\]([\\s\\S]+)");
				Matcher repSlashMatcher = repSlashPattern.matcher(remarks);
				if (repSlashMatcher.find()) {
					colName = repSlashMatcher.group(1);
					colDesc = repSlashMatcher.group(2);
				}else{
					colName=remarks;
				}
			}
			if(colName!=null && colName.length()>50) {//修正当描述过长时，插库错误
				colName = tcv.getColcode();
			}
			tcv.setColname(colName);
			tcv.setColdesc(colDesc);
			// 序号
			tcv.setColorder(rs.getInt("ORDINAL_POSITION"));
			// 是否为空
			tcv.setIsnotnull(rs.getInt("NULLABLE")==SyConstant.SY_FALSE?SyConstant.SY_TRUE:SyConstant.SY_FALSE);
			// 是否自增
			if(!dbType.equals(DBType.ORACLE.getCode())&& !dbType.equals(DBType.DM.getCode())){
				tcv.setIsautoinc(rs.getBoolean("IS_AUTOINCREMENT")?SyConstant.SY_TRUE:SyConstant.SY_FALSE);
			}
			// gencode
			tcv.setGencode("");
			voList.add(tcv);
		}
		return voList;
	}

	private int getColTypeLength(String colType){
		if(colType.indexOf("(")!=-1){
			if(colType.indexOf(",")!=-1)
				return Integer.parseInt(colType.substring(colType.indexOf("(")+1,colType.indexOf(",")));
			else
				return Integer.parseInt(colType.substring(colType.indexOf("(")+1,colType.indexOf(")")));
		}
		return 0;
	}

	private int getColTypeScale(String colType){
		if(colType.indexOf("(")!=-1&&colType.indexOf(",")!=-1){
			return Integer.parseInt(colType.substring(colType.indexOf(",")+1,colType.indexOf(")")));
		}
		return 0;
	}

	public Map<String, String> getColMap() {
		return colMap;
	}

	public void setColMap(Map<String, String> colMap) {
		this.colMap = colMap;
	}
}
