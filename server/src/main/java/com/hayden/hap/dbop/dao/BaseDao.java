package com.hayden.hap.dbop.dao;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/** 
 * @ClassName: BaseDao 
 * @Description: 
 * @author LUYANYING
 * @date 2015年3月24日 上午11:52:05 
 * @version V1.0   
 *  
 */
public interface BaseDao {
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo);
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @param dataSourceId 执行操作的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, String dataSourceId);
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @param dataSourceId 执行操作的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行操作的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: insert 
	 * @Description: 新增
	 * @param vo 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行操作的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * insertBatchHavePks:(vo包含pks，不需要自动生成pk). <br/>
	 * date: 2016年4月29日 <br/>
	 *
	 * @author ZhangJie
	 * @param voList
	 * @return
	 */
	public <T extends AbstractVO> VOSet<T> insertBatchHavePks(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize);
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList);
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dataSourceId 执行新增的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, String dataSourceId);
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dataSourceId 执行新增的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param perBatchSize 批量处理时每批次处理记录数
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, int perBatchSize);
	
	
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行新增的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行新增的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行新增的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param perBatchSize 批量处理时每批次处理记录数
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName, int perBatchSize);
	
	
	/**
	 * 
	 * @Title: insertBatch 
	 * @Description: 批量新增
	 * @param voList 实体对象
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param perBatchSize 批量处理时每批次处理记录数
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, int perBatchSize);
	
	
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo);
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, String dataSourceId);
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @param dynaSqlVO 用于动态拼接SQL的对象 这里用于获取更新字段、更新条件以及是否开启预编译
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @param dynaSqlVO 用于动态拼接SQL的对象 这里用于获取更新字段、更新条件以及是否开启预编译
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @param dynaSqlVO 用于动态拼接SQL的对象 这里用于获取更新字段、更新条件以及是否开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: update 
	 * @Description: 更新
	 * @param vo 更新的实体对象 包含新值
	 * @param dynaSqlVO 用于动态拼接SQL的对象 这里用于获取更新字段、更新条件以及是否开启预编译
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList);
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dataSourceId 执行更新的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, String dataSourceId);
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dataSourceId 执行更新的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param perBatchSize 批量处理时每批次处理记录数
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, int perBatchSize);
	
	
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行更新的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行更新的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId 执行更新的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param perBatchSize 批量处理时每批次处理记录数
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName, int perBatchSize);
	
	/**
	 * 
	 * @Title: updateBatch 
	 * @Description: 批量更新
	 * @param voList 更新的实体对象集合
	 * @param dynaSqlVO 动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @param perBatchSize 批量处理时每批次处理记录数
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, int perBatchSize);
	
	
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, String dataSourceId);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param vo 实体对象
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param tableName 数据库表名  用于拼接删除sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param tableName 数据库表名  用于拼接删除sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param tableName 数据库表名  用于拼接删除sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除
	 * @param tableName 数据库表名  用于拼接删除sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: deleteBatch 
	 * @Description: 批量删除
	 * @param voList 待删除的实体对象
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteBatch(List<T> voList);
	/**
	 * 
	 * @Title: deleteBatch 
	 * @Description: 批量删除
	 * @param voList 待删除的实体对象
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteBatch(List<T> voList, String dataSourceId);
	/**
	 * 
	 * @Title: deleteBatch 
	 * @Description: 批量删除
	 * @param voList 待删除的实体对象
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteBatch(List<T> voList, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: deleteBatch 
	 * @Description: 批量删除
	 * @param voList 待删除的实体对象
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteBatch(List<T> voList, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey);
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey, String dataSourceId);
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 待删除的主键值
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param tableName 表名
	 * @param primaryKey 待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPK(String tableName, String primaryKey);
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param tableName 表名
	 * @param primaryKey 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPK(String tableName, String primaryKey, String dataSourceId);
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param tableName 表名
	 * @param primaryKey 待删除的主键值
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPK(String tableName, String primaryKey, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: deleteByPK 
	 * @Description: 按主键删除
	 * @param tableName 表名
	 * @param primaryKey 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPK(String tableName, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys);
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys, String dataSourceId);
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 待删除的主键值
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo, Collection<Long> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);
		
	
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param tableName 表名
	 * @param primaryKeys 待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPKs(String tableName, Collection<String> primaryKeys);
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param tableName 表名
	 * @param primaryKeys 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPKs(String tableName, Collection<String> primaryKeys, String dataSourceId);
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param tableName 表名
	 * @param primaryKeys 待删除的主键值
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPKs(String tableName, Collection<String> primaryKeys, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param tableName 表名
	 * @param primaryKeys 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPKs(String tableName, Collection<String> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: deleteByPKs 
	 * @Description: 按主键批量删除
	 * @param tableName 表名
	 * @param primaryKeys 待删除的主键值
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPKsOfLong(String tableName, Collection<Long> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 主键值
	 * @return
	 * @return T 查询结果
	 * @throws
	 */
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey);
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 主键值
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return T 查询结果
	 * @throws
	 */
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey, String dataSourceId);
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 主键值
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T 查询结果
	 * @throws
	 */
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 主键值
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T 查询结果
	 * @throws
	 */
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKey 主键值
	 * @param isReadSlave 是否从库进行读取，默认为false 
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T 查询结果
	 * @throws
	 */
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param tableName 表名
	 * @param primaryKey 主键值
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	public AbstractVO queryByPK(String tableName, String primaryKey);
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param tableName 表名
	 * @param primaryKey 主键值
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	public AbstractVO queryByPK(String tableName, String primaryKey, String dataSourceId);
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param tableName 表名
	 * @param primaryKey 主键值
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	public AbstractVO queryByPK(String tableName, String primaryKey, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param tableName 表名
	 * @param primaryKey 主键值
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	public AbstractVO queryByPK(String tableName, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: queryByPK 
	 * @Description: 按主键查询
	 * @param tableName 表名
	 * @param primaryKey 主键值
	 * @param isReadSlave 是否从库进行读取，默认为false 
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	public AbstractVO queryByPK(String tableName, String primaryKey, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @param tableName 表名
	 * @param primaryKey 主键值
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return AbstractVO 查询结果
	 * @author zhangfeng
	 * @date 2016年8月2日
	 */
	public AbstractVO queryByPK4update(String tableName, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: queryByPKs 
	 * @Description: 按主键批量查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 主键值集合
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys);
	/**
	 * 
	 * @Title: queryByPKs 
	 * @Description: 按主键批量查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 主键值集合
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys, String dataSourceId);
	/**
	 * 
	 * @Title: queryByPKs 
	 * @Description: 按主键批量查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 主键值集合
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: queryByPKs 
	 * @Description: 按主键批量查询
	 * @param vo 实体对象 这里用于获取表名而已
	 * @param primaryKeys 主键值集合
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo, Collection<Long> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo, Collection<Long> primaryKeys, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param vo 实体类对象 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param vo 实体类对象 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param vo 实体类对象 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param vo 实体类对象 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

	
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param voClass 实体类class 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param voClass 实体类class 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param voClass 实体类class 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: query 
	 * @Description: 查询
	 * @param voClass 实体类class 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

	
	
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @return
	 * @return VOSet<T> 查询结果集 返回BaseVO的集合
	 * @throws
	 */
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<T> 查询结果集 返回BaseVO的集合
	 * @throws
	 */
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集 返回BaseVO的集合
	 * @throws
	 */
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集 返回BaseVO的集合
	 * @throws
	 */
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO, String dataSourceId);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam sql语句中预编译参数
	 * @param preStatementParamType sql语句中预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[]preStatementParamType, String tableName);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam sql语句中预编译参数
	 * @param preStatementParamType sql语句中预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[]preStatementParamType, String tableName, String dataSourceId);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam sql语句中预编译参数
	 * @param preStatementParamType sql语句中预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[]preStatementParamType, String tableName, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 执行sql语句查询
	 * @param voClass 实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam sql语句中预编译参数
	 * @param preStatementParamType sql语句中预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[]preStatementParamType, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 查询
	 * @param sql 执行查询的sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam 预编译参数
	 * @param resultSetExtractor ResultSet处理对象
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return List<T> 查询结果集
	 * @throws
	 */
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 查询
	 * @param sql 执行查询的sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam 预编译参数
	 * @param resultSetExtractor ResultSet处理对象
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return List<T> 查询结果集
	 * @throws
	 */
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName, String dataSourceId);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 查询
	 * @param sql 执行查询的sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam 预编译参数
	 * @param resultSetExtractor ResultSet处理对象
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return List<T> 查询结果集
	 * @throws
	 */
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: executeQuery 
	 * @Description: 查询
	 * @param sql 执行查询的sql语句
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam 预编译参数
	 * @param resultSetExtractor ResultSet处理对象
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return List<T> 查询结果集
	 * @throws
	 */
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, String tableName);
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, String tableName, String dataSourceId);
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, String tableName, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param preStatementParam sql预编译参数
	 * @param preStatementParamType sql预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName);
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param preStatementParam sql预编译参数
	 * @param preStatementParamType sql预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String dataSourceId);
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param preStatementParam sql预编译参数
	 * @param preStatementParamType sql预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String ruleName, String dataSourcePoolName);
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param preStatementParam sql预编译参数
	 * @param preStatementParamType sql预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: executeUpate 
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql sql语句
	 * @param preStatementParam sql预编译参数
	 * @param preStatementParamType sql预编译参数jdbc类别
	 * @param tableName 数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId 执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param isAddTS 是否更新ts字段。 
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName, boolean isAddTS);
	
	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid);

	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId);

	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String ruleName,
			String dataSourcePoolName);

	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName);


	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @return int 删除记录数
	 */
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid);
	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @return int 删除记录数
	 */
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId);
	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String ruleName, String dataSourcePoolName);

	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId, String ruleName,
			String dataSourcePoolName);

	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys,Long tenantid);
	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys,Long tenantid, String dataSourceId);
	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys,Long tenantid, String ruleName,
			String dataSourcePoolName);
	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys,Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName);
	
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys,Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName);
	
	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 * @return int 删除记录数
	 */
	public int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys,Long tenantid);
	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @return int 删除记录数
	 */
	public int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys,Long tenantid,
			String dataSourceId);
	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys,Long tenantid,
			String ruleName, String dataSourcePoolName);

	/**
	 * deleteByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @Description: 按主键批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return int 删除记录数
	 */
	public int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys,Long tenantid,
			String dataSourceId, String ruleName, String dataSourcePoolName);	

	public int deleteByPKsOfLongAndTenantid(String tableName, Collection<Long> primaryKeys,Long tenantid,
			String dataSourceId, String ruleName, String dataSourcePoolName);	
	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @return T 查询结果
	 */
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid);

	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @return T 查询结果
	 */
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId);

	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return T 查询结果
	 */
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String ruleName,
			String dataSourcePoolName);

	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return T 查询结果
	 */
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName);

	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false           
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return T 查询结果
	 */
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, Boolean isReadSlave, String dataSourceId,
			String ruleName, String dataSourcePoolName);
	
	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 */
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid);

	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 */
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId);

	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 */
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String ruleName, String dataSourcePoolName);

	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 */
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId, String ruleName,
			String dataSourcePoolName);
	
	/**
	 * queryByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false           
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return AbstractVO 查询结果
	 */
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, Boolean isReadSlave, String dataSourceId, String ruleName,
			String dataSourcePoolName);
	
	/**
	 * 
	 * @param tableName
	 * @param primaryKey
	 * @param tenantid
	 * @param dataSourceId
	 * @param ruleName
	 * @param dataSourcePoolName
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月2日
	 */
	public AbstractVO queryByPKAndTenantid4update(String tableName,
			Long primaryKey, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName);

	/**
	 * queryByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param tenantid
	 *            租户id
	 * @return VOSet<T> 查询结果
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid);

	/**
	 * queryByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return VOSet<T> 查询结果
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId);

	/**
	 * queryByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param tenantid
	 *            租户id
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return VOSet<T> 查询结果
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName);

	/**
	 * queryByPKsAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @Description: 按主键查询
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param tenantid
	 *            租户id
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return VOSet<T> 查询结果
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName);	
	
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName);	
	
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, Boolean isReadSlave, String dataSourceId,
			String ruleName, String dataSourcePoolName);
	
	/**
	 * 
	 * @Title: getVOPkColValue 
	 * @Description: 获取实体对象的主键值
	 * @param vo
	 * @return
	 * @return Object
	 * @throws
	 */
	public Object getVOPkColValue(AbstractVO vo);
	
	
	public List<TableColumnVO> getTableColVoList(String catalog,String schema,String tableName,Long parentid);

	
	/**
	 * getCount:(只查询记录数). <br/>
	 * date: 2015年12月31日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @return
	 */
	public int getCount(String tableName, DynaSqlVO dynaSqlVO);
		
	
	/**
	 * getCount:(只查询记录数). <br/>
	 * date: 2015年12月31日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 */
	public int getCount(String tableName, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);
		
	
	/**
	 * getCount:(只查询记录数). <br/>
	 * date: 2015年12月31日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @return
	 */
	public int getCount(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId);
		
	/**
	 * getCount:(只查询记录数). <br/>
	 * date: 2015年12月31日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName 数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO 用于动态拼接SQL的对象,用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 */
	public int getCount(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);
	
	/**
	 * 返回表定义vo的建表语句
	 * @param dbType 数据库类型，mysql、oracle
	 * @param tableDefVO 表定义vo
	 * @return map数据，key为数据库类型，value为表定义相关的建表语句
	 * @throws HDException 
	 * @author wangyi
	 * @date 2017年11月9日
	 */
	public Map<String,List<String>> getCreateTableSql(String dbType, List<TableDefVO> tableDefVO) throws HDException;
	
}
