package com.hayden.hap.common.utils.table.impl;

import com.hayden.hap.common.common.entity.BaseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月19日
 */
public class ClassToTableForDB extends AbstractClassToTableForDB {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClassToTableForDB.class);

	/** 
	 *
	 * @see com.hayden.hap.common.utils.table.itf.IClassToTable#getTableName(java.lang.Class)
	 * @author zhangfeng
	 * @date 2015年11月19日
	 */
	@Override
	public String getTableName(Class<? extends BaseVO> clazz) {
		BaseVO baseVO = null;
		try {
			baseVO = clazz.newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		String tableName = baseVO.getTableName();
		return tableName;
	}

}
