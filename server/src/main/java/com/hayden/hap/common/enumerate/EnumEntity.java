package com.hayden.hap.common.enumerate;

import java.io.Serializable;

public class EnumEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String value;
	private String name;
	
	EnumEntity(String value,String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
