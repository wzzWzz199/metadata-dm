package com.hayden.hap.utils;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.run.UpgradeRun;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 10:47
 */
public class RunnableUtils {
    //终止标识
    private static Map<String, UpgradeRun> UPGRADERUNMAP=new HashMap<>();

    public static Message interrupt(ParamVO paramVO){
        String key=paramVO.getKey();
        if(UPGRADERUNMAP.containsKey(key)){
            UPGRADERUNMAP.get(key).interrupt();
        }else{
            return new Message("此环境模块不存在升级线程");
        }
        return new Message("请等待升级线程执行终止操作");
    }

    public static void setRunnable(ParamVO paramVO,UpgradeRun upgradeRun){
        UPGRADERUNMAP.put(paramVO.getKey(),upgradeRun);
    }

    public static void isInterrupt(ParamVO paramVO) throws HDException {
        boolean flag=UPGRADERUNMAP.containsKey(paramVO.getKey())?UPGRADERUNMAP.get(paramVO.getKey()).isInterrupt():false;
        if(flag){
            throw new HDException("升级线程被手动终止");
        }
    }
}
