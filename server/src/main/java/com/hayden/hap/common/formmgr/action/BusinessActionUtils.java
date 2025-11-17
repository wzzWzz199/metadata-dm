package com.hayden.hap.common.formmgr.action;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * 业务处理工具类
 * @author zhangfeng
 * @date 2018年6月26日
 */
public class BusinessActionUtils {

	private static final String PACKAGE_TO_SCAN = "com.hayden.hap";

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BusinessActionUtils.class);

//	@SuppressWarnings("unchecked")
//	public static <T> T getBusinessAction(String funccode, Long tenantid, Class<T> clazz) throws HDException {
//		IFormService formService = AppServiceHelper.findBean(IFormService.class);
//
//		IAction action = formService.getActionByFuncCode(funccode, tenantid);
//		if(action==null) {
//			throw new HDException("没有配置扩展类："+funccode);
//		}
//
//		Class<?> cls = action.getBusinessAction();
//		if(cls==null) {
//			throw new HDException("扩展类没有定义业务处理类型信息");
//		}
//
//		if(!clazz.isAssignableFrom(cls)) {
//			throw new HDException(cls+"不能转换成"+clazz);
//		}
//
//		Component component = cls.getAnnotation(Component.class);
//		if(component==null) {//如果没有spring的
//			try {
//				return (T)cls.newInstance();
//			} catch (InstantiationException | IllegalAccessException e) {
//				logger.error(e.getMessage(), e);
//				return null;
//			} 
//		}
//
//		String value = component.value();
//		return AppServiceHelper.findBean(clazz, value);		
//	}
	
	private static Map<Class<?>, Class<?>> cache = new HashMap<>();

	/**
	 * 获取实现类实例，类名最长的那个
	 * @param clazz
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年8月23日
	 */
	public static <T> T getBusinessAction(Class<T> clazz) throws HDException {		
		Class<? extends T> implClazz = getBusinessClass(clazz);
		
		//先看是不是spring的bean，如果是，则通过获取bean实例返回
		List<? extends T> tList = AppServiceHelper.findBeans(implClazz);
		if(ObjectUtil.isNotEmpty(tList)) {
			T t = tList.get(0);
			return t;
		}
		
		//如果不是spring的bean，则反射
		try {
			T t = (T)implClazz.newInstance();
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new HDException(e);
		}
		
	}
	
	/**
	 * 获取实现类，名字最长的那个
	 * @param clazz
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年8月23日
	 */
	@SuppressWarnings("unchecked")
	private static <T> Class<? extends T> getBusinessClass(Class<T> clazz) throws HDException {
		Class<?> cls = cache.get(clazz);
		if(cls!=null) {
			return (Class<? extends T>) cls;
		}
		
		Collection<URL> set = ClasspathHelper.forPackage(PACKAGE_TO_SCAN);
		Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(set).setScanners(new SubTypesScanner()));

		Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(clazz);
		if(ObjectUtil.isEmpty(subTypesOf)) {
			String errorMsg = "没有找到任何"+clazz.getName()+"的实现类";
			logger.error(errorMsg);
			throw new HDException(errorMsg);
		}
		
		List<Class<? extends T>> list = new ArrayList<Class<? extends T>>(subTypesOf);
		//按类名从长到短排序
		Collections.sort(list, (x,y)->clsLength(x).compareTo(clsLength(y)));
		//取类名最长的实现类
		Class<? extends T> implClazz = list.get(list.size()-1);
		cache.put(clazz, implClazz);
		return implClazz;
	}
	
	/**
	 * 获取类名长度
	 * @param clazz
	 * @return 
	 * @author zhangfeng
	 * @date 2018年8月23日
	 */
	private static Integer clsLength(Class<?> clazz) {
		return clazz.getSimpleName().length();
	}
}
