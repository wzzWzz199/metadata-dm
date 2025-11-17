package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.formmgr.entity.Id2NameVO;

import java.util.List;

/**
 * 主键返名称策略接口
 * @author zhangfeng
 * @date 2016年9月9日
 */
public interface IId2NameStrategy {

	/**
	 * 查询选择，单选时候给名称字段赋值
	 * @param abstractVO
	 * @param inputConfigVO
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2016年9月8日
	 */
	public void assignName4single(AbstractVO abstractVO, Id2NameVO id2NameVO);

	/**
	 * 查询选择，单选时候批量给名称字段赋值
	 * @param abstractVOs
	 * @param inputConfigVO
	 * @param tenantid
	 * @param PKposition 
	 * @author zhangfeng
	 * @date 2016年9月8日
	 */
	public void assignName4single(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO);

	/**
	 * 查询选择，多选选时候给名称字段赋值
	 * @param abstractVO
	 * @param inputConfigVO
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2016年9月8日
	 */
	public void assignName4multiple(AbstractVO abstractVO, Id2NameVO id2NameVO);

	/**
	 * 查询选择，多选选时候批量给名称字段赋值
	 * @param abstractVOs
	 * @param inputConfigVO
	 * @param tenantid
	 * @param PKposition 
	 * @author zhangfeng
	 * @date 2016年9月8日
	 */
	public void assignName4multiple(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO);
}
