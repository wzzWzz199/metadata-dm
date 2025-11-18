package com.hayden.hap.dbop.db.util;

import com.hayden.hap.dbop.utils.SyConstant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: StringUtil
 * @Description:
 * @author LUYANYING
 * @date 2015年3月19日 上午10:00:09
 * @version V1.0
 * 
 */
public class ObjectUtil {
	/**
	 * 
	 * @Title: isNotNull
	 * @Description: 判断是否为空
	 * @param str
	 * @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isNotNull(Object object) {
		if (object == null)
			return false;
		if (object instanceof String && "".equals(object))
			return false;
		return true;
	}

	/**
	 * 
	 * @Title: isNotEmpty
	 * @Description: 判断map是否不为空
	 * @param map
	 * @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isNotEmpty(Map map) {
		if (map != null && !map.isEmpty())
			return true;
		return false;
	}

	/**
	 * 
	 * @Title: isNotEmpty
	 * @Description: 判断Collection是否不为空
	 * @param map
	 * @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isNotEmpty(Collection collection) {
		if (collection != null && !collection.isEmpty())
			return true;
		return false;
	}

	/**
	 * 判断是否为空或空集合
	 * @param collection
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	/**
	 * 
	 * @Title: isNotEmpty
	 * @Description: 判断数组是否不为空
	 * @param array
	 * @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isNotEmpty(Object[] array) {
		if (array != null && array.length > 0)
			return true;
		return false;
	}

	/**
	 * 
	 * @Title: validNotNull
	 * @Description: 
	 *               验证对象是否为空，为空或空字符串(如果是字符串还需判断是否为空字符串)则抛出异常IllegalArgumentException
	 * @param object
	 * @param message
	 * @return void
	 * @throws
	 */
	public static void validNotNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
		if (object instanceof String && "".equals(object))
			throw new IllegalArgumentException(message);
	}

	/**
	 * 
	 * @Title: validIsTrue
	 * @Description: 验证表达式是否为true，如果为true则抛出异常IllegalArgumentException
	 * @param expression
	 * @param message
	 * @return void
	 * @throws
	 */
	public static void validIsTrue(boolean expression, String message) {
		if (expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static boolean isTrue(Integer flag) {
		if(flag==null)
			return false;
		if(SyConstant.SY_TRUE==flag) 
			return true;
		return false;
	}

	/**
	 * 
	 * @Title: toArray
	 * @Description: list转化数组
	 * @param list
	 * @return
	 * @return T[]
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<T> list) {
		if (list == null)
			return null;
		return (T[]) list.toArray();
	}

	/**
	 * 
	 * @Title: printArray
	 * @Description: 打印数组
	 * @param array
	 * @return
	 * @return String
	 * @throws
	 */
	public static String printArray(int[] array) {
		if (array == null || array.length == 0)
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < array.length; i++) {
			int element = array[i];
			sb.append(element);
			if (i == array.length - 1)
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
		return sb.toString();
	}

	/**
	 * 
	 * @Title: clone
	 * @Description: 复制数组
	 * @param array
	 * @return
	 * @return int[]
	 * @throws
	 */
	public static int[] clone(int[] array) {
		if (array == null) {
			return null;
		}
		return (int[]) array.clone();
	}

	/**
	 * 
	 * @Title: addAll
	 * @Description: 合并数组
	 * @param array1
	 * @param array2
	 * @return
	 * @return int[]
	 * @throws
	 */
	public static int[] addAll(int[] array1, int[] array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		int[] joinedArray = new int[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

	/**
	 * 
	 * @Title: asList
	 * @Description: 把int数组转为List<Integer>
	 * @param array
	 * @return
	 * @return List<Integer>
	 * @throws
	 */
	public static List<Integer> asList(int[] array) {
		if (array == null)
			return null;
		List<Integer> list = new ArrayList<Integer>();
		for (int i : array)
			list.add(i);
		return list;
	}

	/**
	 * 
	 * @Title: asLong
	 * @Description: 把string转为Long对象
	 * @param str
	 * @return
	 * @return Long
	 * @throws
	 */
	public static Long asLong(String str) {
		if (str == null || "".equals(str))
			return null;
		return Long.valueOf(str);
	}

	/**
	 * 
	 * @Title: asInteger
	 * @Description: 把string转为Integer对象
	 * @param str
	 * @return
	 * @return Integer
	 * @throws
	 */
	public static Integer asInteger(String str) {
		if (str == null || "".equals(str))
			return null;
		return Integer.valueOf(str);
	}

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_DATEONLY_FORMAT = "yyyy-MM-dd";
	public static final String DEFAULT_TIMEONLY_FORMAT = "HH:mm:ss";

	/**
	 * 
	 * @Title: getDateFormatString
	 * @Description: 格式化时间
	 * @param date
	 * @param format
	 * @return
	 * @return String
	 * @throws
	 */
	public static String getDateFormatString(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @Title: getDateFormatString
	 * @Description: 格式化时间,包含日期和时分秒
	 * @param date
	 * @return
	 * @return String
	 * @throws
	 */
	public static String getDateFormatString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @Title: getDateOnlyFormatString
	 * @Description: 格式化时间,只有日期
	 * @param date
	 * @return
	 * @return String
	 * @throws
	 */
	public static String getDateOnlyFormatString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATEONLY_FORMAT);
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @Title: getTimeOnlyFormatString
	 * @Description: 格式化时间,只有时分秒
	 * @param date
	 * @return
	 * @return String
	 * @throws
	 */
	public static String getTimeOnlyFormatString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(DEFAULT_TIMEONLY_FORMAT);
		return dateFormat.format(date);
	}

	/**
	 * dropDotOfStr:(判断并去掉字符串后面的逗号或其他符号). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @param str
	 * @return
	 */
	public static String dropLastSplitSign(String str, String sign) {
		if (str.trim().endsWith(sign)) {
			str = str.substring(0, str.lastIndexOf(sign))
					+ str.substring(str.lastIndexOf(sign) + 1, str.length());
		}
		return str;
	}

	/**
	 * dropFirstSplitSign:(去掉字符串前面的分隔符). <br/>
	 * date: 2015年11月21日 <br/>
	 * @author ZhangJie
	 * @param str
	 * @param sign
	 * @return
	 */
	public static String dropFirstSplitSign(String str, String sign) {
		if (str.trim().startsWith(sign)) {
			str = str.substring(0, str.indexOf(sign))
					+ str.substring(str.indexOf(sign) + 1);
		}
		return str;
	}
}
