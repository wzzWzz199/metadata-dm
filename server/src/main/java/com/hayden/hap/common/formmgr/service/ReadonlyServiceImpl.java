package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.control.ListDataCtrlVO;
import com.hayden.hap.common.formmgr.entity.ListDataVO;
import com.hayden.hap.common.formmgr.itf.IReadonlyService;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.utils.VariableUtils;
import com.hayden.hap.wf.itf.IWorkflowButtonService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2017年4月24日
 */
@Service("readonlyService")
public class ReadonlyServiceImpl implements IReadonlyService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReadonlyServiceImpl.class);

	@Autowired(required=false)
	private IWorkflowButtonService workflowButtonService;
	
	@Autowired
	private IFormService formService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	/** 
	 * 获取只读状态
	 * @see com.hayden.hap.common.formmgr.itf.IReadonlyService#getReadonlyStatus(com.hayden.hap.common.func.entity.FuncVO, com.hayden.hap.common.common.entity.AbstractVO, boolean)
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2017年4月24日
	 */
	@Override
	public boolean getReadonlyStatus(FuncVO funcVO, AbstractVO businessVO, boolean settedReadonlyStatus) throws HDException {
		if(settedReadonlyStatus)
			return settedReadonlyStatus;
		return getReadonlyStatus(funcVO, businessVO);
	}

	/** 
	 * 获取只读状态
	 * @see com.hayden.hap.common.formmgr.itf.IReadonlyService#getReadonlyStatus(com.hayden.hap.common.func.entity.FuncVO, com.hayden.hap.common.common.entity.AbstractVO)
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2017年4月24日
	 */
	@Override
	public boolean getReadonlyStatus(FuncVO funcVO, AbstractVO businessVO) throws HDException {
		boolean readonly = calculateReadonlyExpression(funcVO.getFunc_readonly_exp(), businessVO);
		return readonly;//workflowButtonService.getReadonlyStatus(funcVO, businessVO, readonly);
	}
	
	/**
	 * 计算只读表达式
	 * @param exp
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年6月30日
	 */
	private boolean calculateReadonlyExpression(String exp, AbstractVO vo) throws HDException {
		if(StringUtils.isEmpty(exp)) 
			return false;
		
		exp = VariableUtils.replaceFormItemParam(exp, vo);
		exp = VariableUtils.replaceSystemParam(exp);
		
		ScriptEngineManager factory = new ScriptEngineManager();  
		ScriptEngine engine = factory.getEngineByName("JavaScript"); 
		
		Object result;
		try {
			result = engine.eval(exp);
		} catch (ScriptException e) {
			logger.error(e.getMessage(), e);
			throw new HDException(e);
		}
		return Boolean.valueOf(result.toString());
	}

	/** 
	 * 包装只读状态
	 * @see com.hayden.hap.common.formmgr.itf.IReadonlyService#wrapReadonlyStatus(com.hayden.hap.common.func.entity.FuncVO, com.hayden.hap.common.common.entity.VOSet)
	 * @author zhangfeng
	 * @date 2017年4月24日
	 */
	@Override
	public ListDataVO wrapReadonlyStatus(FuncVO funcVO, VOSet<? extends AbstractVO> voSet, Long tenantid) throws HDException {
		List<? extends AbstractVO> list = voSet.getVoList();
		if(list==null || list.size()==0)
			return new ListDataVO(voSet);
		
		FormVO formVO = formService.getFormVOByFunccode(funcVO.getFunc_code(), tenantid);
		if(formVO==null || StringUtils.isEmpty(formVO.getQuery_table_code()))
			return new ListDataVO(voSet);
		
		String tableName = formVO.getQuery_table_code();
		String pkColname = tableDefService.getPkColName(tableName);
		
		List<ListDataCtrlVO> ctrlList = new ArrayList<>();
		for(AbstractVO vo : list) {
			boolean readonly = getReadonlyStatus(funcVO, vo);
			ListDataCtrlVO ctrlVO = new ListDataCtrlVO();
			ctrlVO.setId(vo.getLong(pkColname));
			ctrlVO.setReadonly(readonly);
			ctrlList.add(ctrlVO);
		}
		
		ListDataVO listDataVO = new ListDataVO(voSet);
		listDataVO.setCtrlList(ctrlList);
 		return listDataVO;
	}

}
