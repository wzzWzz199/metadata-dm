package com.hayden.hap.service.upgrade.itf;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.vo.upgrade.ProgressModuleVO;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/11 10:06
 */
public interface IDbHandleService {
    void recordUpgradeTable(ParamVO paramVO, String tableName);

    void createTable(ParamVO paramVO, String dataSourceId, ProgressModuleVO progressModuleVO)  throws HDException;


    Map<String, List<String>> getDbTypeSqlList(String tableName, String dataSource)  throws Exception;

    void initDataBase(ParamVO paramVO)  throws Exception;
}
