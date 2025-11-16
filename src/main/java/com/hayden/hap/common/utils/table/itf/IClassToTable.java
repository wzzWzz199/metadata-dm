package com.hayden.hap.common.utils.table.itf;

import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.utils.table.entity.ColumnDTO;

import java.util.List;

/**
 * 从类定义到数据库持久化的操作接口
 * @author zhangfeng
 * @date 2015年11月19日
 */
public interface IClassToTable {

	/**
	 * 获取表名
	 * @param clazz
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	public String getTableName(Class<? extends BaseVO> clazz);
	
	/**
	 * 获取表字段信息
	 * @param clazz
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	public List<ColumnDTO> getColumns(Class<? extends BaseVO> clazz) throws Exception;
	
	/**
	 * 为mysql数据库创建表
	 * @param clazz
	 * @throws Exception 
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	public void createTableForMysql(Class<? extends BaseVO> clazz) throws Exception;
	
	/**
	 * 为类初始化sy_table_column表数据
	 * @param clazz
	 * @throws Exception 
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	public void insertColumnTable(Class<? extends BaseVO> clazz) throws Exception;
	
	/**
	 * 为类初始化sy_table_def表数据
	 * @param clazz
	 * @throws Exception 
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	public void insertTableDef(Class<? extends BaseVO> clazz) throws Exception;
}
