package com.hayden.hap.common.common.entity;

import com.hayden.hap.common.utils.table.annotation.Column;

import java.util.Date;

/** 
 * @ClassName: BaseVO 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月22日 下午4:09:28 
 * @version V1.0   
 *  
 */
public class BaseVO extends CommonVO {
	/**
	 * serialVersionUID:TODO().
	 */
	private static final long serialVersionUID = 1L;

	/** 乐观锁版本 */
	@Column(type="INTEGER",length=50)
	private Integer ver=new Integer(1);
	
	/** 创建者(登录帐号) */
	@Column(type="bigint",length=19)
	private Long created_by;

	/** 创建时间 */	
	@Column(type="datetime",length=6)
	private Date created_dt =new Date();

	/** 最后更新者(登录帐号) */
	@Column(type="bigint",length=19)
	private Long updated_by;

	/** 最后更新时间 */
	@Column(type="datetime",length=6)
	private Date updated_dt=new Date();
	
	/** 逻辑删除标识 */
	@Column(type="tinyint",length=1)
	private Integer df = new Integer(0);
	
	/**
	 * 租户id
	 */
	@Column(type="INTEGER",length=19)
	private Long tenantid;
	
	/**
	 * 时间戳 
	 */
	private Long ts;
	
	public Long getTs() {
		return ts;
	}
	public void setTs(Long ts) {
		this.ts = ts;
	}
	public BaseVO(String tableName){
		super(tableName.toLowerCase());
	}
	public BaseVO(){
		
	}
	public Integer getVer() {
		return ver;
	}
	public void setVer(Integer ver) {
		this.ver = ver;
	}
	public Long getCreated_by() {
		return created_by;
	}
	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}
	public Date getCreated_dt() {
		return created_dt;
	}
	public void setCreated_dt(Date created_dt) {
		this.created_dt = created_dt;
	}
	public Long getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}
	public Date getUpdated_dt() {
		return updated_dt;
	}
	public void setUpdated_dt(Date updated_dt) {
		this.updated_dt = updated_dt;
	}
	public Integer getDf() {
		return df;
	}
	public void setDf(Integer df) {
		this.df = df;
	}
	public Long getTenantid() {
		return tenantid;
	}
	public void setTenantid(Long tenantid) {
		this.tenantid = tenantid;
	}
}
