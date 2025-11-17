package com.hayden.hap.dbop.db.tableDef.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataTypeUtil {
	//存储数据类型的数据长度和小数位数的取值范围
	private static HashMap<String, String[]> dataTypeInputRangeMap = new HashMap<String, String[]>(){{
		//注意下下面的值范围，使用数组存储，最大长度为2。如果不含小数时，只需要定义数据长度的范围，长度为1。
		//默认数组第一个存储数据长度的范围，第二个存储小数位数的范围。
		//取值范围当为0时表示不含或者是不需要输入，例如DECIMAL的小数范围，输入0时表示不含小数。
		//整型
		put("TINYINT",new String[]{"1-3"});
		put("SMALLINT",new String[]{"1-5"});
		put("MEDIUMINT",new String[]{"1-7"});
		put("INTEGER",new String[]{"1-10"});
		put("BIGINT",new String[]{"1-19"});
		//字符串类型
		put("CHAR",new String[]{"1-255"});
		put("VARCHAR",new String[]{"1-2000"});
		//浮点型
		put("DECIMAL",new String[]{"1-65","0-30"});
		put("FLOAT",new String[]{"0-38","0-10"});
		put("DOUBLE",new String[]{"0-255","0-10"});
		//日期型，data不用设置
		put("TIMESTAMP",new String[]{"0-6"});
		put("TIME",new String[]{"0-6"});
		//位类型
		put("BIT",new String[]{"1-64"});
		//二进制字符串
		put("BINARY",new String[]{"1-255"});
		put("VARBINARY",new String[]{"1-6000"});
	}};

	private static HashMap<String, String> dataTypeOracleMap = new HashMap<String, String>(){{
		//日期型
		put("DATE", "DATE");
		put("DATETIME", "DATE");
		put("TIME", "DATE");
		put("TIMESTAMP", "TIMESTAMP");
		//字符串
		put("CHAR","CHAR");
		put("VARCHAR","VARCHAR2");
		put("TEXT","CLOB");
		put("LONGTEXT","CLOB");
		//二进制字符串
		put("BINARY","BLOB");
		put("VARBINARY","BLOB");
		put("BLOB","BLOB");
		put("LONGBLOB","BLOB");
	}};
	private static List<String> linkNumberList = new ArrayList<String>(){{
		add("TINYINT");
		add("SMALLINT");
		add("MEDIUMINT");
		add("INTEGER");
		add("INT");
		add("BIGINT");
		add("DECIMAL");
		add("FLOAT");
		add("DOUBLE");
		add("BIT");
	}};
	
	public static String getOracleColType(String colType){
		if(dataTypeOracleMap.containsKey(colType)){
			return dataTypeOracleMap.get(colType);
		}else{
			if(linkNumberList.contains(colType)){
				return "NUMBER";
			}
		}
		return null;
	}
	public static String validator(String dataType, int currentDataLength, int currentDecimalDigit) {
		String[] inputRanges = dataTypeInputRangeMap.get(dataType);
		
		if(inputRanges==null)
			return null;
		
		String dataLengthRange = inputRanges[0];
		String decimalDigitRange = null;
		if (inputRanges.length > 1)
			decimalDigitRange = inputRanges[1];		
		
		StringBuilder errInfo = new StringBuilder();

		String dataLengthInfo = validDataLength(dataLengthRange,currentDataLength);
		if (StringUtils.isNotEmpty(dataLengthInfo))
			errInfo.append("数据长度输入值").append(dataLengthInfo);

		String decimalDigitInfo = validDecimalDigit(decimalDigitRange,currentDecimalDigit);
		if (StringUtils.isNotEmpty(decimalDigitInfo)) {
			if (errInfo.length() != 0)
				errInfo.append(";");
			errInfo.append("小数位数输入值").append(decimalDigitInfo);
		}

		return errInfo.toString();
	}

	// 验证数据长度
	public static String validDataLength(String dataLengthRange, int currentDataLength) {
		return commValid(dataLengthRange, currentDataLength);
	};

	// 验证小数位数
	public static String validDecimalDigit(String decimalDigitRange, int currentDecimalDigit) {
		if (StringUtils.isNotEmpty(decimalDigitRange)) {
			return commValid(decimalDigitRange, currentDecimalDigit);
		}
		return null;
	};

	public static String commValid(String range, int currVal) {
		String[] tmpRanges = range.split("-");
		int startVal = Integer.valueOf(tmpRanges[0]);
		int endVal = Integer.valueOf(tmpRanges[1]);

		if (currVal < startVal || currVal > endVal)
			return "不在" + range + "范围内";

		return null;
	}

}
