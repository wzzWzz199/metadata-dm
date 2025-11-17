package com.hayden.hap.common.upgrade.service;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.sqlcreate.entity.PatchXmlItemVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 租户脚本升级扩展父类
 * 
 * @author wushuangyang
 * @date 2016年8月1日
 */
public abstract class AbstractItemUpgrade {
	/**
	 * Logger for this class
	 */
	protected final Logger logger;

	public AbstractItemUpgrade() {
		logger = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * 管理租户升级模板方法
	 * @param tenant
	 * @param item
	 * @author wushuangyang
	 * @date 2016年8月1日
	 */
	public final void managerTenantUpgrade(Long tenantId, PatchXmlItemVO item)throws HDException {
		logger.debug("管理租户开始升级补丁包[{}]的item：[{}]。。。",item.getXmlFileName(),item.getItemName());
		try {
			doManagerTenantUpgrade(tenantId,item);
			logger.debug("管理租户升级成功补丁包[{}]的item：[{}]。。。",item.getXmlFileName(),item.getItemName());
		} catch (Exception e) {
			logger.error("管理租户升级成功补丁包[{}]的item：[{}]失败，发生异常：{}" ,item.getXmlFileName(),item.getItemName(),e.getMessage());
			throw e;
		}
	}
	
	
	/**
	 * 普通租户升级模板方法
	 * @param tenant
	 * @param item
	 * @author wushuangyang
	 * @date 2016年8月1日
	 */
	public final void commonTenantUpgrade(Long tenantId, PatchXmlItemVO item)throws HDException {
		logger.debug("普通租户开始[{}]升级补丁包[{}]的item：[{}]。。。",tenantId.toString(),item.getXmlFileName(),item.getItemName());
		try {
			doCommonTenantUpgrade(tenantId,item);
			logger.debug("普通租户[{}]升级补丁包[{}]的item：[{}]成功。。。",tenantId.toString(),item.getXmlFileName(),item.getItemName());
		} catch (Exception e) {
			logger.error("普通租户[{}]升级补丁包[{}]的item：[{}]失败，发生异常：{}" ,tenantId,item.getXmlFileName(),item.getItemName(),e.getMessage());
			throw e;
		}
	}
	
	
	/**
	 * 对管理租户进行升级
	 * @param tenant
	 * @param item 
	 * @author wushuangyang
	 * @date 2016年8月1日
	 */
	public abstract void doManagerTenantUpgrade(Long tenantId, PatchXmlItemVO item)throws HDException;
	
	
	/**
	 * 对普通租户进行升级
	 * @param tenant
	 * @param item 
	 * @author wushuangyang
	 * @date 2016年8月1日
	 */
	public abstract void doCommonTenantUpgrade(Long tenantId, PatchXmlItemVO item)throws HDException;

}
