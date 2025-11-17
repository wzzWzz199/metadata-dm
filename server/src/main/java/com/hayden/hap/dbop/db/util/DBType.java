package com.hayden.hap.dbop.db.util;

/** 
 * 
 * @ClassName: DBType 
 * @Description: 数据库类别枚举
 * @author LUYANYING
 * @date 2015年3月31日 下午2:05:41 
 * @version V1.0   
 *
 */
public enum DBType {
	ORACLE("ORACLE", "ORACLE", ""),
	MYSQL("MYSQL", "MYSQL", ""),
	DM("DM","DM",""),
	MARIADB("MARIADB", "MARIADB", ""),
	DERBY("DERBY", "DERBY", ""),
	SQLSERVER("SQLSERVER", "SQLSERVER", ""),
	SYBASE("SYBASE", "SYBASE", ""),
	POSTGRESQL("POSTGRESQL", "POSTGRESQL", ""),
	HSQL("HSQL", "HSQL", ""),
	DB2("DB2", "DB2", "");
	
	private String code;
	private String name;
	private String desc;
	
	private DBType(String code, String name, String desc){
		this.code = code;
		this.name = name;
		this.desc = desc;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
