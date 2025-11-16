package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.formmgr.entity.Id2NameVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IId2NameStrategy;

import java.util.List;

/**
 * 主键返名称环境
 * @author zhangfeng
 * @date 2016年9月9日
 */
public class Id2NameContext {

	/**
	 * 多选flag
	 */
	protected static final String MULTI = "multi";
	
	private IId2NameStrategy strategy;
	
	/**
	 * 主键返名称环境类构造器
	 * @param strategy 返名称策略
	 * @author zhangfeng
	 * @date 2016年9月9日
	 */
	public Id2NameContext(IId2NameStrategy strategy) {
		this.strategy = strategy;
	}
	
	/**
	 * 赋值名称字段
	 * @param abstractVO
	 * @param id2NameVO 
	 * @author zhangfeng
	 * @date 2016年9月9日
	 */
	public void assignName(AbstractVO abstractVO, Id2NameVO id2NameVO) {
		QueryselectorInputConfigVO inputConfigVO = id2NameVO.getInputConfigVO();
		if(inputConfigVO.isIsmulti()) {
			strategy.assignName4multiple(abstractVO, id2NameVO);
		}else {
			strategy.assignName4single(abstractVO, id2NameVO);
		}
	}

	/**
	 * 批量赋值名称字段
	 * @param abstractVOs
	 * @param id2NameVO 
	 * @author zhangfeng
	 * @date 2016年9月9日
	 */
	public void assignName(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO) {
		QueryselectorInputConfigVO inputConfigVO = id2NameVO.getInputConfigVO();
		if(inputConfigVO.isIsmulti()) {
			strategy.assignName4multiple(abstractVOs, id2NameVO);
		}else {
			strategy.assignName4single(abstractVOs, id2NameVO);
		}

	}
}
