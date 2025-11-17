package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.CommonVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.TreeNodeVO;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.form.service.DefaultTableCallback;
import com.hayden.hap.common.formmgr.action.ActionHandler;
import com.hayden.hap.common.formmgr.constant.QuerySelectConstant;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.MetaData;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IListFormService;
import com.hayden.hap.common.formmgr.itf.IMetaDataService;
import com.hayden.hap.common.formmgr.itf.IQuerySelectorService;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月23日
 */
@Service(value="querySelectorService")
public class QuerySelectorServiceImpl implements IQuerySelectorService{

	@Autowired
	private IFormService formService;
	
	@Autowired
	private IListFormService listFormService;
	
	@Autowired
	private IFormItemPCService formItemPCService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IMetaDataService metaDataService;
	
	/**
	 * pc端列查询选择的列表查询
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年6月7日
	 */
	@Override
	public VOSet<? extends AbstractVO> querySelectorListQuery(
			FormParamVO formParamVO) throws HDException {				
		return listFormService.listQuery4querySelect(formParamVO);
	}
	
	/**
	 * pc端查询选择统一接口，能干三件事，获取结构数据、列表查询、全息查询树数据
	 * @param formParamVO
	 * @param operaType
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年6月7日
	 */
	@Override
	public ReturnResult<?> querySelector(FormParamVO formParamVO, Map<String,String> item2FuncMap, Long userid, Long tenantid) throws HDException {
		String originFuncode = formParamVO.getFuncCode();
		String actualFuncCode = formParamVO.getReqParamVO().getQuerySelectFunc();
		if (StringUtils.isEmpty(actualFuncCode)) {
			actualFuncCode=parseQueryselectFuncCode(formParamVO, tenantid, item2FuncMap);
		}
		formParamVO.setFuncCode(actualFuncCode);
		if(StringUtils.isEmpty(originFuncode)) {
			originFuncode = actualFuncCode;
		}
		
		String operaType = formParamVO.getRequest().getParameter("isGetMetaData");
		if(QuerySelectConstant.LIST_QUERY.equals(operaType)) {//查询选择列表查询
			ActionHandler.handleQuerySelectorBeforeQuery(formParamVO, originFuncode, formParamVO.getFitemCode(), tenantid);
			VOSet<? extends AbstractVO> voSet = querySelectorListQuery(formParamVO);
			voSet.setSql(null);
			return new ReturnResult<>(voSet);
		}else if(QuerySelectConstant.GET_METADATA.equals(operaType)) {//查询选择结构数据
//			Long userid = CurrentEnvUtils.getUserId();
			MetaData metaData = metaDataService.getQuerySelectorMetaData(formParamVO, tenantid, userid);
			return new ReturnResult<>(metaData);
		}else if(QuerySelectConstant.GET_TREEDATA.equals(operaType)) {//查询选择 全息查询树数据
			List<TreeNodeVO> treeData = getQuerySelectorTreeData(formParamVO, tenantid);
			return new ReturnResult<>(treeData);
		}else if(QuerySelectConstant.LIST_QUERY_4_SELECT.equals(operaType)) {//下拉框列表获取
			ActionHandler.handleQuerySelectorBeforeQuery(formParamVO, originFuncode, formParamVO.getFitemCode(), tenantid);
			VOSet<? extends AbstractVO> voSet = querySelectorListQuery(formParamVO);
			assignDictDataid(voSet.getVoList(), actualFuncCode, tenantid);
			return new ReturnResult<>(voSet.getVoList());
		}
		
		throw new HDException("请求参数错误，没有标记请求数据类型.");
	}
	
	
	private void assignDictDataid(List<? extends AbstractVO> list, String funccode, Long tenantid) {
		if(ObjectUtil.isEmpty(list))
			return;
		FormVO formVO = formService.getFormVOByFunccode(funccode, tenantid);
		String tablecode = formVO.getQuery_table_code();
		String pkcolname = tableDefService.getPkColName(tablecode);
		
		for(AbstractVO vo : list) {
			vo.set("dictdataid", vo.get(pkcolname));
		}
	}
	
	/**
	 * 获取查询选择树数据
	 * @param formParamVO
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年6月7日
	 */
	public List<TreeNodeVO> getQuerySelectorTreeData(FormParamVO formParamVO, Long tenantid) throws HDException {
//		String actualFuncCode = parseQueryselectFuncCode(formParamVO, tenantid, item2FuncMap);
		FuncVO funcVO = funcService.queryByFunccode(formParamVO.getFuncCode(),tenantid);		
//		formParamVO.setFuncCode(actualFuncCode);
		formParamVO.setFuncVO(funcVO);
		return listFormService.getTreeData(formParamVO);
	}
	
