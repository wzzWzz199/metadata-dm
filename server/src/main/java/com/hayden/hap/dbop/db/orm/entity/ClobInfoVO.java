package com.hayden.hap.dbop.db.orm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * clob对象，包括是否处理clob、sql的解析结果、clob列等。
 * @author wangyi
 * @date 2018年1月2日
 */
@Data
public class ClobInfoVO implements Serializable{
	private boolean dealClob;
	private SqlParseVO sqlParseVO;
	//clob字段集
	List<String> clobCols;
}
