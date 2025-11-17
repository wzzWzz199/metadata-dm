package com.hayden.hap.dbop.db.temptable.itf;

import java.util.Collection;

/**
 * <p>mysql和oracle临时表差异：</br>
 *      mysql临时表基于连接，不同的连接是相互不可见的，同一时间，不同的连接可以创建相同的临时表结构，数据当然也不会相互影响；连接断开后表结构也被删除</br>
 *      oracle临时表表结构全局可见，当一个用户已建立一个临时表，其它用户则不可建重名临时表；但用户数据相互独立，不可见；表结构永久存在</br>
 *      oracle临时表有两种：</br>
 *      	a)一种是基于会话的；只要在同一会话中，你可以使用查询你的数据,一旦退出会话，你的数据将被截断（删除）;</br>
 *          b)一种是基于事务的；就是你的数据在提交之后被截断（删除）;</br>
 *      但我们系统中，由于shiro对其语句不支持，只能是默认方式，即为基于事务的oracle临时表。</br>
 *      
 * @author zhangfeng
 * @date 2014年9月5日
 */
public interface ITempTableService {

	/**
	 * <p>创建临时表；同时插入数据</p>
	 * <P>example：</br>
	 * 	  String[][] columns = {{"NAME","varchar","800"},{"ID","int","20"}};</br>
	 * 	  createTempTable("STUDENT",columns,true);</br>
	 * @param tableName 临时表名称
	 * @param columns 列定义：  为二维数组，格式定义和example一样，一次为：列名称，列字段类型，长度
	 * @param list 数据集合
	 * @param dataSourceId 数据源
	 * @return 
	 */
	<T> void createAndInsertData(String tableName,String[][] columns,Collection<T> list, String dataSourceId);
	
	/**
	 * 删除临时表
	 * @param tableName 临时表名称
	 * @param dataSourceId 数据源
	 * @return 
	 */
	void dropTempTable(String tableName, String dataSourceId);
}
