package com.hayden.hap.dbop.db.util;

import com.hayden.hap.dbop.utils.ModuleDataSrcUtils;

/**
 * @Author: zhaorunjia
 * @Date: 2023-11-20 16:27
 * @Description: DB类型判断
 */
public class DBTypeUtil {

    /**
     * 判断当前数据源是否是达梦数据库
     * @return
     */
    public static boolean isDM() {
        // 达梦数据库判断
        String dbType = ModuleDataSrcUtils.getDbType();
        return DBType.DM.getCode().equals(dbType);
    }
    
}
