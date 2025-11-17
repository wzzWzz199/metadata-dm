package com.hayden.hap.common.utils;

import com.hayden.hap.common.enumerate.IEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class EnumUtil {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(EnumUtil.class);

    /**
     * 根据枚举类和枚举code获取枚举对象
     * @param value
     * @param clazz
     * */
    @SuppressWarnings("unchecked")
	public static <T extends IEnum>  T getEnumObject(long code,Class<T> clazz){
        return (T)EnumConstant.ENUM_MAP.get(clazz).get(code);
    }

    /**
     * 根据枚举类名获取对应的枚举对象集合
     * @param enumName
     * @return 
     * @author zhangfeng
     * @date 2015年5月29日
     */
    public static Collection<IEnum> getEnumsByName(String enumName) {
    	try {
			Class<?> clz = Class.forName(EnumConstant.PACKAGE_NAME+"."+enumName);
			Map<String,IEnum> map = EnumConstant.ENUM_MAP.get(clz);
			if(map!=null) {
				return map.values();
			}
			return null;
			
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
    }
    
    
}
