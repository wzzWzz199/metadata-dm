package com.hayden.hap.common.utils.tuple;

/**
 * 三元组
 * @author zhangfeng
 * @date 2017年7月19日
 */
public class ThreeTuple<A,B,C> extends TwoTuple<A, B> {

	private C _3;
	
	public ThreeTuple(A a, B b, C c) {
		super(a, b);
		this._3 = c;
	}

	public C _3() {
		return _3;
	}
}
