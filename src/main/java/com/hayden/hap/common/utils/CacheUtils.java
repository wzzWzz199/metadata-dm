package com.hayden.hap.common.utils;

import com.hayden.hap.common.spring.service.AppServiceHelper;
import net.sf.ehcache.Ehcache;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.List;
import java.util.regex.Pattern;

public class CacheUtils {

	private static final CacheUtils instance = new CacheUtils();
	
	private EhCacheCacheManager cacheManager;
	
	private CacheUtils() {
		cacheManager = AppServiceHelper.findBean(EhCacheCacheManager.class, "cacheManager");
	}
	
	public static CacheUtils getInstance() {
		return instance;
	}
	
	public Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}
	
	public void evict(String cacheName, Object key) {
		Cache cache = getCache(cacheName);
		cache.evict(key);
	}
	
	/**
	 * 根据正则表达式清缓存
	 * @param cacheName
	 * @param regular 
	 * @author zhangfeng
	 * @date 2018年8月20日
	 */
	public void evictByRegular(String cacheName, String regular) {		
		Cache cache = getCache(cacheName);
		Ehcache ehcache = (Ehcache) cache.getNativeCache();
		List<?> keys = ehcache.getKeys();
		
		Pattern pattern = Pattern.compile(regular);
		for(Object key : keys) {
			String keyStr = key.toString();
			if(pattern.matcher(keyStr).matches()) {
				cache.evict(keyStr);
			}
		}		
	}
	
	/**
	 * 根据前缀清缓存
	 * @param cacheName
	 * @param prefix 
	 * @author zhangfeng
	 * @date 2018年8月20日
	 */
	public void evictByPrefix(String cacheName, String prefix) {
		Cache cache = getCache(cacheName);
		Ehcache ehcache = (Ehcache) cache.getNativeCache();
		List<?> keys = ehcache.getKeys();
		
		for(Object key : keys) {
			String keyStr = key.toString();
			if(keyStr.startsWith(prefix)) {
				cache.evict(keyStr);
			}
		}	
	}
	
	/**
	 * 根据后缀清缓存
	 * @param cacheName
	 * @param suffix 
	 * @author zhangfeng
	 * @date 2018年8月20日
	 */
	public void evictBySuffix(String cacheName, String suffix) {
		Cache cache = getCache(cacheName);
		Ehcache ehcache = (Ehcache) cache.getNativeCache();
		List<?> keys = ehcache.getKeys();
		
		for(Object key : keys) {
			String keyStr = key.toString();
			if(keyStr.endsWith(suffix)) {
				cache.evict(keyStr);
			}
		}
	}
	
	public void put(String cacheName,Object key,Object value) {
		Cache cache = getCache(cacheName);
		cache.put(key, value);
	}
	
	/**
	 * 清除该缓存的所有数据，不支持事务
	 * @param cacheName 
	 * @author zhangfeng
	 * @date 2017年6月7日
	 */
	public void clear(String cacheName) {
		Cache cache = getCache(cacheName);
		cache.clear();
	}
}
