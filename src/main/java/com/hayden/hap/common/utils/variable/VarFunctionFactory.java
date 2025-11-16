package com.hayden.hap.common.utils.variable;

import com.hayden.hap.common.common.exception.HDRuntimeException;
import com.hayden.hap.common.utils.variable.itf.IVarFunction;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author zhangfeng
 * @date 2018年7月9日
 */
public class VarFunctionFactory {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VarFunctionFactory.class);

	private static final String PACKAGE_TO_SCAN = "com.hayden.hap";
	
	Map<String, IVarFunction> impl;
		
	private VarFunctionFactory() {
		impl = getImpl();
	}
	
	public static VarFunctionFactory getInstance() {
		return A.instance;
	}
	
	public IVarFunction getVarFunction(String funcname) {
		IVarFunction varFunction = impl.get(funcname);
		if(varFunction==null)
			throw new HDRuntimeException("不支持的函数名："+funcname);
		return varFunction;
	}

	private Map<String, IVarFunction> getImpl() {
		final Collection<URL> set = ClasspathHelper.forPackage(PACKAGE_TO_SCAN);
        final Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(set).setScanners(new SubTypesScanner()));

		final Set<Class<? extends IVarFunction>> subTypesOf = reflections.getSubTypesOf(IVarFunction.class);
        
        Map<String, IVarFunction> map = new HashMap<>();
        for(Class<? extends IVarFunction> clazz : subTypesOf) {
        	IVarFunction varFunction = null;
        	try {
				varFunction = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error(e.getMessage(),e);
				throw new HDRuntimeException(e);
			}
        	map.put(varFunction.supportFunction(), varFunction);
        }
        return map;
	}
	
	private static class A {
		private static VarFunctionFactory instance = new VarFunctionFactory();
	}
}
