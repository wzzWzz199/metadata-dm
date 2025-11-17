package com.hayden.hap.dbop.db.keyGen.service;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.service.BaseServiceImpl;
import com.hayden.hap.dbop.db.keyGen.entity.SerialGeneratorVO;
import com.hayden.hap.dbop.db.keyGen.itf.ISerialGeneratorService;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 
 * @ClassName: SerialGeneratorServiceImpl 
 * @Description: 
 * @author LUYANYING
 * @date 2015年5月26日 上午11:02:17 
 * @version V1.0   
 *  
 */
@Service("serialGeneratorService")
public class SerialGeneratorServiceImpl extends BaseServiceImpl implements ISerialGeneratorService {

	private static final Logger logger = LoggerFactory.getLogger(SerialGeneratorServiceImpl.class);
	
	public static final String DEFAULT_GENCODE = "default";
	private Map<String, SerialGeneratorVO> serialGeneratorCacheMap = new HashMap<String, SerialGeneratorVO>();
	@Autowired
	private RedisTemplate redisTemplate;
	@Value("${min.genstart:2000000000000}")
	private Long minGenstart;
	@Override
	public String generate() {
		// TODO Auto-generated method stub
		return generate(DEFAULT_GENCODE);
	}
	
	public String generate(String gencode){
		ObjectUtil.validNotNull(gencode, "gencode is required.");
		gencode = gencode.toLowerCase();
		//利用redis计数实现自增主键的获取
		if (!redisTemplate.hasKey(gencode)){
			RedisAtomicLong counter = new RedisAtomicLong(gencode, redisTemplate.getConnectionFactory());
			Long maxKey=minGenstart;
			if (gencode.equals(DEFAULT_GENCODE)){
				String getMaxkeySql = "select  gennext as maxkey from sy_serial_generator where gencode = 'default'";
				maxKey = getMaxKey(maxKey, getMaxkeySql);
			}else {
				DynaSqlVO sql = new DynaSqlVO();
				sql.addWhereParam("ispk", 1);
				sql.addWhereParam("gencode", gencode);
				VOSet<TableColumnVO> primaryKeys = this.query(TableColumnVO.class, sql);
				for (TableColumnVO columnVO : primaryKeys.getVoList()) {
					String getMaxkeySql = "select max("+columnVO.getColcode()+") as maxkey from "+columnVO.getTable_code();
					maxKey = getMaxKey(maxKey, getMaxkeySql);
				}
			}
			counter.set(maxKey);
			return String.valueOf(counter.incrementAndGet());
		}else{
			RedisAtomicLong counter = new RedisAtomicLong(gencode, redisTemplate.getConnectionFactory());
			return String.valueOf(counter.incrementAndGet());
		}
	}

	private Long getMaxKey(Long maxKey, String getMaxkeySql) {
		VOSet<AbstractVO> abstractVOVOSet = this.executeQuery(getMaxkeySql, null);
		if (abstractVOVOSet.getVoList() != null && !abstractVOVOSet.getVoList().isEmpty() && abstractVOVOSet.getVoList().get(0).get("maxkey") != null) {
			Long maxKeyTemp = abstractVOVOSet.getVoList().get(0).getLong("maxKey");
			if (maxKey.longValue() < maxKeyTemp.longValue()) {
				maxKey = maxKeyTemp.longValue();
			}
		}
		return maxKey;
	}

	private String getFromCache(String gencode){
		SerialGeneratorVO serialGeneratorCache = serialGeneratorCacheMap.get(gencode);
		if(serialGeneratorCache.getCounter()<serialGeneratorCache.getGencache().longValue()){
			String value = (serialGeneratorCache.getGenprefix()!=null?serialGeneratorCache.getGenprefix():"") + serialGeneratorCache.getGennext();
			serialGeneratorCache.setCounter(serialGeneratorCache.getCounter()+1);
			serialGeneratorCache.setGennext((Long.parseLong(serialGeneratorCache.getGennext())+1)+"");
			return value;
		}
		else{
			ISerialGeneratorService self = (ISerialGeneratorService) AppServiceHelper.findBean("serialGeneratorService");
			self.loadFromDb_RequiresNew(gencode);
			return getFromCache(gencode);
		}
	}
	
	public void loadFromDb_RequiresNew(String gencode){
		loadFromDb_RequiresNew(gencode, -1);
	}
	
