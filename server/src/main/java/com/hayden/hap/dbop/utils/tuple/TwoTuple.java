package com.hayden.hap.dbop.utils.tuple;

import java.io.Serializable;

/**
 * 二元组
 * @author zhangfeng
 * @date 2017年7月19日
 */
public class TwoTuple<A,B> implements Serializable{
	private A _1;
	private B _2;
	
	public TwoTuple(A a, B b) {
		this._1 = a;
		this._2 = b;
	}
	
	public A _1() {
		return _1;
	}
	
	public B _2() {
		return _2;
	}
}
