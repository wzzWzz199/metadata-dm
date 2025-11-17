package com.hayden.hap.common.formmgr.query.strategy;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 查询策略工厂
 * @author zhangfeng
 * @date 2018年8月23日
 */
public class QueryStrategyFactory {

	private Map<String,Class<? extends IQueryStrategy>> map = new HashMap<>();
	
	private QueryStrategyFactory() {
		Set<Class<? extends IQueryStrategy>> set = ClassUtils.getImplClassByItf(IQueryStrategy.class);
		for(Class<? extends IQueryStrategy> cls : set) {
			map.put(cls.getSimpleName(), cls);
		}
	}
	
	public static QueryStrategyFactory getInstance() {
		return A.B;
	}
	
	public IQueryStrategy getQueryStrategy(String qs) throws HDException {
		Class<? extends IQueryStrategy> cls = map.get(qs);
		if(cls==null)
			throw new HDException("没有找到查询策略："+qs);
		
		IQueryStrategy strategy = null;
		try {
			strategy = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new HDException("实例化查询策略异常",e);
		}
		return strategy;
	}
	
	private static class A {
		private static final QueryStrategyFactory B = new QueryStrategyFactory();
	}
}
