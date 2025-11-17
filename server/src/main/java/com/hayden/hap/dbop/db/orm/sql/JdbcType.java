package com.hayden.hap.dbop.db.orm.sql;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: JdbcType
 * @Description: jdbc type枚举类
 * @author LUYANYING
 * @date 2015年4月17日 下午4:14:51
 * @version V1.0
 * 
 */
public enum JdbcType {
	ARRAY(Types.ARRAY, "ARRAY"), 
	BIT(Types.BIT, "BIT"), 
	TINYINT(Types.TINYINT, "TINYINT"), 
	SMALLINT(Types.SMALLINT, "SMALLINT"), 
	INTEGER(Types.INTEGER, "INTEGER"), 
	BIGINT(Types.BIGINT, "BIGINT"), 
	FLOAT(Types.FLOAT, "FLOAT"), 
	REAL(Types.REAL, "REAL"), 
	DOUBLE(Types.DOUBLE, "DOUBLE"), 
	NUMERIC(Types.NUMERIC, "NUMERIC"), 
	DECIMAL(Types.DECIMAL, "DECIMAL"), 
	CHAR(Types.CHAR, "CHAR"), 
	VARCHAR(Types.VARCHAR, "VARCHAR"),
	LONGVARCHAR(Types.LONGVARCHAR, "LONGVARCHAR"), 
	DATE(Types.DATE, "DATE"), 
	TIME(Types.TIME, "TIME"), 
	DATETIME(Types.DATE,"DATETIME"), // 达梦需要改 timestamp
	TIMESTAMP(Types.TIMESTAMP, "TIMESTAMP"), 
	BINARY(Types.BINARY, "BINARY"), 
	VARBINARY(Types.VARBINARY, "VARBINARY"), 
	LONGVARBINARY(Types.LONGVARBINARY, "LONGVARBINARY"), 
	NULL(Types.NULL, "NULL"), 
	OTHER(Types.OTHER, "OTHER"), 
	BLOB(Types.BLOB, "BLOB"), 
	CLOB(Types.CLOB, "CLOB"), 
	BOOLEAN(Types.BOOLEAN, "BOOLEAN"), 
	CURSOR(-10, "CURSOR"), // Oracle
	UNDEFINED(Integer.MIN_VALUE + 1000, "UNDEFINED"), 
	NVARCHAR(Types.NVARCHAR, "NVARCHAR"), // JDK6
	NCHAR(Types.NCHAR, "NCHAR"), // JDK6
	NCLOB(Types.NCLOB, "NCLOB"), // JDK6
	STRUCT(Types.STRUCT, "STRUCT"),
	//添加text的支持
	TEXT(Types.CLOB, "TEXT");

	private final int typeCode;
	private final String typeName;
	private static Map<Integer, JdbcType> codeLookup = new HashMap<Integer, JdbcType>();
	private static Map<String, JdbcType> nameLookup = new HashMap<String, JdbcType>();
	
	static {
		for (JdbcType type : JdbcType.values()) {
			codeLookup.put(type.typeCode, type);
			nameLookup.put(type.typeName, type);
		}
	}

	private JdbcType(int typeCode, String typeName) {
		this.typeCode = typeCode;
		this.typeName = typeName;
	}

	public static JdbcType forCode(int code) {
		return codeLookup.get(code);
	}
	public static JdbcType forName(String name) {
		return nameLookup.get(name);
	}

	public int getTypeCode() {
		return typeCode;
	}

	public String getTypeName() {
		return typeName;
	}
}
