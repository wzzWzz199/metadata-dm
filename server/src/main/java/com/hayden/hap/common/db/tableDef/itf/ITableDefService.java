package com.hayden.hap.common.db.tableDef.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.spring.service.IService;

/** 
 * @ClassName: ITableService 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月15日 上午10:12:57 
 * @version V1.0   
 *  
 */
@IService("tableDefService")
public interface ITableDefService {
	/**
	 * querySyAllTables:(获取sy所有表定义，并进行缓存). <br/>
	 * date: 2016年11月4日 <br/>
	 *
	 * @author wangyi
	 * @return
	 */
	public VOSet<TableDefVO> querySyAllTables() ;
	public TableDefVO queryDetailedTableByTbname(String tbname);
	
	public String getPkColName(String tableName);
	
	/**
	 * insert:(表定义主记录). <br/>
	 * date: 2016年3月22日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName
	 * @return
	 */
	public TableDefVO insert(String tableName);
	
	public Long getTableDefPKOfTable(String tableName);
	/**
	 * 获取主键列列名
	 * @param tableInfoCallback
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月21日
	 */
	public String getPkColName(ITableInfoCallback tableInfoCallback);
	
	/**
	 * 判断是否支持一致性校验
	 * @param tableName
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月20日
	 */
	boolean isSupportConsistencyValidate(String tableName);
	
	/**
	 * 根据表定义获取实体类
	 * @param tableDefVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月7日
	 */
	Class<? extends AbstractVO> getVOClass(TableDefVO tableDefVO);
	
	/**
	 * 该表需要处理内部编码
	 * @param tableName
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月24日
	 */
	boolean isNeedHandleInnercode(String tableName);
}
