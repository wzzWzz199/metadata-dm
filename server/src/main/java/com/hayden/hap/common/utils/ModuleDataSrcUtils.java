package com.hayden.hap.common.utils;

import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.dbop.reflect.ClassInfo;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.properties.ModuleDataSrcPropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * 依据模块获取相应数据源类型
 * @author wangyi
 * @date 2017年6月27日
 */
public class ModuleDataSrcUtils {
	
	private static JdbcTemplateSupportDao jdbcTemplateSupportDao = 
			(JdbcTemplateSupportDao)AppServiceHelper.findBean("jdbcTemplateSupportDao");
	
	private static ClassInfo classInfo = ClassInfo.forClass(ModuleDataSrcPropertiesUtil.class); 
	
	private static final Logger logger = LoggerFactory.getLogger(ModuleDataSrcUtils.class);
	
	/**
	 * @return 默认数据源的数据类型
	 * @author wangyi
	 * @date 2017年6月27日
	 */
	public static String getDbType(){
		String defaultDataSourceName = ModuleDataSrcPropertiesUtil.getDefaultDataSource();
		return jdbcTemplateSupportDao.getDataSourceManager().getDbType(defaultDataSourceName);
	}
	
	/**
	 * 依据模块，查绑定数据源对应的数据类型
	 * @param moduleCode
	 * @return 
	 * @author wangyi
	 * @date 2017年6月27日
	 */
	public static String getDbTypeByModuleCode(String moduleCode){
		//_m移动端模块，需要拼写下field的名称
		String fieldName = moduleCode;
		if(moduleCode.endsWith("_m")){
			fieldName = moduleCode.substring(0, moduleCode.indexOf("_m")) + "M";
		}
		fieldName = fieldName + "DataSource";
		//判断是否有针对该模块的配置，如果没有取默认数据源
		if(classInfo.hasGetter(fieldName)){
			try {
				String dataSourceName = (String) classInfo.getGetInvoker(fieldName).invoke(ModuleDataSrcPropertiesUtil.class, null);
				//判断配置的值是否为空，为空时也取默认数据源
				if(StringUtils.isNotEmpty(dataSourceName))
					return jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceName);
			} catch (IllegalAccessException e) {
				logger.error("依据模块查询相应的数据类型出错！"+moduleCode+e.getMessage());
			} catch (InvocationTargetException e) {
				logger.error("依据模块查询相应的数据类型出错！"+moduleCode+e.getMessage());
			}
		}	
		//读取默认数据源
		return getDbType();
	}
}
