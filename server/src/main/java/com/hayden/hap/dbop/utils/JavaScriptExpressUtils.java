package com.hayden.hap.dbop.utils;

import com.hayden.hap.dbop.exception.HDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * js表达式工具类
 * @author zhangfeng
 * @date 2016年7月1日
 */
public class JavaScriptExpressUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JavaScriptExpressUtils.class);

	/**
	 * 执行带有变量js表达式，返回表达式的执行结果
	 * @param express
	 * @param variableMap 用键值对形式保存变量名和变量值
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年7月1日
	 */
	public static Object eval4variable(String express,Map<String,Object> variableMap) throws ScriptException {
		ScriptEngineManager factory = new ScriptEngineManager();  
		ScriptEngine engine = factory.getEngineByName("JavaScript"); 
		
		return eval4variable(express, variableMap, engine);
	}
	
	public static Object eval4variable(String express,Map<String,Object> variableMap, ScriptEngine engine) throws ScriptException {
		if(variableMap!=null) {
			for(Entry<String, Object> entry:variableMap.entrySet()) {
				engine.put(entry.getKey(), entry.getValue());
			}
		}
		
		Object result = engine.eval(express);
		
		return result;
	}
	
	/**
	 * 执行js表达式，返回boolean型结果<br/>
	 * <font color="red">注：如果循环多次执行表达式，请先构造js引擎，调用重载方法</font>
	 * @param express
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年7月1日
	 */
	public static boolean eval(String express) throws ScriptException {
		Object obj = eval4variable(express, null);
		return Boolean.valueOf(obj.toString());
	}
	
	/**
	 * 提供个可传入js引擎的方法，因为在多次执行表达式时，构造引擎需要时间
	 * @param express
	 * @param engine
	 * @return 
	 * @author zhangfeng
	 * @throws ScriptException 
	 * @date 2018年11月8日
	 */
	public static boolean eval(String express, ScriptEngine engine) throws ScriptException {
		Object obj = eval4variable(express, null, engine);
		return Boolean.valueOf(obj.toString());
	}
	
	public static void main(String[] args) throws ScriptException {
		String s = "abc!=null";
		boolean b = eval(s);
		System.out.println(b);
	}
}
