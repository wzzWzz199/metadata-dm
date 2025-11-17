package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.dbop.exception.HDRuntimeException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段值匹配器
 * @author zhangfeng
 * @date 2017年7月18日
 */
public class ItemMatcher {

	private String matchStr;
	private String method;
	private boolean isNumber;
	private BigDecimal matchBigDecimal;
	
	private static Pattern numberPattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
	private static Set<String> supportMethod = new HashSet<>();
	static {
		supportMethod.add("eq");
		supportMethod.add("anywhere");
		supportMethod.add("start");
		supportMethod.add("end");
		supportMethod.add("gt");
		supportMethod.add("ge");
		supportMethod.add("lt");
		supportMethod.add("le");
	}
	
	public ItemMatcher(String matchStr, String method) {
		if(matchStr==null)
			throw new NullPointerException();						
		
		if(!support(method)) {
			throw new HDRuntimeException("不支持的匹配方式");
		}
		
		this.matchStr = matchStr;
		this.method = method;	
		
		this.isNumber = isNumber(matchStr);
		if(isNumber) {
			matchBigDecimal = new BigDecimal(matchStr);
		}
		
	}
	
	private boolean support(String method) {
		return supportMethod.contains(method);
	}
	
	public boolean matcher(String test) {
		if(test==null)
			return false;
		
		if("eq".equals(method)){
			return matchStr.equals(test);
		}
		if("anywhere".equals(method)) {
			return test.indexOf(matchStr)>-1;
		}
		if("start".equals(method)) {
			return test.startsWith(matchStr);
		}
		if("end".equals(method)) {
			return test.endsWith(matchStr);
		}	
		
		boolean isTestNumber = isNumber(test);
		if("gt".equals(method)){
			if(isNumber && isTestNumber) {
				return new BigDecimal(test).compareTo(matchBigDecimal)==1;
			}
			return test.compareTo(matchStr)==1;
		}
		if("ge".equals(method)){
			if(isNumber && isTestNumber) {
				return new BigDecimal(test).compareTo(matchBigDecimal)>=0;
			}
			return test.compareTo(matchStr)>=0;
		}
		if("lt".equals(method)){
			if(isNumber && isTestNumber) {
				return new BigDecimal(test).compareTo(matchBigDecimal)<0;
			}
			return test.compareTo(matchStr)<0;
		}
		if("le".equals(method)){
			if(isNumber && isTestNumber) {
				return new BigDecimal(test).compareTo(matchBigDecimal)<=0;
			}
			return test.compareTo(matchStr)<=0;
		}
		
		return false;
	}
	
	private boolean isNumber(String test) {			
		Matcher matcher = numberPattern.matcher(test);
		return matcher.matches();
	}
}
