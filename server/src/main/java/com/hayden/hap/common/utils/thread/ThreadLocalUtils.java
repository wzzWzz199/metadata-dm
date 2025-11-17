package com.hayden.hap.common.utils.thread;

import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.CommonVO;

/**
 * 处理线程级变量传递
 * 
 * @source
 * @author lengzy
 * @date 2015年11月26日
 */
public class ThreadLocalUtils {
	
	/** 线程级变量存储 */
    private static final ThreadLocal<CommonVO> THREAD_LOCAL = new ThreadLocal<CommonVO>();

    /**
     * 初始化整体的Thread存储信息
     * @return  threadBean   存储信息
     */
    private static CommonVO initThread() {
    	CommonVO dataVO = new CommonVO();
        THREAD_LOCAL.set(dataVO);
        return dataVO;
    }


    /**
     * 将数据存储到对应的键值中
     * @param key       对应键值
     * @param value     数据
     */
    public static void set(String key, Object value) {
    	CommonVO dataVO = THREAD_LOCAL.get();
        if (dataVO == null) {
        	dataVO = initThread();
        }
        dataVO.set(key, value);
    }

    /**
     * 根据Key获取存储的数据信息
     * @param key   标识
     * @return      存储的数据（缺省值为null
     */
    public static Object get(String key) {
    	CommonVO dataVO = THREAD_LOCAL.get();
        if (dataVO == null) {
        	dataVO = initThread();
        }
        return dataVO.get(key);
    }
    
    /**
     * 获取数据后从线程变量中移除
     * @param key
     * @return 
     * @author zhangfeng
     * @date 2018年7月6日
     */
    public static Object getAndRemove(String key) {
    	CommonVO dataVO = THREAD_LOCAL.get();
        if (dataVO == null) {
        	dataVO = initThread();
        }
        Object result = dataVO.get(key);
        
        if(dataVO.getColumnValues()!=null)
        	dataVO.getColumnValues().remove(key);
        return result;
    }
    
    /**
     * 根据Key获取存储的数据信息
     * @param key
     * @param clazz
     * @return 
     * @author zhangfeng
     * @date 2018年6月23日
     */
    @SuppressWarnings("unchecked")
	public static <T> T get(String key, Class<T> clazz) {
    	return (T)get(key);
    }
    
    /**
     * 获取数据后从线程变量中移除
     * @param key
     * @param clazz
     * @return 
     * @author zhangfeng
     * @date 2018年7月6日
     */
    @SuppressWarnings("unchecked")
	public static <T> T getAndRemove(String key, Class<T> clazz) {
    	return (T)getAndRemove(key);
    }

    /**
     * 获取thread中对应键值的数据
     * @param key           键值
     * @return              对应键值的数据（缺省值为""，而不是null）
     */
    public static String getStr(String key) {
        return getStr(key, "");
    }

    /**
     * 获取thread中对应键值的数据
     * @param key           键值
     * @param def           缺省值
     * @return              对应键值的数据
     */
    public static String getStr(String key, String def) {
    	CommonVO dataVO = THREAD_LOCAL.get();
    	if(dataVO!=null){
    		return dataVO.getString(key, def);
    	}
    	return null;
    }

    /**
     * 获取thread中对应键值的数据
     * @param key           键值
     * @return              对应键值的数据（如果不存在，缺省返回null）
     */
    public static BaseVO getBaseVO(String key) {
        return getBaseVO(key, null);
    }

    /**
     * 获取thread中对应键值的数据
     * @param key           键值
     * @param def           缺省值
     * @return              对应键值的数据
     */
    public static BaseVO getBaseVO(String key, BaseVO def) {
    	CommonVO dataVO = THREAD_LOCAL.get();
        return  (BaseVO)dataVO.get(key, def);
    }

    /**
     * 获取整体的Thread存储信息
     * @return  ThreadBean存储信息
     */
    public static CommonVO getThread() {
        return (CommonVO) THREAD_LOCAL.get();
    }

    /**
     * 设置整体的Thread存储信息
     * @param  threadBean   存储信息
     */
    public static void setThread(CommonVO dataVO) {
        THREAD_LOCAL.set(dataVO);
    }
    /**
     * 删除线程中的数据
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }
    
    /**
     * 删除线程中的数据
     */
    public static void remove(String key) {
    	CommonVO dataVO = THREAD_LOCAL.get();
        if (dataVO == null) {
        	return;
        }
        
        if(dataVO.getColumnValues()!=null)
        	dataVO.getColumnValues().remove(key);
    }
    
    /**
     * 获取thread中对应键值的数据
     * @param key           键值
     * @return              对应键值的数据（缺省值为null）
     */
    public static Long getLong(String key){
    	CommonVO dataVO = THREAD_LOCAL.get();
    	if(dataVO==null) return null;
    	return dataVO.getLong(key);
    }
}
