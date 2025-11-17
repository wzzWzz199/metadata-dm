package com.hayden.hap.config;

import com.hayden.hap.common.utils.properties.ModuleDataSrcPropertiesUtil;
import com.hayden.hap.common.utils.properties.MycatPropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LegacyPropertiesConfig {

    @Bean
    public MycatPropertiesUtil mycatPropertiesUtil(@Value("${sys.mycat.flag:false}") String flag) {
        MycatPropertiesUtil util = new MycatPropertiesUtil();
        util.setMycatFlag(flag);
        return util;
    }

    @Bean
    public ModuleDataSrcPropertiesUtil moduleDataSrcPropertiesUtil(
            @Value("${default.dataSource:dataSource}") String defaultDataSource) {
        ModuleDataSrcPropertiesUtil util = new ModuleDataSrcPropertiesUtil();
        util.setDefaultDataSource(defaultDataSource);
        return util;
    }
}
