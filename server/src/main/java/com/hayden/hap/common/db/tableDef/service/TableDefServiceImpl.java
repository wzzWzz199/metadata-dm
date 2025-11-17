package com.hayden.hap.common.db.tableDef.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.BaseRuntimeException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVoConstants;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.tableDef.itf.ITableInfoCallback;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.db.util.ResourceUtil;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.DbEnvUtils;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @ClassName: TableServiceImpl
 * @Description:
 * @author LUYANYING
 * @date 2015年4月15日 上午10:14:58
 * @version V1.0
 * 
 */
@Service("tableDefService")
public class TableDefServiceImpl implements ITableDefService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TableDefServiceImpl.class);

	private static final String TS_COLUMN_NAME = "ts";
	
	private static final String INNERCODE_COLUMN_NAME = "innercode";
	
	@Autowired
	IBaseService baseService;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Cacheable(value = "syAllTablesCache", key = "'syAllTables'")
	@Override
	public VOSet<TableDefVO> querySyAllTables() {
		return baseService.query(TableDefVO.class, new DynaSqlVO());
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Cacheable(value = "syTablesCache", key = "(#tbname).toLowerCase()", condition = "#tbname!=null")
	@Override
	public TableDefVO queryDetailedTableByTbname(String tbname) {
		if (!ObjectUtil.isNotNull(tbname))
			return null;
		tbname = tbname.toLowerCase();
		StringBuffer sb = new StringBuffer();
		sb.append("select t1.tabledefid, t1.table_code, t1.modulecode, t1.table_name, t1.table_type, t1.issqllog ");
		sb.append(", t1.table_desc, t1.ddlsql, t1.classname");
		sb.append(", t1.created_by, t1.created_dt, t1.updated_by, t1.updated_dt, t1.ver, t1.df, t1.isenable");
		sb.append(", t2.tablecolumnid, t2.colcode, t2.table_code as col_tbname, t2.coltype, t2.ora_coltype, t2.collen, t2.colscale");
		sb.append(", t2.colname, t2.coldesc, t2.ispk, t2.coldefault, t2.isnotnull, t2.isautoinc, t2.gencode, t2.colorder");
		sb.append(", t2.created_by as col_created_by, t2.created_dt as col_created_dt, t2.updated_by as col_updated_by, t2.updated_dt as col_updated_dt, t2.ver as col_ver, t2.df as col_df");
		sb.append(" from sy_table_def t1 left join sy_table_column t2 on t1.table_code = t2.table_code");
		sb.append(" where t1.table_code=? ");
		List<TableDefVO> resultList = baseService.executeQuery(sb.toString(), null,
				new Object[] { tbname }, new ResultSetExtractor<TableDefVO>() {

					@Override
					public TableDefVO extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						TableDefVO tableDefVO = null;
						List<TableColumnVO> columnList = null;
						while (rs.next()) {
							if(rs.getString("colcode")==null)
								return null;
							if (tableDefVO == null) {
								tableDefVO = new TableDefVO();
								tableDefVO.setTabledefid(rs
										.getLong("tabledefid"));
								tableDefVO.setTable_code(rs
										.getString("col_tbname"));
								tableDefVO.setModulecode(rs
										.getString("modulecode"));
								tableDefVO.setTable_name(rs
										.getString("table_name"));
								tableDefVO.setTable_type(rs
										.getInt("table_type"));
								tableDefVO.setIssqllog(rs.getInt("issqllog"));
								tableDefVO.setTable_desc(rs
										.getString("table_desc"));
								tableDefVO.setDdlsql(rs.getString("ddlsql"));
								tableDefVO.setClassname(rs
										.getString("classname"));
								tableDefVO.setCreated_by(rs
										.getLong("created_by"));
								Timestamp createdDt = rs
										.getTimestamp("created_dt");
								if (createdDt != null)
									tableDefVO.setCreated_dt(new Date(createdDt
											.getTime()));
								tableDefVO.setUpdated_by(rs
										.getLong("updated_by"));
								Timestamp updatedDt = rs
										.getTimestamp("updated_dt");
								if (updatedDt != null)
									tableDefVO.setUpdated_dt(new Date(updatedDt
											.getTime()));
								tableDefVO.setVer(rs.getInt("ver"));
								tableDefVO.setDf(rs.getInt("df"));
								tableDefVO.setIsenable(rs.getInt("isenable"));
								columnList = new ArrayList<TableColumnVO>();
								tableDefVO.setColumnList(columnList);
							}
							TableColumnVO columnVO = new TableColumnVO();
							columnVO.setTabledefid(tableDefVO.getTabledefid());
							columnVO.setTablecolumnid(rs
									.getLong("tablecolumnid"));
							columnVO.setColcode(rs.getString("colcode"));
							columnVO.setTable_code(rs.getString("col_tbname"));
							columnVO.setColtype(rs.getString("coltype"));
							columnVO.setOra_coltype(rs.getString("ora_coltype"));
							columnVO.setCollen(Integer.parseInt(rs
									.getString("collen")==null?"0":rs
											.getString("collen")));
							columnVO.setColscale(Integer.parseInt(rs
									.getString("colscale")==null?"0":rs
											.getString("colscale")));
							columnVO.setColname(rs.getString("colname"));
							columnVO.setColdesc(rs.getString("coldesc"));
							columnVO.setIspk(rs.getInt("ispk"));
							columnVO.setColdefault(rs.getString("coldefault"));
							columnVO.setIsnotnull(rs.getInt("isnotnull"));
							columnVO.setIsautoinc(rs.getInt("isautoinc"));
							columnVO.setGencode(rs.getString("gencode"));
							columnVO.setColorder(rs.getInt("colorder"));
							columnVO.setCreated_by(rs.getLong("col_created_by"));
							Timestamp createdDt = rs
									.getTimestamp("col_created_dt");
							if (createdDt != null)
								columnVO.setCreated_dt(new Date(createdDt
										.getTime()));
							columnVO.setUpdated_by(rs.getLong("col_updated_by"));
							Timestamp updatedDt = rs
									.getTimestamp("col_updated_dt");
							if (updatedDt != null)
								columnVO.setUpdated_dt(new Date(updatedDt
										.getTime()));
							columnVO.setVer(rs
									.getInt("col_ver"));
							columnVO.setDf(rs
									.getInt("col_df"));
							columnList.add(columnVO);

							if (columnVO.getIspk()== SyConstant.SY_TRUE){
								tableDefVO.setPkColumnVO(columnVO);
								//处理存在联合主键时涉及字段的存储
								if(tableDefVO.getPkColumnVOList()==null){
									List<TableColumnVO> pkColumnVOList=new ArrayList<TableColumnVO>();
									pkColumnVOList.add(columnVO);
									tableDefVO.setPkColumnVOList(pkColumnVOList);
								}else{
									tableDefVO.getPkColumnVOList().add(columnVO);
								}
							}
						}
						return tableDefVO;
					}

				}, "sy_table_def");
		if (ObjectUtil.isNotEmpty(resultList))
			return resultList.get(0);
		return null;
	}

	
	public Long getTableDefPKOfTable(String tableName)
	{
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		Map<String,Object> whereParamMap = new HashMap<String,Object>();
		whereParamMap.put(TableColumnVoConstants.TABLE_CODE, tableName);
		dynaSqlVO.setWhereParamMap(whereParamMap);
		VOSet<TableDefVO> voSet = baseService.query(TableDefVO.class, dynaSqlVO);
		if(voSet.getVoList()==null||voSet.getVoList().size()==0 ){
			ITableDefService tableDefS = (ITableDefService)AppServiceHelper.findBean("tableDefService");
			return tableDefS.insert(tableName).getTabledefid();
		}else if(voSet.getVoList().size()>1)
		{
			throw new BaseRuntimeException("表定义存在【"+tableName+"]的多个定义");
		}else
		{
			return voSet.getVO(0).getTabledefid();
		}
	}
	
	public TableDefVO insert(String tableName)
	{
		TableDefVO vo = new TableDefVO();
		vo.setTable_code(vo.getTableName());
		vo.setTenantid(DbEnvUtils.getTenantId());
		vo.setTable_code(tableName.toLowerCase());
		vo.setIssqllog(SyConstant.SY_FALSE);
		return baseService.insert(vo);
	}
	
	
	

	@Override
	public String getPkColName(ITableInfoCallback tableInfoCallback) {
		String tableName = tableInfoCallback.getTableName().toLowerCase();
		ITableDefService tableDefService = (ITableDefService)AppServiceHelper.findBean("tableDefService");
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		if(StringUtils.isEmpty(pkColName))
			throw new IllegalArgumentException("Can't find pkColName of the table["+tableName+"].");
		//ObjectUtil.validNotNull(pkColName, "Can't find pkColName of the table["+tableName+"].");
		return pkColName.toLowerCase();
	}

	@Override
	public String getPkColName(String tableName) {
		ITableDefService tableDefService = (ITableDefService)AppServiceHelper.findBean("tableDefService");
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName.toLowerCase());
		String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		if(StringUtils.isEmpty(pkColName))
			throw new IllegalArgumentException("Can't find pkColName of the table["+tableName.toLowerCase()+"].");
		//ObjectUtil.validNotNull(pkColName, "Can't find pkColName of the table["+tableName.toLowerCase()+"].");
		return pkColName.toLowerCase();
	}

	
	@Override
	public boolean isSupportConsistencyValidate(String tableName) {
		ITableDefService tableDefService = (ITableDefService)AppServiceHelper.findBean("tableDefService");
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		
		for(TableColumnVO columnVO : tableDefVO.getColumnList()) {
			if(TS_COLUMN_NAME.equalsIgnoreCase(columnVO.getColcode())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends AbstractVO> getVOClass(TableDefVO tableDefVO) {
		ObjectUtil.validNotNull(tableDefVO, "tableDefVO is required.");
		if(ObjectUtil.isNotNull(tableDefVO.getClassname())){
			try {
				return (Class<? extends AbstractVO>) ResourceUtil.classForName(tableDefVO.getClassname());
			} catch (ClassNotFoundException e) {
				logger.error("以下异常信息不影响系统运行...");
				logger.error(e.getMessage(), e);
				return BaseVO.class;
			}
		}
		return BaseVO.class;
	}

	@Override
	public boolean isNeedHandleInnercode(String tableName) {
		ITableDefService tableDefService = (ITableDefService)AppServiceHelper.findBean("tableDefService");
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		
		for(TableColumnVO columnVO : tableDefVO.getColumnList()) {
			if(INNERCODE_COLUMN_NAME.equalsIgnoreCase(columnVO.getColcode())) {
				return true;
			}
		}
		return false;
	}
}
