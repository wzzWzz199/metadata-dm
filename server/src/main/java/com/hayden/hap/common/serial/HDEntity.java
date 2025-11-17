package com.hayden.hap.common.serial;

import com.hayden.hap.dbop.entity.BaseVO;

import java.util.HashMap;
import java.util.Map;

public class HDEntity extends BaseVO{

	private static final long serialVersionUID = 1L;
	
	public Map<String,Object> data = new HashMap<String, Object>();
	
	public void setAttribute(String key,Object value) {
		data.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return data.get(key);
	}
}
