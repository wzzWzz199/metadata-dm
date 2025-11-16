package com.hayden.hap.common.formmgr.factory;

import com.hayden.hap.common.formmgr.itf.IId2NameStrategy;
import com.hayden.hap.common.formmgr.service.DefaultId2NameStrategy;
import com.hayden.hap.common.formmgr.service.OrgId2NameStrategy;
import com.hayden.hap.common.formmgr.service.TenantId2NameStrategy;
import com.hayden.hap.common.formmgr.service.UserId2NameStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * id返名称辅助类工厂<br/>
 * 针对用户、组织、租户的查询选择返名称优化<br/>
 * 目前只有这三个策略，如果以后添加更多的策略类，则需要来修改这个简单工厂<br/>
 * 
 * @author zhangfeng
 * @date 2016年9月7日
 */
public class Id2NameStrategyFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(Id2NameStrategyFactory.class);
	
	private static Map<String,Class<? extends IId2NameStrategy>> map = new HashMap<>();
	
	/**
	 * 初始化策略类映射：键为表名，值为策略类
	 */
	static {
		map.put("sy_user", UserId2NameStrategy.class);
		map.put("sy_org", OrgId2NameStrategy.class);
		map.put("sy_tenant", TenantId2NameStrategy.class);
	}
	
	/**
	 * 根据表名，创建返名称策略,没有定义对应策略，则返回默认策略
	 * @param tableName
	 * @return 
	 * @author zhangfeng
	 * @date 2016年9月8日
	 */
	public IId2NameStrategy createId2NameStrategy(String tableName) {
		Class<? extends IId2NameStrategy> clazz = map.get(tableName);
		if(clazz==null) {
			return new DefaultId2NameStrategy();
		}
		
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			logger.error("创建"+clazz.getName()+"对象出现以上异常，将执行默认策略");
		}
		return new DefaultId2NameStrategy();
	}
}
