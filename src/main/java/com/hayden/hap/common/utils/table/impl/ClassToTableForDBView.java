package com.hayden.hap.common.utils.table.impl;

import com.hayden.hap.common.common.entity.BaseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月18日
 */
public class ClassToTableForDBView extends AbstractClassToTableForDB {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClassToTableForDBView.class);

	/**
	 * 视图名前缀
	 */
	private static final String VIEW_PREFIX = "V_";
	
	
	@Override
	public String getTableName(Class<? extends BaseVO> clazz) {
		BaseVO baseVO = null;
		try {
			baseVO = clazz.newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		String tableName = baseVO.getTableName();
		return VIEW_PREFIX+tableName;
	}	

}
