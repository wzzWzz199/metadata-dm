package com.hayden.hap.common.func.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.itf.IQueryService;
import com.hayden.hap.common.formmgr.utils.Code2NameHandleUtils;
import com.hayden.hap.common.formmgr.utils.ItemMatcher;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.itf.IQueryChildrenService;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.JavaScriptExpressUtils;
import com.hayden.hap.common.utils.OriginalInfoUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VariableUtils;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author zhangfeng
 * @date 2017年7月18日
 */
@Service("queryChildrenService")
public class QueryChildrenServiceImpl implements IQueryChildrenService{

	@Autowired
	private IFormService formService;
	
	@Autowired
	private IFormItemService formItemService;
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IQueryService queryService;
	
	/**
	 * 根据父功能数据获取子功能数据
	 * @param parentList
	 * @param linkVO
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2017年7月18日
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractVO> List<T> getChildren(List<T> parentList,FuncLinkVO linkVO,Long tenantid) throws HDException {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		String queryStr = getWhereStr(parentList, linkVO, tenantid);
		dynaSqlVO.addWhereClause(queryStr);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		
		FormVO childFormVO = formService.getFormVOByFunccode(linkVO.getSub_func_code(),tenantid);
		if(childFormVO==null) {
			String errorMsg = MessageFormat.format("根据功能编码：{0}没有找到表单", linkVO.getSub_func_code());
			throw new HDRuntimeException(errorMsg);
		}
		String tableName = childFormVO.getQuery_table_code();
		VOSet<AbstractVO> voSet = baseService.query(tableName, dynaSqlVO);
		
		return (List<T>) voSet.getVoList();
	}
	
	/**
	 * 根据父功能数据获取子功能数据
	 * @param parentList
	 * @param linkVO
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractVO> List<T> getChildren4Export(List<T> parentList,FuncLinkVO linkVO,Long tenantid) throws HDException {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		String queryStr = getWhereStr(parentList, linkVO, tenantid);
		dynaSqlVO.addWhereClause(queryStr);
		String link_where = linkVO.getLink_where();
		dynaSqlVO.addWhereClause(link_where);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		
		FormVO childFormVO = formService.getFormVOByFunccode(linkVO.getSub_func_code(),tenantid);
		if(childFormVO==null) {
			String errorMsg = MessageFormat.format("根据功能编码：{0}没有找到表单", linkVO.getSub_func_code());
			throw new HDRuntimeException(errorMsg);
		}
		String tableName = childFormVO.getQuery_table_code();
		VOSet<AbstractVO> voSet = baseService.query(tableName, dynaSqlVO);
		
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(linkVO.getSub_func_code(), tenantid);
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		for(FormItemVO itemVO : formItemVOs) {
			if(InputTypeEnum.QUERY_SELECT.getCode().equals(itemVO.getFitem_input_type())) {
				Code2NameHandleUtils.handleQuerySelector(voSet.getVoList(), itemVO, currentDataTenantid);				
			}
		}
		
		return (List<T>) voSet.getVoList();
	}
	
	/**
	 * 匹配父子关系（键为父vo，值为子vo集合）
	 * @param parentList
	 * @param children
	 * @param linkVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	@Override
	public <T extends AbstractVO> Map<T,List<? extends AbstractVO>> matchChildren(List<T> parentList, 
			List<? extends AbstractVO> children, FuncLinkVO linkVO) {
		List<FuncLinkItemVO> itemVOs = linkVO.getLinkItems();
		//主子功能关联关系表达式
		StringBuilder condition = new StringBuilder(); 
		for(FuncLinkItemVO linkItemVO : itemVOs) {
			if(ObjectUtil.isTrue(linkItemVO.getLitem_isvalue()) 
					&& ObjectUtil.isTrue(linkItemVO.getLitem_iswhere())) {
				String parentProperty = linkItemVO.getLitem_main_field();//传值的主功能字段编码
				String childProperty = linkItemVO.getLitem_sub_field();//传值的子功能字段编码
				if (condition.toString().length()!=0) {
					condition.append(" && ");
				}
				condition.append("'#").append(parentProperty).append("#'=='%").append(childProperty).append("%'");
			}				
		}
		ScriptEngineManager factory = new ScriptEngineManager();  
		ScriptEngine engine = factory.getEngineByName("JavaScript"); 
		Map<T,List<? extends AbstractVO>> result = new LinkedHashMap<>();
		for(T parent : parentList) {	
			//解析条件中父对象字段
			String conexpress = replaceAuditExtendParam(condition.toString(), SyConstant.SY_FITEM_SEPARATOR,
					parent);
			List<? extends AbstractVO> list = result.get(parent);
			List<AbstractVO> matchChildList = new ArrayList<AbstractVO>();
			for (AbstractVO child : children) {
				//解析条件中子对象字段
				String express = replaceAuditExtendParam(conexpress, SyConstant.SY_CUSTOM_SEPARATOR,child);
				try {
					boolean expressFlag = JavaScriptExpressUtils.eval(express,engine);
					if (expressFlag) {
						matchChildList.add(child);
					}
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
			if(list==null) {
				list = new ArrayList<>(matchChildList);
				result.put(parent, list);	
			}else {
				list.retainAll(matchChildList);
			}	
		}
		return result;
	}
	
	private String replaceAuditExtendParam(String str, String splitStr, AbstractVO vo) {
		// TODO Auto-generated method stub
		if(!VariableUtils.hasFitemParam(str) && !VariableUtils.hasCustomFitemParam(str)) 
			return str;
		
		StringBuilder result = new StringBuilder();		
		String[] arr = str.split(splitStr);
		for(int i=0;i<arr.length;i++) {
			if(i%2==0) {
				result.append(arr[i]);
			}else {		
				Object value = OriginalInfoUtils.getOriginalInfo(arr[i], vo).toString();
				if(value==null) {
					result.append("");
				}else if(value instanceof Date) {
					String dateStr = DateFormatUtils.format((Date)value, SyConstant.DATE_TIME_PATTERN);
					result.append(dateStr);
				}else {
					result.append(value);
				}
								
			}
		}
		return result.toString();
	}

	/**
	 * 过滤
	 * @param list
	 * @param filterMethod
	 * @param matchStr
	 * @param property
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	private List<AbstractVO> filter(List<? extends AbstractVO> list, String filterMethod, String matchStr, String property) {
		List<AbstractVO> result = new ArrayList<>();
		ItemMatcher matcher = new ItemMatcher(matchStr, filterMethod);
		for(AbstractVO vo : list) {
			String value = vo.getString(property);
			if(matcher.matcher(value)) {
				result.add(vo);
			}
		}
		return result;
	}
	
	
	/**
	 * 将children进行分组
	 * @param children
	 * @param itemVOs
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	private Map<String,Map<String,List<AbstractVO>>> groupedByItemcode(List<? extends AbstractVO> children, List<FuncLinkItemVO> itemVOs) {
		Map<String,Map<String,List<AbstractVO>>> item2mapMap = new HashMap<>();
		for(FuncLinkItemVO linkItemVO : itemVOs) {
			if(ObjectUtil.isTrue(linkItemVO.getLitem_isvalue())) {
				Map<String,List<AbstractVO>> tempMap = new HashMap<>();
				item2mapMap.put(linkItemVO.getLitem_sub_field(), tempMap);
			}
		}
		
		for(AbstractVO child : children) {
			for(Entry<String,Map<String,List<AbstractVO>>> entry : item2mapMap.entrySet()) {
				String property = entry.getKey();
				String value = child.getString(property);
				Map<String,List<AbstractVO>> map = entry.getValue();
				List<AbstractVO> list = map.get(value);
				if(list==null){
					list = new ArrayList<>();
					map.put(value, list);
				}
				list.add(child);
			}
		}
		return item2mapMap;
	}
	
	@Override
	public <T extends AbstractVO> String getWhereStr(List<T> parentList,FuncLinkVO linkVO,Long tenantid) throws HDException {
		List<FuncLinkItemVO> itemVOs = linkVO.getLinkItems();
		if(ObjectUtil.isEmpty(itemVOs)) {
			throw new HDException("没有配置关联字段");
		}
		if(ObjectUtil.isEmpty(parentList)) {
			throw new HDException("没有父级数据");
		}
		
		StringBuilder sb = new StringBuilder("(");
		for(AbstractVO parent : parentList) {
			sb.append("(");
			String queryStr = parseFunclinkClause(itemVOs, parent);
			sb.append(queryStr);
			sb.append(") ");
			sb.append("or ");
		}
		sb.delete(sb.length()-3, sb.length());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * 解析关联功能条件
	 * @param funcLink
	 * @param parentVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月14日
	 */
	@Override
	public String parseFunclinkClause(List<FuncLinkItemVO> linkItems,AbstractVO parentVO) {
		StringBuilder sb = new StringBuilder();
		for(FuncLinkItemVO linkItem : linkItems) {
			if(ObjectUtil.isTrue(linkItem.getLitem_iswhere())) {
				if(ObjectUtil.isTrue(linkItem.getLitem_isconstant())) {
					sb.append(parseFuncLinkItemClause(linkItem,linkItem.getLitem_main_field()));
				}else {
					Object value = OriginalInfoUtils.getOriginalInfo(linkItem.getLitem_main_field(), parentVO);
//					Object value = parentVO.get(linkItem.getLitem_main_field());
					if(value!=null && value instanceof Date) {
						value = DateFormatUtils.format((Date)value, "yyyy-MM-dd HH:mm:ss");
					}
					sb.append(parseFuncLinkItemClause(linkItem,value));
				}
				sb.append(" and ");
			}
		}
		if(sb.length()>1) {
			sb.delete(sb.length()-4, sb.length());
		}
		return sb.toString();
	}
	
	/**
	 * 解析关联字段条件
	 * @param linkItem
	 * @param value
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月14日
	 */
	private String parseFuncLinkItemClause(FuncLinkItemVO linkItem,Object value) {
		String funcCode = linkItem.getSub_func_code();
		ObjectUtil.validNotNull(funcCode, "关联字段，子功能编码为空...");

		Long tenantid = CurrentEnvUtils.getTenantId();
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		FormItemVO subItemVO = null;
		for(FormItemVO itemVO : formItemVOs) {
			if(itemVO.getFitem_code().equals(linkItem.getLitem_sub_field())) {
				subItemVO = itemVO;
				break;
			}
		}		
		ObjectUtil.validNotNull(subItemVO, "功能关联字段有误，没有找到对应表单字段...");

		return queryService.parseWhereClaus(subItemVO, value, linkItem.getLitem_query_sign(), subItemVO.getFitem_code());
	}
}
