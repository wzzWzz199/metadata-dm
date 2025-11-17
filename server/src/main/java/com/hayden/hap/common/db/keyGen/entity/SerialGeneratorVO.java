package com.hayden.hap.common.db.keyGen.entity;

import com.hayden.hap.common.common.entity.BaseVO;

/** 
 * 
 * @ClassName: SerialGeneratorVO 
 * @Description: 序列号生成器实体类
 * @author LUYANYING
 * @date 2015年5月27日 下午3:02:26 
 * @version V1.0   
 *
 */
public class SerialGeneratorVO extends BaseVO{
	/**
	 * serialVersionUID:TODO().
	 */
	private static final long serialVersionUID = 1L;
	private Long serialgenid;
	private String gencode;
	private String gennext;
	private Long gencache;
	private String genprefix;
	private long counter;
	
	public SerialGeneratorVO(){
		super("SY_SERIAL_GENERATOR");
	}
	
	public String getGencode() {
		return gencode;
	}
	public void setGencode(String gencode) {
		this.gencode = gencode;
	}
	public String getGennext() {
		return gennext;
	}
	public void setGennext(String gennext) {
		this.gennext = gennext;
	}
	public String getGenprefix() {
		return genprefix;
	}
	public void setGenprefix(String genprefix) {
		this.genprefix = genprefix;
	}
	public Long getGencache() {
		return gencache;
	}
	public void setGencache(Long gencache) {
		this.gencache = gencache;
	}
	
	public long getCounter() {
		return counter;
	}
	public void setCounter(long counter) {
		this.counter = counter;
	}

	public Long getSerialgenid() {
		return serialgenid;
	}

	public void setSerialgenid(Long serialgenid) {
		this.serialgenid = serialgenid;
	}
}
