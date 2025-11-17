package com.hayden.hap.utils;

import com.hayden.hap.common.entity.UserVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 17:32
 */
public class CurrentEnvUtils {

    private static ThreadLocal<Map<String,Object>> contextMap=new ThreadLocal();

    public static void setContextMap(HashMap<String,Object> map){
        contextMap.set(map);
    }

    public static String getUserCode(){
        if(contextMap.get()!=null&&contextMap.get().containsKey("uservo")){
            UserVO userVO= (UserVO) contextMap.get().get("uservo");
            if(userVO!=null){
                return userVO.getUsercode();
            }
        }
        return null;
    }

    public static ThreadLocal<Map<String, Object>> getContextMap() {
        return contextMap;
    }
}