	public void loadFromDb_RequiresNew(String gencode, int batchSize){
		logger.error("进入读取数据库自增主键记录并更新时间："+new Date().getTime()+",锁为："+gencode);
		SerialGeneratorVO serialGeneratorVO = null;
//		DynaSqlVO dynaSqlVO = new DynaSqlVO();
//		dynaSqlVO.addWhereParam("gencode", gencode);
//		dynaSqlVO.setSelectForUpdate(true);
		VOSet<SerialGeneratorVO> voSet = this.executeQuery(SerialGeneratorVO.class, "select * from sy_serial_generator where gencode = '"+gencode+"' for update", null);//this.query(SerialGeneratorVO.class, dynaSqlVO);
		logger.error("查询数据库主键记录结束时间："+new Date().getTime()+",锁为："+gencode);
		if(voSet.getVoList()!=null && !voSet.getVoList().isEmpty())
			serialGeneratorVO = voSet.getVoList().get(0);
		if(serialGeneratorVO == null)
			throw new IllegalArgumentException("请先配置序列号【"+gencode+"】！");
		
		String gennext = serialGeneratorVO.getGennext();
		//批量获取主键
		long genCache = serialGeneratorVO.getGencache().longValue();
		//当为批量获取，并且库中定义的阈值小于待取的数值时，使用batchSize值
		if(batchSize!=-1){
			//如果申请的大于设置的阈值，需要修改为申请的量级
			if(batchSize>genCache){
				genCache = Long.valueOf(batchSize).longValue();
			}			
		}
		serialGeneratorVO.setGennext((Long.parseLong(serialGeneratorVO.getGennext())+genCache)+"");
		this.update(serialGeneratorVO);
		logger.error("更新自增主键记录结束时间："+new Date().getTime()+",锁为："+gencode);
		serialGeneratorVO.setCounter(0);
		serialGeneratorVO.setGennext(gennext);
		//重新赋值
		serialGeneratorVO.setGencache(Long.valueOf(genCache));
		serialGeneratorCacheMap.put(gencode, serialGeneratorVO);
		logger.error("结束读取数据库自增主键记录并更新时间："+new Date().getTime()+",锁为："+gencode);
	}
	
	private String[] getFromCache(String gencode, int batchSize){
		//首先判断缓存中值是否够用，不够用读取数据库获取一次主键值
				SerialGeneratorVO serialGeneratorCache = serialGeneratorCacheMap.get(gencode);
		long remainVal = serialGeneratorCache.getGencache().longValue() -
				serialGeneratorCache.getCounter();
		if(remainVal<batchSize){
			ISerialGeneratorService self = (ISerialGeneratorService) AppServiceHelper.findBean("serialGeneratorService");
			self.loadFromDb_RequiresNew(gencode, batchSize);
		}
		
		String[] keys = new String[batchSize];
		for(int i =0;i<batchSize;i++){
			keys[i] = this.getFromCache(gencode);
		}
		return keys;
	}
	@Override
	public String[] generate(String gencode, int batchSize) {
		ObjectUtil.validNotNull(gencode, "gencode is required.");
		gencode = gencode.toLowerCase();
		//利用redis计数实现自增主键的获取
		RedisAtomicLong counter = null;
		if (!redisTemplate.hasKey(gencode)){
			counter = new RedisAtomicLong(gencode, redisTemplate.getConnectionFactory());
			Long maxKey=minGenstart;
			if (gencode.equals(DEFAULT_GENCODE)){
				String getMaxkeySql = "select  gennext as maxkey from sy_serial_generator where gencode = 'default'";
				maxKey = getMaxKey(maxKey, getMaxkeySql);
			}else {
				DynaSqlVO sql = new DynaSqlVO();
				sql.addWhereParam("ispk", 1);
				sql.addWhereParam("gencode", gencode);
				VOSet<TableColumnVO> primaryKeys = this.query(TableColumnVO.class, sql);
				for (TableColumnVO columnVO : primaryKeys.getVoList()) {
					String getMaxkeySql = "select max("+columnVO.getColcode()+") as maxkey from "+columnVO.getTable_code();
					maxKey = getMaxKey(maxKey, getMaxkeySql);
				}
			}
			counter.set(maxKey);
		}else{
			counter = new RedisAtomicLong(gencode, redisTemplate.getConnectionFactory());
		}
		String[] keys = new String[batchSize];
		for(int i =0;i<batchSize;i++){
			keys[i] = String.valueOf(counter.incrementAndGet());
		}
		return keys;
	}

	@Override
	public String[] generate(int batchSize) {
		return generate(DEFAULT_GENCODE, batchSize);
	}


}
