package com.hayden.hap.common.enumerate;

/**
 * 字典类型枚举
 * @author zhangfeng
 * @date 2014年11月26日
 */
public enum DictType implements IEnum{
	LIST("1","列表"),TREE("2","树"),PARENT_TREE("3","父子树");
		
	private String id;
	
	private String tname;
	
	private DictType(String id,String tname) {
		this.id = id;
		this.tname = tname;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	@Override
	public String getCode() {
		return id;
	}

	@Override
	public String getName() {
		return tname;
	}

	@Override
	public EnumEntity toEntity() {		
		return new EnumEntity(this.id, this.tname);
	}
	
}
