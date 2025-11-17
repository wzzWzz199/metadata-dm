package com.hayden.hap.common.common.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.spring.service.IService;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BaseService
 * @Description:
 * @author LUYANYING
 * @date 2015年4月15日 上午9:59:04
 * @version V1.0
 * 
 */
@IService("baseService")
public interface IBaseService {
	/**
	 * 
	 * @Title: insert
	 * @Description: 新增
	 * @param vo
	 *            实体对象
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo);

	/**
	 * 
	 * @Title: insert
	 * @Description: 新增
	 * @param vo
	 *            实体对象
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @return
	 * @return T
	 * @throws
	 */
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: insertBatch
	 * @Description: 批量新增
	 * @param voList
	 *            实体对象
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList);
	/**
	 * insertBatchHavePks:(vo已经存在主键，不要申请新的主键). <br/>
	 * date: 2016年4月29日 <br/>
	 *
	 * @author ZhangJie
	 * @param voList
	 * @return
	 */
	public <T extends AbstractVO> VOSet<T> insertBatchHavePks(List<T> voList);

	/**
	 * 
	 * @Title: insertBatch
	 * @Description: 批量新增
	 * @param voList
	 *            实体对象
	 * @param perBatchSize
	 *            批量处理时每批次处理记录数
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			int perBatchSize);

	/**
	 * 
	 * @Title: insertBatch
	 * @Description: 批量新增
	 * @param voList
	 *            实体对象
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: insertBatch
	 * @Description: 批量新增
	 * @param voList
	 *            实体对象
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param perBatchSize
	 *            批量处理时每批次处理记录数
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize);

	/**
	 * 
	 * @Title: update
	 * @Description: 更新
	 * @param vo
	 *            更新的实体对象 包含新值
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo);

	/**
	 * 
	 * @Title: update
	 * @Description: 更新
	 * @param vo
	 *            更新的实体对象 包含新值
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象 这里用于获取更新字段、更新条件以及是否开启预编译
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: updateBatch
	 * @Description: 批量更新
	 * @param voList
	 *            更新的实体对象集合
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList);

	/**
	 * 
	 * @Title: updateBatch
	 * @Description: 批量更新
	 * @param voList
	 *            更新的实体对象集合
	 * @param perBatchSize
	 *            批量处理时每批次处理记录数
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			int perBatchSize);

	/**
	 * 
	 * @Title: updateBatch
	 * @Description: 批量更新
	 * @param voList
	 *            更新的实体对象集合
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: updateBatch
	 * @Description: 批量更新
	 * @param voList
	 *            更新的实体对象集合
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @param perBatchSize
	 *            批量处理时每批次处理记录数
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize);

	/**
	 * updateBatch:(批量更新，需要设置每条数据更新的列，voList.size()=dynaSqlVOList.size() 一一对应). <br/>
	 * date: 2015年11月3日 <br/>
	 *
	 * @author ZhangJie
	 * @param voList
	 * @param dynaSqlVOList
	 * @return
	 */
	public <T extends AbstractVO> int updateBatchForList(List<T> voList,
			List<DynaSqlVO> dynaSqlVOList);

	
	/**
	 * 
	 * @Title: delete
	 * @Description: 删除
	 * @param vo
	 *            实体对象
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo);

	/**
	 * 
	 * @Title: delete
	 * @Description: 删除
	 * @param vo
	 *            实体对象
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO);

	
	/**
	 * 
	 * @Title: delete
	 * @Description: 删除
	 * @param tableName
	 *            数据库表名 用于拼接删除sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译,如果对象为空，将删除表所有数据
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(String tableName,
			DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: deleteBatch
	 * @Description: 批量删除
	 * @param voList
	 *            待删除的实体对象
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteBatch(List<T> voList);

	/**
	 * 
	 * @Title: deleteByPK
	 * @Description: 按主键删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	@Deprecated
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey);

	/**
	 * 
	 * @Title: deleteByPK
	 * @Description: 按主键删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPK(T vo, Long primaryKey);
	
	
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
	 */
	@Deprecated
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
	 */
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			Long primaryKey, Long tenantid);	

	/**
	 * 
	 * @Title: deleteByPK
	 * @Description: 按主键删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	@Deprecated
	public int deleteByPK(String tableName, String primaryKey);

	/**
	 * 
	 * @Title: deleteByPK
	 * @Description: 按主键删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPK(String tableName, Long primaryKey);	
	
	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键和租户id删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 */
	@Deprecated
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid);

	/**
	 * deleteByPKAndTenantid:(). <br/>
	 * date: 2015年11月21日 <br/>
	 * 
	 * @author ZhangJie
	 * @Description: 按主键和租户id删除
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            待删除的主键值
	 * @param tenantid
	 */
	public int deleteByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid);

	/**
	 * 
	 * @Title: deleteByPKs
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	@Deprecated
	public <T extends AbstractVO> int deleteByPKs(T vo,
			Collection<String> primaryKeys);
	
	/**
	 * 
	 * @Title: deleteByPKs
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo,
			Collection<Long> primaryKeys);

	/**
	 * @Title: deleteByPKsAndTenantid
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @return int 删除记录数
	 */
	@Deprecated
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid);
	
	/**
	 * @Title: deleteByPKsAndTenantid
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @return int 删除记录数
	 */
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid);
	
	/**
	 * 
	 * @Title: deleteByPKs
	 * @Description: 按主键批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	@Deprecated
	public int deleteByPKs(String tableName, Collection<String> primaryKeys);

	/**
	 * 
	 * @Title: deleteByPKs
	 * @Description: 按主键批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public int deleteByPKsOfLong(String tableName, Collection<Long> primaryKeys);	
	
	/**
	 * @Title: deleteByPKsAndTenantid
	 * @Description: 按主键和租户id批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @return int 删除记录数
	 */
	@Deprecated
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid);
	
	/**
	 * @Title: deleteByPKsAndTenantid
	 * @Description: 按主键和租户id批量删除
	 * @param tableName
	 *            表名
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param tenantid
	 *            租户id
	 * @return int 删除记录数
	 */
	public int deleteByPKsOfLongAndTenantid(String tableName,
			Collection<Long> primaryKeys, Long tenantid);	

	/**
	 * 
	 * @Title: queryByPK
	 * @Description: 按主键查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @return
	 * @return T 查询结果
	 * @throws
	 */
	@Deprecated
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey);
	/**
	 * @deprecated 该方法不支持传入租户参数
	 * 后续建议使用下面方法代替：queryByPKAndTenantid(T vo, Long primaryKey, Long tenantid)
	 * @Title: queryByPK
	 * @Description: 按主键查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @return
	 * @return T 查询结果
	 * @throws
	 */	
	@Deprecated
	public <T extends AbstractVO> T queryByPK(T vo, Long primaryKey);

	/**
	 * @deprecated 该方法不支持传入租户参数
	 * 后续建议使用下面方法代替：queryByPKAndTenantid(T vo, Long primaryKey, Long tenantid, Boolean isReadSlave)
	 * @Title: queryByPK
	 * @Description: 按主键查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false            
	 * @return
	 * @return T 查询结果
	 * @throws
	 */	
	@Deprecated
	public <T extends AbstractVO> T queryByPK_ReadSlave(T vo, Long primaryKey);
	
	/**
	 * queryByPKAndTenantid:(根据主键和租户id查询表数据). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @return T 查询结果
	 */
	@Deprecated
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid);
	
	
	/**
	 * queryByPKAndTenantid:(根据主键和租户id查询表数据). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @return T 查询结果
	 */
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			Long primaryKey, Long tenantid);	
	
	/**
	 * queryByPKAndTenantid:(根据主键和租户id查询表数据). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false           
	 * @return T 查询结果
	 */
	public <T extends AbstractVO> T queryByPKAndTenantid_ReadSlave(T vo,
			Long primaryKey, Long tenantid);
	
	/**
	 * 
	 * @Title: queryByPK
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	@Deprecated
	public AbstractVO queryByPK(String tableName, String primaryKey);

	
	/**
	 * @deprecated 该方法不支持传入租户参数
	 * 后续建议使用下面方法代替：queryByPKAndTenantid(String tableName, Long primaryKey, Long tenantid)
	 * @Title: queryByPK
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	@Deprecated
	public AbstractVO queryByPK(String tableName, Long primaryKey);	
	
	/**
	 * @deprecated 该方法不支持传入租户参数
	 * 后续建议使用下面方法代替：queryByPKAndTenantid(String tableName, Long primaryKey, Long tenantid, Boolean isReadSlave)
	 * @Title: queryByPK
	 * @Description: 按主键查询
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false           
	 * @return
	 * @return AbstractVO 查询结果
	 * @throws
	 */
	@Deprecated
	public AbstractVO queryByPK_ReadSlave(String tableName, Long primaryKey);	
		
	/**
	 * queryByPKAndTenantid:(按表名+主键+租户id查询). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @return AbstractVO 查询结果
	 */
	@Deprecated
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid);
	
	/**
	 * queryByPKAndTenantid:(按表名+主键+租户id查询). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @return AbstractVO 查询结果
	 */
	public AbstractVO queryByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid);
	
	/**
	 * queryByPKAndTenantid:(按表名+主键+租户id查询). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName
	 *            表名
	 * @param primaryKey
	 *            主键值
	 * @param tenantid
	 *            租户id
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false           
	 * @return AbstractVO 查询结果
	 */
	public AbstractVO queryByPKAndTenantid_ReadSlave(String tableName, Long primaryKey,	Long tenantid);
	
	/**
	 * 
	 * @param tableName
	 * @param primaryKey
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月2日
	 */
	public AbstractVO queryByPKAndTenantid4update(String tableName, Long primaryKey,
			Long tenantid);	
	
	/**
	 * 
	 * @Title: queryByPKs
	 * @Description: 按主键批量查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	@Deprecated
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo,
			Collection<String> primaryKeys);
	
	/**
	 * @deprecated 该方法不支持传入租户参数
	 * 后续建议使用下面方法代替：queryByPKsOfLongAndTenantid(T vo, Collection<Long> primaryKeys, Long tenantid)
	 * @Title: queryByPKs
	 * @Description: 按主键批量查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	@Deprecated
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys);	

	/**
	 * @deprecated 该方法不支持传入租户参数
	 * 后续建议使用下面方法代替：queryByPKsOfLongAndTenantid(T vo, Collection<Long> primaryKeys, Long tenantid, Boolean isReadSlave)
	 * @Title: queryByPKs
	 * @Description: 按主键批量查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false           
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	@Deprecated
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong_ReadSlave(T vo,
			Collection<Long> primaryKeys);
	
	/**
	 * queryByPKsAndTenantid:(按主键和租户号批量查询). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param tenantid
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 */
	@Deprecated
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid);
	
	/**
	 * queryByPKsAndTenantid:(按主键和租户号批量查询). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param tenantid
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid);	

	/**
	 * queryByPKsAndTenantid:(按主键和租户号批量查询). <br/>
	 * date: 2015年11月20日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param tenantid
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid_ReadSlave(T vo,
			Collection<Long> primaryKeys, Long tenantid);
	
	/**
	 * 
	 * @Title: query
	 * @Description: 查询
	 * @param vo
	 *            实体类对象 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: query
	 * @Description: 查询
	 * @param tableName
	 *            数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: query
	 * @Description: 查询
	 * @param voClass
	 *            实体类class 用于获取表名及查询结果集映射到该实体类,必填
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @return
	 * @return VOSet<T>查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass,
			DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: executeQuery
	 * @Description: 执行sql语句查询
	 * @param sql
	 *            sql语句
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取分页信息
	 * @return
	 * @return VOSet<T> 查询结果集 返回BaseVO的集合
	 * @throws
	 */
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: executeQuery
	 * @Description: 执行sql语句查询
	 * @param voClass
	 *            实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql
	 *            sql语句
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取分页信息
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO);

	/**
	 * 
	 * @Title: executeQuery
	 * @Description: 执行sql语句查询
	 * @param voClass
	 *            实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql
	 *            sql语句
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam
	 *            sql语句中预编译参数
	 * @param preStatementParamType
	 *            sql语句中预编译参数jdbc类别
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			final String sql, DynaSqlVO dynaSqlVO,
			final Object[] preStatementParam,
			final int[] preStatementParamType, String tableName);
	
	/**
	 * 
	 * @Title: executeQuery
	 * @Description: 查询
	 * @param sql
	 *            执行查询的sql语句
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam
	 *            预编译参数
	 * @param resultSetExtractor
	 *            ResultSet处理对象
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return List<T> 查询结果集
	 * @throws
	 */
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			Object[] preStatementParam,
			ResultSetExtractor<T> resultSetExtractor, String tableName);
	
	/**
	 * 
	 * @Title: executeUpate
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql
	 *            sql语句
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, String tableName);
	
	/**
	 * wangyi add。
	 * 当使用sql做更新操作时，某些sql可能不需要对ts字段数据进行更新。
	 * 此时，可以设置参数isAddTS值为false。
	 * @Title: executeUpate
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql
	 *            sql语句
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param isAddTS
	 *            执行更新时，是否更新ts字段。         
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, String tableName, boolean isAddTS);

	/**
	 * 
	 * @Title: executeUpate
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql
	 *            sql语句
	 * @param preStatementParam
	 *            sql预编译参数
	 * @param preStatementParamType
	 *            sql预编译参数jdbc类别
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, Object[] preStatementParam,
			int[] preStatementParamType, String tableName);

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
	
	/**
	 * getCount:(获得总数). <br/>
	 * date: 2015年12月30日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName
	 *            数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 */
	public int getCount(String tableName, DynaSqlVO dynaSqlVO);
	
	
	public Map<String,List<String>> getCreateTableSql(String dbType, List<TableDefVO> tableDefVOList) throws HDException;
	
	/**
	 * 校验一致性
	 * @param vo
	 * @return true 校验通过，false校验没通过
	 * @author zhangfeng
	 * @date 2016年7月20日
	 */
	public boolean validateConsistency(AbstractVO vo);
	
	/**
	 * 校验一致性
	 * @param vo
	 * @return true 校验通过，false校验没通过
	 * @author zhangfeng
	 * @date 2016年7月20日
	 */
	public <T extends AbstractVO> boolean validateConsistency(List<T> list);
	