	/**
	 * 移动端查询选择的列表查询
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IQuerySelectorService#querySelectorMListQuery(com.hayden.hap.common.formmgr.entity.FormParamVO)
	 * @author zhangfeng
	 * @date 2017年4月19日
	 */
	@Override
	public List<CommonVO> querySelectorMListQuery(
			FormParamVO formParamVO) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		
		List<? extends FormItemVO> itemVOs = formItemPCService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);
		
		String inputConfig = null;
		for(FormItemVO formItemVO : itemVOs) {
			if(formItemVO.getFitem_code().equals(formParamVO.getFitemCode())) {
				inputConfig = formItemVO.getFitem_input_config();
				break;
			}
		}
		
		if(inputConfig==null) {
			throw new HDException("没有找到对应输入设定");
		}
		
		QueryselectorInputConfigVO inputConfigVO = InputConfigUtils.getQueryselectorInputConfigVO(inputConfig);
		
		String funcCode = inputConfigVO.getFunccode();
		formParamVO.setFuncCode(funcCode);
				
		List<FormItemPCVO> formItemVOs = formItemPCService.getFormItemsByFunccode(funcCode, tenantid);
		VOSet<? extends AbstractVO> voset = listFormService.listQuery4Mobile(formParamVO);		
		
		String pkColumn = tableDefService.getPkColName(new DefaultTableCallback(funcCode, null, tenantid));
		
		List<CommonVO> result = new ArrayList<CommonVO>();
		for(AbstractVO vo : voset.getVoList()) {
			CommonVO commonVO = new CommonVO();
			for(FormItemPCVO formItemVO : formItemVOs) {
				if(!ObjectUtil.isTrue(formItemVO.getFitem_show_list()))
					continue;
				String fitemCode = formItemVO.getFitem_code();
				commonVO.set(fitemCode, vo.get(fitemCode));
			}
			commonVO.set(pkColumn, vo.get(pkColumn));
			result.add(commonVO);
		}
		
		return result;
	}

	/**
	 * 解析查询选择的功能编码
	 * @param formParamVO
	 * @param fitemCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月12日
	 */
	@Override
	public String parseQueryselectFuncCode(FormParamVO formParamVO, Long tenantid, 
			Map<String,String> item2FuncMap) throws HDException {
		String fitemCode = formParamVO.getFitemCode();
		String[] fitemCodeArr = fitemCode.split(",");
		String nextFunc = formParamVO.getFuncCode();
		for(String itemCode : fitemCodeArr) {
			nextFunc = parseQueryselectFuncCode(nextFunc, itemCode, tenantid, item2FuncMap);
		}
		return nextFunc;
	}

	/**
	 * 根据当前功能编码和字段编码，解析得到该字段查询选择的功能编码
	 * @param funcCode
	 * @param fitemCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月12日
	 */
	private String parseQueryselectFuncCode(String funcCode, String fitemCode, Long tenantid, 
			Map<String,String> item2FuncMap) throws HDException {
		if(item2FuncMap!=null) {//得到功能选择的功能编码
			String nextFuncCode = item2FuncMap.get(fitemCode);
			if(nextFuncCode!=null) {
				return nextFuncCode;
			}
		}
		if(StringUtils.isEmpty(fitemCode))
			throw new HDException("字段编码为空...");
		
		List<FormItemPCVO> itemVOs = formItemPCService.getFormItemsByFunccode(funcCode, tenantid);
		FormItemVO formItemVO = null;
		for(FormItemVO itemVO : itemVOs) {
			if(fitemCode.equals(itemVO.getFitem_code())) {
				formItemVO = itemVO;
				break;
			}
		}
		
		if(formItemVO==null) {
			throw new HDException("没有找到功能"+funcCode+"的字段:"+fitemCode);
		}
		
		String inputConfigStr = formItemVO.getFitem_input_config();
		if(StringUtils.isEmpty(inputConfigStr)) {
			throw new HDException("功能"+funcCode+"的字段"+fitemCode+"没有配置查询选择输入设定");
		}
		
		QueryselectorInputConfigVO configVO = InputConfigUtils.getQueryselectorInputConfigVO(inputConfigStr);
		
		return configVO.getFunccode();
	}

	
}
