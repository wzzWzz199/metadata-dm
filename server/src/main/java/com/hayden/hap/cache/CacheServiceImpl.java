package com.hayden.hap.cache;

import com.hayden.hap.cache.itf.ICacheService;
import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.cache.utils.RedisCacheUtil;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.upgrade.entity.UpgradeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description
 * @Author suntaiming
 * @Date 2022/4/22 15:29
 **/
@Service("cacheService")
public class CacheServiceImpl implements ICacheService {

    private static final String REDIS_CACHE_JOIN_CHAR = ":";

    private Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);
    @Autowired
    DataSourceCreator dataSourceCreator;
    //元数据redis缓存key处理器
    private Map<String, RedisCacheKeyHandler> keyHandlerMap = new HashMap<>();
    public CacheServiceImpl(){
        keyHandlerMap.put("pcmenu", new PcmenuRedisCacheKeyHandler());
        keyHandlerMap.put("mobilemenu", new MobilemenuRedisCacheKeyHandler());
        keyHandlerMap.put("permpackage", new PermpackageRedisCacheKeyHandler());
        keyHandlerMap.put("pcfunc", new PcfuncRedisCacheKeyHandler());
        keyHandlerMap.put("linkfunc", new LinkfuncRedisCacheKeyHandler());
        keyHandlerMap.put("linkfuncitem", new LinkfuncitemRedisCacheKeyHandler());
        keyHandlerMap.put("linkdata", new LinkdataRedisCacheKeyHandler());
        keyHandlerMap.put("mobilefunc", new MobilefuncRedisCacheKeyHandler());
        keyHandlerMap.put("mobilelinkfunc", new MobilelinkfuncRedisCacheKeyHandler());
        keyHandlerMap.put("mobilelinkfuncitem", new MobilelinkfuncitemRedisCacheKeyHandler());
        keyHandlerMap.put("pcform", new PcformRedisCacheKeyHandler());
        keyHandlerMap.put("pcfitem", new PcfitemRedisCacheKeyHandler());
        keyHandlerMap.put("pcfbutton", new PcfbuttonRedisCacheKeyHandler());
        keyHandlerMap.put("mobileform", new MobileformRedisCacheKeyHandler());
        keyHandlerMap.put("mobilefitem", new MobilefitemRedisCacheKeyHandler());
        keyHandlerMap.put("mobilebutton", new MobilebuttonRedisCacheKeyHandler());
        keyHandlerMap.put("table", new TableRedisCacheKeyHandler());
        keyHandlerMap.put("dict", new DictRedisCacheKeyHandler());
        keyHandlerMap.put("dictdata", new DictdataRedisCacheKeyHandler());
        keyHandlerMap.put("config", new ConfigRedisCacheKeyHandler());
        keyHandlerMap.put("import", new ImportRedisCacheKeyHandler());
        keyHandlerMap.put("export", new ExportRedisCacheKeyHandler());
    }
    @Override
    public void evict(UpgradeContext upgradeContext, ParamVO paramVO) throws HDException {
        RedisTemplate redisTemplate = dataSourceCreator.getRedisTemplate(upgradeContext.getDataSourceId());
        if(redisTemplate == null){
            log.warn("redisTemplate is null - dataSourceId={}", upgradeContext.getDataSourceId());
            return;
        }

        String nodetype = upgradeContext.getMetaRelationVO().getNodetype();
        RedisCacheKeyHandler keyHandler = keyHandlerMap.get(nodetype);
        if(keyHandler != null){
            List<String> keys = keyHandler.getRedisKeys(upgradeContext.getVo(), paramVO.getProject(), upgradeContext.getTenantid());
            if(keys.isEmpty()){
                return;
            }

            //普通key
            List<String> commKeys = new ArrayList<>();
            //需要正则匹配key
            List<String> likeKeys = new ArrayList<>();
            for (String key : keys){
                if(key.contains("*")){
                    likeKeys.add(key);
                }else {
                    commKeys.add(key);
                }
            }

            if (!commKeys.isEmpty()) {
                redisTemplate.delete(keys);
            }

            if(!likeKeys.isEmpty()){
                for (String key : likeKeys){
                    Set<String> itemKeys = redisTemplate.keys(key);
                    if(itemKeys != null && !itemKeys.isEmpty()){
                        redisTemplate.delete(itemKeys);
                    }
                }
            }
        }


    }

    /**
     * redis缓存key处理器
     * @Author: suntaiming
     * @Date: 2022/4/22 18:14
     */
    private interface RedisCacheKeyHandler{

        /**
         * 获取元数据缓存在redis中的key，如：hd_SY_FORM:SY_USER_ROLES|1
         * @param vo
         * @param projectIdentify
         * @param tenantid
         * @return: java.util.List<java.lang.String>
         * @Author: suntaiming
         * @Date: 2022/4/25 14:00
         */
        List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid);
    }

    //pc端菜单
    private class PcmenuRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();
            return keys;
        }
    }

    //移动端菜单
    private class MobilemenuRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();
            return keys;
        }
    }

    //权限组
    private class PermpackageRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();
            return keys;
        }
    }

    //功能
    private class PcfuncRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FUNC);
            String func_code = vo.getString("func_code");

            //@Cacheable(value = "SY_FUNC", key = "#funccode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(func_code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }


    //子功能
    private class LinkfuncRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FUNC_LINK);
            String func_code = vo.getString("main_func_code");

            //@Cacheable(value="SY_FUNC_LINK",key="#mainFuncCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(func_code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //子功能项
    private class LinkfuncitemRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();


            return keys;
        }
    }

    //
    private class LinkdataRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FUNC_LINK_DATA);
            String func_code = vo.getString("func_code");

            //@Cacheable(value=CacheConstant.CACHE_FUNC_LINK_DATA,key="#funcCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(func_code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //移动端功能
    private class MobilefuncRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FUNC_MOBILE);
            String code = vo.getString("func_code");

            //@Cacheable(value = CacheConstant.CACHE_FUNC_MOBILE, key = "#funccode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //移动端子功能
    private class MobilelinkfuncRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FUNC_LINK_MOBILE);
            String code = vo.getString("main_func_code");

            //@Cacheable(value=CacheConstant.CACHE_FUNC_LINK_MOBILE,key="#mainFuncCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //移动端子功能项
    private class MobilelinkfuncitemRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            return keys;
        }
    }

    //pc端表单
    private class PcformRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FORM);
            String code = vo.getString("form_code");

            //@Cacheable(value="SY_FORM",key="#formcode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //pc端表单项
    private class PcfitemRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FORM_ITEM);
            String code = vo.getString("form_code");

            //@Cacheable(value="SY_FORM_ITEM",key="#formCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            //@Cacheable(value = "SY_FORM_ITEM", key = "#formCode.concat('|').concat(#tenantid).concat('|displayitem')")
            StringBuilder key2 = new StringBuilder();
            key2.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid).append("|displayitem");

            keys.add(key.toString());
            keys.add(key2.toString());
            return keys;
        }
    }

    //pc端表单按钮
    private class PcfbuttonRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FORM_BUTTON);
            String code = vo.getString("form_code");

            //	@Cacheable(value="SY_FORM_BUTTON",key="#formCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //移动端表单
    private class MobileformRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FORM_MOBILE);
            String code = vo.getString("form_code");

            //@Cacheable(value=CacheConstant.CACHE_FORM_MOBILE,key="#formcode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //移动端表单项
    private class MobilefitemRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FORM_ITEM_MOBILE);
            String code = vo.getString("form_code");

            //@Cacheable(value=CacheConstant.CACHE_FORM_ITEM_MOBILE,key="#formCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }
    }

    //移动端表单按钮
    private class MobilebuttonRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_FORM_BUTTON_MOBILE);
            String code = vo.getString("form_code");

            //@Cacheable(value = CacheConstant.CACHE_FORM_BUTTON_MOBILE, key = "#formCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            keys.add(key.toString());
            return keys;
        }


    }

    //表定义
    private class TableRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, "syTablesCache");
            String code = vo.getString("table_code");

            //@Cacheable(value = "syTablesCache", key = "(#tbname).toLowerCase()", condition = "#tbname!=null")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code);

            //@Cacheable(value = "syAllTablesCache", key = "'syAllTables'")
            String cacheName2 = RedisCacheUtil.encodeRedisCacheName(projectIdentify, "syAllTablesCache");
            StringBuilder key2 = new StringBuilder();
            key2.append(cacheName2).append(":").append("syAllTables");

            keys.add(key.toString());
            keys.add(key2.toString());
            return keys;
        }


    }


    //字典
    private class DictRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_DICT);
            String code = vo.getString("dict_code");

            //@Cacheable(value="SY_DICT", key="#dictCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);


            String cacheName2 = RedisCacheUtil.encodeRedisCacheName(projectIdentify, "OUT_DICT_TABLE2CODE_BY_TENANTID");
            String code2 = vo.getString("dict_t_table");

            //@Cacheable(value="OUT_DICT_TABLE2CODE_BY_TENANTID", key="#tablecode.concat('|').concat(#tenantid)")
            StringBuilder key2 = new StringBuilder();
            key2.append(cacheName2).append(REDIS_CACHE_JOIN_CHAR).append(code2).append("|").append(tenantid);

            keys.add(key.toString());
            keys.add(key2.toString());
            return keys;
        }


    }


    //字典项
    private class DictdataRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_DICT_DATA);
            String code = vo.getString("dict_code");

            //@Cacheable(value="SY_DICT_DATA",key="#dictCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);


            //@Cacheable(value="SY_DICT_DATA",key="#dictCode.concat('|').concat(#tenantid).concat(#extWhere)")
            StringBuilder key2 = new StringBuilder();
            key2.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid).append("*");

            keys.add(key.toString());
            keys.add(key2.toString());
            return keys;
        }
    }


    //导入模板
    private class ImportRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            return keys;
        }
    }

    //导出模板
    private class ExportRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_EXPORT_TEMPLATE);
            String code = vo.getString("func_code");

            //@Cacheable(value = "SY_EXPORT_TEMPLATE", key = "#funcCode.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            return keys;
        }
    }


    //系统参数
    private class ConfigRedisCacheKeyHandler implements RedisCacheKeyHandler{

        @Override
        public List<String> getRedisKeys(AbstractVO vo, String projectIdentify, Long tenantid) {
            List<String> keys = new ArrayList<>();

            String cacheName = RedisCacheUtil.encodeRedisCacheName(projectIdentify, CacheConstant.CACHE_CONFIG);
            String code = vo.getString("conf_code");

            //@Cacheable(value="SY_CONFIG",key="#code.concat('|').concat(#tenantid)")
            StringBuilder key = new StringBuilder();
            key.append(cacheName).append(REDIS_CACHE_JOIN_CHAR).append(code).append("|").append(tenantid);

            return keys;
        }
    }







}
