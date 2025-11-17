package com.hayden.hap.db.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.keyGen.entity.SerialGeneratorVO;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.db.dataSource.itf.IDataSourceGeneratorService;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LUYANYING
 * @version V1.0
 * @ClassName: SerialGeneratorServiceImpl
 * @Description:
 * @date 2015年5月26日 上午11:02:17
 */
@Service("dataSourceGeneratorService")
public class dataSourceGeneratorServiceImpl implements IDataSourceGeneratorService {

    @Autowired
    ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;
    @Autowired
    DataSourceCreator dataSourceCreator;
    private static final Logger logger = LoggerFactory.getLogger(dataSourceGeneratorServiceImpl.class);

    public static final String DEFAULT_GENCODE = "default";
    private Map<String, Map<String, SerialGeneratorVO>> projectSerialGeneratorCacheMap = new HashMap<>();

    @Override
    public String getPkColGencode(TableDefVO tableDefVO) {
        String gencode = tableDefVO.getPkColumnVO() != null && tableDefVO.getPkColumnVO().getIspk() == 1 ? tableDefVO.getPkColumnVO().getGencode() : null;
        return gencode;
    }

    @Override
    public String generate(String dataSourceId) throws HDException {
        // TODO Auto-generated method stub
        return generate(DEFAULT_GENCODE, dataSourceId);
    }

    @Override
    public String generate(String gencode, String dataSourceId) throws HDException {
        ObjectUtil.validNotNull(gencode, "gencode is required.");
        gencode = gencode.toLowerCase();
        //先从redis获取自增主键,获取不到说明改环境未使用redis存储自增主键，还是按原逻辑获取自增主键 modify by yinbinchen
        RedisTemplate redisTemplate = dataSourceCreator.getRedisTemplate(dataSourceId);
        if (redisTemplate != null) {
            if (!redisTemplate.hasKey(gencode)){
                RedisAtomicLong counter = new RedisAtomicLong(gencode, redisTemplate.getConnectionFactory());
                Long maxKey=2000000000000L;
                if (gencode.equals(DEFAULT_GENCODE)){
                    String getMaxkeySql = "select  gennext as maxkey from sy_serial_generator where gencode = 'default'";
                    maxKey = getMaxKey(maxKey,getMaxkeySql,dataSourceId);
                }else {
                    DynaSqlVO sql = new DynaSqlVO();
                    sql.addWhereParam("ispk", 1);
                    sql.addWhereParam("gencode", gencode);
                    VOSet<TableColumnVO> primaryKeys = simpleJdbcTemplateSupportDao.query(TableColumnVO.class, sql,dataSourceId);
                    for (TableColumnVO columnVO : primaryKeys.getVoList()) {
                        String getMaxkeySql = "select max("+columnVO.getColcode()+") as maxkey from "+columnVO.getTable_code();
                        maxKey = getMaxKey(maxKey, getMaxkeySql,dataSourceId);
                    }
                }
                counter.set(maxKey);
                return String.valueOf(counter.incrementAndGet());
            }else{
                RedisAtomicLong counter = new RedisAtomicLong(gencode, redisTemplate.getConnectionFactory());
                return String.valueOf(counter.incrementAndGet());
            }
        }
        synchronized (gencode.intern()) {
            if (projectSerialGeneratorCacheMap.containsKey(dataSourceId) && projectSerialGeneratorCacheMap.get(dataSourceId).containsKey(gencode)) {
                return getFromCache(gencode, dataSourceId);
            } else {
                this.loadFromDb_RequiresNew(gencode, dataSourceId);
                return getFromCache(gencode, dataSourceId);
            }
        }

    }

    private String getFromCache(String gencode, String dataSourceId) {
        SerialGeneratorVO serialGeneratorCache = projectSerialGeneratorCacheMap.get(dataSourceId).get(gencode);
        if (serialGeneratorCache.getCounter() < serialGeneratorCache.getGencache().longValue()) {
            String value = (serialGeneratorCache.getGenprefix() != null ? serialGeneratorCache.getGenprefix() : "") + serialGeneratorCache.getGennext();
            serialGeneratorCache.setCounter(serialGeneratorCache.getCounter() + 1);
            serialGeneratorCache.setGennext((Long.parseLong(serialGeneratorCache.getGennext()) + 1) + "");
            return value;
        } else {
            this.loadFromDb_RequiresNew(gencode, dataSourceId);
            return getFromCache(gencode, dataSourceId);
        }
    }

    public void loadFromDb_RequiresNew(String gencode, String dataSourceId) {
        loadFromDb_RequiresNew(gencode, dataSourceId, -1);
    }

