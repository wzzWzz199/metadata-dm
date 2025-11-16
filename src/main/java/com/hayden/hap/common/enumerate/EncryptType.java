package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2017年8月17日
 */
public enum EncryptType {
	RSA("r","公私钥加密"),
	SHA256RSA("sr","sha256+rsa加密"),
	SHA256("s","sha256加密"),
	UNENCRYPTED("u","不加密");
	
	private String code;
	
	private String name;
	
	private EncryptType(String code,String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
