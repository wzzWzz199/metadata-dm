package com.hayden.hap.dbop.utils;

import com.hayden.hap.common.enumerate.IEnum;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2015年5月29日
 */
public class EnumConstant {
	
	/**
     * 枚举类对应的包路径
     */
    public final static String PACKAGE_NAME = "com.hayden.hap.sy.enumerate";
    /**
     * 枚举接口类全路径
     */
    public final static String ENUM_MESSAGE_PATH=PACKAGE_NAME+".IEnum";

    /**
     * 存放单个枚举对象 map常量定义
     */
    private static Map<String, IEnum> SINGLE_ENUM_MAP = null;
    /**
     * 所有枚举对象的 map
     */
    public static final Map<Class<? extends IEnum>, Map<String, IEnum>> ENUM_MAP = initialEnumMap(true);


    /**
     * 加载所有枚举对象数据
     * @param  isFouceCheck 是否强制校验枚举是否实现了IEnum接口
     *
     * */
    private static Map<Class<? extends IEnum>, Map<String, IEnum>> initialEnumMap(boolean isFouceCheck){
        Map<Class<? extends IEnum>, Map<String, IEnum>> temp = new HashMap<Class<? extends IEnum>, Map<String, IEnum>>();
        List<Class<? extends IEnum>> list = ClassUtils.getAllClassByInterface(IEnum.class);
        try {
            for (Class<? extends IEnum> cls : list) {
                Class <?>[]iter=cls.getInterfaces();
                boolean flag=false;
                if(isFouceCheck){
                    for(Class cz:iter){
                        if(cz.getName().equals(ENUM_MESSAGE_PATH)){
                            flag=true;
                            break;
                        }
                    }
                }
                if(flag==isFouceCheck){
                     SINGLE_ENUM_MAP = new HashMap<String, IEnum>();
                    initialSingleEnumMap(cls);
                    temp.put(cls, SINGLE_ENUM_MAP);
                }

            }
        } catch (Exception e) {
           
        }
        return temp;
    }

    /**
     * 加载每个枚举对象数据
     * */
    private static void initialSingleEnumMap(Class<?> cls )throws Exception{
        Method method = cls.getMethod("values");
        IEnum inter[] = (IEnum[]) method.invoke(null, null);
        for (IEnum enumMessage : inter) {
            SINGLE_ENUM_MAP.put(enumMessage.getCode(), enumMessage);
        }
        
    }

}