    public void loadFromDb_RequiresNew(String gencode, String dataSourceId, int batchSize) {
        SerialGeneratorVO serialGeneratorVO = null;

        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("gencode", gencode);
        dynaSqlVO.setSelectForUpdate(true);
        VOSet<SerialGeneratorVO> voSet = simpleJdbcTemplateSupportDao.query(new SerialGeneratorVO(), dynaSqlVO, dataSourceId);

        if (voSet.getVoList() != null && !voSet.getVoList().isEmpty())
            serialGeneratorVO = voSet.getVoList().get(0);
        if (serialGeneratorVO == null)
            throw new IllegalArgumentException("请先配置序列号【" + gencode + "】！");

        String gennext = serialGeneratorVO.getGennext();
        //批量获取主键
        long genCache = serialGeneratorVO.getGencache().longValue();
        //当为批量获取，并且库中定义的阈值小于待取的数值时，使用batchSize值
        if (batchSize != -1) {
            //如果申请的大于设置的阈值，需要修改为申请的量级
            if (batchSize > genCache) {
                genCache = Long.valueOf(batchSize).longValue();
            }
        }
        serialGeneratorVO.setGennext((Long.parseLong(serialGeneratorVO.getGennext()) + genCache) + "");
        simpleJdbcTemplateSupportDao.update(serialGeneratorVO, new DynaSqlVO(), dataSourceId);
        serialGeneratorVO.setCounter(0);
        serialGeneratorVO.setGennext(gennext);
        //重新赋值
        serialGeneratorVO.setGencache(Long.valueOf(genCache));
        if (!projectSerialGeneratorCacheMap.containsKey(dataSourceId)) {
            projectSerialGeneratorCacheMap.put(dataSourceId, new HashMap<>());
        }
        projectSerialGeneratorCacheMap.get(dataSourceId).put(gencode, serialGeneratorVO);
    }

    private String[] getFromCache(String gencode, String dataSourceId, int batchSize) {
        //首先判断缓存中值是否够用，不够用读取数据库获取一次主键值
        SerialGeneratorVO serialGeneratorCache = projectSerialGeneratorCacheMap.get(dataSourceId).get(gencode);
        long remainVal = serialGeneratorCache.getGencache().longValue() - serialGeneratorCache.getCounter();
        if (remainVal < batchSize) {
            this.loadFromDb_RequiresNew(gencode, dataSourceId, batchSize);
        }

        String[] keys = new String[batchSize];
        for (int i = 0; i < batchSize; i++) {
            keys[i] = this.getFromCache(gencode, dataSourceId);
        }
        return keys;
    }

    @Override
    public String[] generate(String gencode, String dataSourceId, int batchSize) throws HDException {

        gencode = gencode.toLowerCase();
        //先从redis获取自增主键,获取不到说明改环境未使用redis存储自增主键，还是按原逻辑获取自增主键 modify by yinbinchen
        RedisTemplate redisTemplate = dataSourceCreator.getRedisTemplate(dataSourceId);
        if (redisTemplate != null) {
            RedisAtomicLong counter = null;
            if (!redisTemplate.hasKey(gencode)){
                counter = new RedisAtomicLong(gencode, redisTemplate.getConnectionFactory());
                Long maxKey=2000000000000L;
                if (gencode.equals(DEFAULT_GENCODE)){
                    String getMaxkeySql = "select  gennext as maxkey from sy_serial_generator where gencode = 'default'";
                    maxKey = getMaxKey(maxKey,getMaxkeySql,dataSourceId);
                }else {
                    DynaSqlVO sql = new DynaSqlVO();
                    sql.addWhereParam("ispk", 1);
                    sql.addWhereParam("gencode", gencode);
                    VOSet<TableColumnVO> primaryKeys = simpleJdbcTemplateSupportDao.query(TableColumnVO.class, sql,dataSourceId);
                    for (TableColumnVO columnVO : primaryKeys.getVoList()) {
                        String getMaxkeySql = "select max("+columnVO.getColcode()+") as maxkey from "+columnVO.getTable_code();
                        maxKey = getMaxKey(maxKey, getMaxkeySql,dataSourceId);
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
        synchronized (gencode.intern()) {
            if (projectSerialGeneratorCacheMap.containsKey(dataSourceId) && projectSerialGeneratorCacheMap.get(dataSourceId).containsKey(gencode)) {
                return getFromCache(gencode, dataSourceId, batchSize);
            } else {
                this.loadFromDb_RequiresNew(gencode, dataSourceId, batchSize);
                return getFromCache(gencode, dataSourceId, batchSize);
            }
        }
    }

    @Override
    public void cleanCache(String dataSourceId) {

        if (projectSerialGeneratorCacheMap.containsKey(dataSourceId)){
            projectSerialGeneratorCacheMap.remove(dataSourceId);
        }
    }

    private Long getMaxKey(Long maxKey, String getMaxkeySql, String dataSourceId) {
        List<AbstractVO> abstractVOVOSet = simpleJdbcTemplateSupportDao.executeQuery(getMaxkeySql,null,null,new ResultSetExtractor<AbstractVO>() {
            public AbstractVO extractData(ResultSet rs) throws SQLException, DataAccessException {
                AbstractVO gennext = null;
                while (rs.next()) {
                    if (rs.getObject("maxkey") == null) {
                        return null;
                    }
                    if (gennext == null) {
                        gennext = new BaseVO();
                        gennext.setLong("maxkey",rs.getLong("maxkey"));
                    }
                }
                return gennext;
            }
        }, dataSourceId);
        if (abstractVOVOSet!= null && !abstractVOVOSet.isEmpty() && abstractVOVOSet.get(0).get("maxkey") != null) {
            Long maxKeyTemp = abstractVOVOSet.get(0).getLong("maxKey");
            if (maxKey.longValue() < maxKeyTemp.longValue()) {
                maxKey = maxKeyTemp.longValue();
            }
        }
        return maxKey;
    }

}
