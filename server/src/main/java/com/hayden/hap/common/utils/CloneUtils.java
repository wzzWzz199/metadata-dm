package com.hayden.hap.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * 克隆工具类
 * @author zhangfeng
 * @date 2016年8月11日
 */
public class CloneUtils {

	/**
	 * 深度克隆对象
	 * @param t
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月11日
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cloneObj(T t) {
		try {
			//save the object to a byte array
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(t);
			out.close();

			//read a clone of the object from byte array
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bin);
			Object ret = in.readObject();
			in.close();
			return (T)ret;
		}catch(Exception e) {
			return null;
		}		
	}
	/**
	 * @Description <p>获取到对象中属性为null的属性名  </P>
	 * @param source 要拷贝的对象
	 * @return
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/**
	 * @Description <p> 拷贝非空对象属性值 </P>
	 * @param source 源对象
	 * @param target 目标对象
	 */
	public static void copyPropertiesIgnoreNull(Object source, Object target) {
		BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
	}
}
