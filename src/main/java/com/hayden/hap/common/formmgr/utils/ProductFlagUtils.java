package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.enumerate.ProductFlagEnum;
import com.hayden.hap.common.utils.SyConstant;

/**
 * 产品/项目标识工具类
 * @author zhangfeng
 * @date 2017年2月20日
 */
public class ProductFlagUtils {
	private static final String PRODUCT_FLAG_COLUMN = "product_flag";

	/**
	 * 初始化产品/项目标识<br/>
	 * 海顿租户初始化为产品，其它租户初始化为项目
	 * @param vo
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2017年2月20日
	 */
	public static void initProductFlag(AbstractVO vo, Long tenantid) {
		if(SyConstant.TENANT_HD.equals(tenantid)) {
			vo.set(PRODUCT_FLAG_COLUMN, ProductFlagEnum.PRODUCT.getCode());
		}else {
			vo.set(PRODUCT_FLAG_COLUMN, ProductFlagEnum.PROJECT.getCode());
		}
	}
}
