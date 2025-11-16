package com.hayden.hap.common.utils.tuple;

/**
 * 
 * @author zhangfeng
 * @date 2017年7月19日
 */
public class TupleUtils {

	public static <A,B> TwoTuple<A, B> tuple(A a, B b) {
		return new TwoTuple<A, B>(a, b);
	}
	
	public static <A,B,C> ThreeTuple<A, B, C> tuple(A a, B b, C c) {
		return new ThreeTuple<A, B, C>(a, b, c);
	}
}