//	/**
//	 * 
//	 * @Title: saveTemporaryTableData
//	 * @Description: 当条件中关联到的
//	 * @param voClass
//	 *            实体类class 用于获取表名及查询结果集映射到该实体类,必填
//	 * @param dynaSqlVO
//	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
//	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
//	 * @param dataSourceId
//	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
//	 *            如果没有路由到数据源则返回默认数据源
//	 * @return
//	 * @return VOSet<T>查询结果封装对象
//	 * @throws
//	 */
//	public <T extends AbstractVO> String saveTemporaryTableData(DynaSqlVO dynaSqlVO);
//	/**
//	 * 
//	 * @Title: query
//	 * @Description: 查询
//	 * @param voClass
//	 *            实体类class 用于获取表名及查询结果集映射到该实体类,必填
//	 * @param dynaSqlVO
//	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
//	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
//	 * @param dataSourceId
//	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
//	 *            如果没有路由到数据源则返回默认数据源
//	 * @return
//	 * @return VOSet<T>查询结果封装对象
//	 * @throws
//	 */
//	public <T extends AbstractVO> String saveTemporaryTableData(DynaSqlVO dynaSqlVO, String dataSourceId);
//	/**
//	 * 
//	 * @Title: query
//	 * @Description: 查询
//	 * @param voClass
//	 *            实体类class 用于获取表名及查询结果集映射到该实体类,必填
//	 * @param dynaSqlVO
//	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
//	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
//	 * @param dataSourceId
//	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
//	 *            如果没有路由到数据源则返回默认数据源
//	 * @return
//	 * @return VOSet<T>查询结果封装对象
//	 * @throws
//	 */
//	public <T extends AbstractVO> String saveTemporaryTableData(List<T> list);
}
