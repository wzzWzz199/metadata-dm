package com.hayden.hap.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtil {

	public static <T1, T> List<T1> select(List<T> data, SelectFunc<T, T1> func) {
		List<T1> list = new ArrayList<>();
		for (T t : data) {
			list.add(func.select(t));
		}
		return list;
	}

	public static <T> List<T> where(List<T> data, CheckFunc<T> func) {
		List<T> list = new ArrayList<>();
		for (T t : data) {
			if (func.check(t))
				list.add(t);
		}
		return list;
	}

	public static <T> T first(List<T> data, CheckFunc<T> func) {
		for (T t : data) {
			if (func.check(t))
				return t;
		}
		return null;
	}

	public static <T> T last(List<T> data, CheckFunc<T> func) {
		for (int i = data.size() - 1; i >= 0; i--) {
			T t = data.get(i);
			if (func.check(t))
				return t;
		}
		return null;
	}

	public static <T1, T> Map<T1, List<T>> group(List<T> data, SelectFunc<T, T1> func) {
		Map<T1, List<T>> map = new HashMap<>();
		for (T t : data) {
			T1 key = func.select(t);
			List<T> list = map.get(key);
			if (list == null) {
				list = new ArrayList<>();
				map.put(key, list);
			}
			list.add(t);
		}
		return map;
	}

	public static <T1, T> Map<T1, T> toMap(List<T> data, SelectFunc<T, T1> func) {
		Map<T1, T> map = new HashMap<>();
		for (T t : data) {
			T1 key = func.select(t);
			map.put(key, t);
		}
		return map;
	}

	public static <T, T1, T2> Map<T1, T2> toMap(List<T> data, SelectFunc<T, T1> KeyFunc, SelectFunc<T, T2> valueFunc) {
		Map<T1, T2> map = new HashMap<>();
		for (T t : data) {
			T1 key = KeyFunc.select(t);
			T2 value = valueFunc.select(t);
			map.put(key, value);
		}
		return map;
	}

	public static interface SelectFunc<T, P> {
		P select(T t);
	}

	public static interface CheckFunc<T> {
		boolean check(T t);
	}

}
