package com.hayden.hap.common.formmgr.query;

import com.hayden.hap.common.form.entity.FormItemVO;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2017年11月10日
 */
public class DefaultWhereclausParser implements IWhereclausParser{

	protected static Map<String,String> map = new HashMap<String,String>() {
	
		private static final long serialVersionUID = 1L;

		{
			put("eq", " = ");
			put("gt", " > ");
			put("ge", " >= ");
			put("lt", " < ");
			put("le", " <= ");
			put("not_eq", " != ");			
		}
	};

	
	@Override
	public String parse(FormItemVO fieldVO, Object oldValue, String queryMethodStr, String key) {
		String newValue = changeValue(fieldVO, oldValue, queryMethodStr, key);
		return key + map.get(queryMethodStr) + newValue;
	}
	
	/**
	 * 是否支持该字段的查询条件解析
	 * @param fieldVO
	 * @param oldValue
	 * @param queryMethodStr
	 * @param key
	 * @return 
	 * @author zhangfeng
	 * @date 2017年11月10日
	 */
	protected boolean support(FormItemVO fieldVO, Object oldValue, String queryMethodStr, String key) {
		//默认解析器只支持map中定义的这几种查询方式
		return map.containsKey(queryMethodStr);
	}
	
	protected String changeValue(FormItemVO fieldVO, Object oldValue, String queryMethodStr, String key) {
		return oldValue+"";
	}
}
