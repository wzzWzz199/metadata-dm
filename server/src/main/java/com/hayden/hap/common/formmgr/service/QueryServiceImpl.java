package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.ShouldBeCatchException;
import com.hayden.hap.common.enumerate.FitemTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.formmgr.itf.IQueryService;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.query.MultSelectWhereclausParser;
import com.hayden.hap.common.formmgr.query.strategy.IQueryStrategy;
import com.hayden.hap.common.formmgr.query.strategy.QueryStrategyFactory;
import com.hayden.hap.common.formmgr.utils.ValidateUtils;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.serial.VOObjectMapper;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author zhangfeng
 * @date 2018年8月23日
 */
@Service("queryService")
public class QueryServiceImpl implements IQueryService {
	
	private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class);
	
	@Autowired
	private IFormItemService formItemService; 

	@SuppressWarnings("unchecked")
	@Override
	public String parseQueryStr(String queryString, String funcCode) throws HDException {
		VOObjectMapper objectMapper = (VOObjectMapper)AppServiceHelper.findBean("voObjectMapper");
		Map<String,Object> queryMap = new HashMap<String,Object>();
		
		try {
			queryMap = objectMapper.readValue(queryString, Map.class);
		} catch (IOException e) {
			logger.error("查询数据参数转json报错："+e.getMessage());
			e.printStackTrace();
			throw new HDException("查询条件有误,请输入合法的数据");
		}
	
		
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<? extends FormItemVO> itemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		
		Map<String,FormItemVO> map = getQueryField(itemVOs);
		List<Message> messages = ValidateUtils.validateQueryNotnull(itemVOs, queryMap);
		if(messages != null && messages.size()>0) {
			throw new HDException("错误信息：", messages);
		}
		
		StringBuilder sb = new StringBuilder();
		for(Entry<String, Object> entity:queryMap.entrySet()) {
			if(entity.getValue()==null || "".equals(entity.getValue())) {
				continue;
			}
			
			FormItemVO formItemVO = map.get(entity.getKey());
			
			String queryStrategy = getQueryStrategy(formItemVO);
			if(queryStrategy!=null) {
				String fitem_code = formItemVO.getFitem_code();
				String value = queryMap.get(fitem_code).toString();
				String whereStr = getWhereStrByQs(formItemVO.getFitem_code(), value, queryStrategy);
				sb.append(whereStr);
				continue;
			}
			
			//如果传过来的查询条件，既不是表字段也不是视图字段，则扔掉
			if(formItemVO!=null 
					&& !FitemTypeEnum.TABLE.getCode().equals(formItemVO.getFitem_type()) 
					&& !FitemTypeEnum.VIEW.getCode().equals(formItemVO.getFitem_type()) ) {
				continue;
			}
			
			try {
				String whereTemp = parseQueryFiled(entity,map);
				sb.append(" and ");
				sb.append(whereTemp);
			}catch(ShouldBeCatchException e) {
				continue;
			}
		}
		
		return sb.length()>4?sb.substring(4):"";
	}
	
	private String getQueryStrategy(FormItemVO formItemVO) throws HDException {
		if(formItemVO==null)
			return null;
		
		if(StringUtils.isBlank(formItemVO.getFitem_input_config_query()))
			return null;
		
		@SuppressWarnings("unchecked")
		Map<String,Object> inputConfig = JsonUtils.parse(formItemVO.getFitem_input_config_query(), Map.class);
		if(inputConfig.get("qs")==null || StringUtils.isEmpty(inputConfig.get("qs").toString()))
			return null;
		
		return inputConfig.get("qs").toString();
	}
	
	private String getWhereStrByQs(String fitem_code, String value, String queryStrategy) throws HDException {
		IQueryStrategy strategy = QueryStrategyFactory.getInstance().getQueryStrategy(queryStrategy);
		String where = strategy.getQueryWhere(fitem_code, value);
		if(StringUtils.isEmpty(where))
			return where;
		
		if(where.trim().startsWith("and")) {
			return where;
		}
		
		return " and "+where;
	}

	/**
	 * 根据表单编码查询该表单的普通查询字段
	 * @param formCode
	 * @return key为表单字段编码，value为表单字段实体
	 * @author zhangfeng
	 * @date 2015年9月21日
	 */
	private Map<String,FormItemVO> getQueryField(List<? extends FormItemVO> list) {
		Map<String,FormItemVO> map = new HashMap<String, FormItemVO>();
		
		List<FormItemVO> queryOneList = new ArrayList<>();
		for(FormItemVO formItemVO : list) {
			if(StringUtils.isNotEmpty(formItemVO.getFitem_query_one())) {
				queryOneList.add(formItemVO);
			}
		}
		
		for(FormItemVO fieldVO:queryOneList) {
			map.put(fieldVO.getFitem_code(), fieldVO);
		}
		return map;
	}
	
	private String parseQueryFiled(Entry<String, Object> queryEntity, Map<String,FormItemVO> map) throws ShouldBeCatchException {
		String key = queryEntity.getKey();
		FormItemVO fieldVO = map.get(key);
		String queryMethodStr;
		if(fieldVO==null && key.startsWith("to_")) {//如果是范围查询的上限
			key = key.substring(3);//去掉to_前缀
			fieldVO = map.get(key); 			
			if(fieldVO==null) {
				logger.error("编码为："+key.substring(3)+"的表单字段不能只有第二种查询方式");
				throw new RuntimeException("编码为："+key.substring(3)+"的表单字段不能只有第二种查询方式");			
			}
			queryMethodStr = fieldVO.getFitem_query_two();			
		}else if(fieldVO!=null){
			queryMethodStr = fieldVO.getFitem_query_one();
		}else {
			logger.error("根据参数key没有找到对应的表单字段:"+key);
			throw new ShouldBeCatchException("根据参数key没有找到对应的表单字段");
		}
		
		return parseWhereClaus(fieldVO, queryEntity.getValue(), queryMethodStr, key);
	}
	
	@Override
	public String parseWhereClaus(FormItemVO fieldVO, Object oldValue, String queryMethodStr, String key) {
		boolean isNum = true;
		String value = oldValue+"";
		if(!"2".equals(fieldVO.getFitem_data_type())) {//数据类型是 字符串
			MultSelectWhereclausParser whereclausParser = new MultSelectWhereclausParser();
			if(whereclausParser.support(fieldVO, oldValue, queryMethodStr, key)) {
				return whereclausParser.parse(fieldVO, oldValue, queryMethodStr, key);
			}
			value = "'"+oldValue+"'";
			isNum = false;
		}		
		if("eq".equals(queryMethodStr)) {	
			return key+ " = " + value;
		}else if("gt".equals(queryMethodStr)) {
			return key+ " > " + value;
		}else if("ge".equals(queryMethodStr) || "range".equals(queryMethodStr)) {
			return key+ " >= " + value;
		}else if("lt".equals(queryMethodStr)) {
			return key+ " < " + value;
		}else if("le".equals(queryMethodStr)) {
			return key+ " <= " + value;
		}else if("not_eq".equals(queryMethodStr)) {
			return key+ " != " + value;
		}else if("in".equals(queryMethodStr)) {
			if(String.valueOf(value).indexOf(",")>=0) {
				if(isNum) {
					return key+ " in (" + value+")";
				}else {
					return key+ " in (" + String.valueOf(value).replaceAll(",", "','")+")";
				}
			}else {
				return key+ " = " + value;
			}			
		}else if("not_in".equals(queryMethodStr)) {
			if(String.valueOf(value).indexOf(",")>=0) {
				if(isNum) {
					return key+ " not in (" + value+")";
				}else {
					return key+ " not in (" + String.valueOf(value).replaceAll(",", "','")+")";
				}
			}else {
				return key+ " != " + value;
			}
		}else if("anywhere".equals(queryMethodStr)) {
			return key+ " like '%" + oldValue+ "%'";
		}else if("start".equals(queryMethodStr)) {
			return key+ " like '" + oldValue+ "%'";
		}else if("end".equals(queryMethodStr)) {
			return key+ " like '%" + oldValue +"'";
		}else
			return key+ " = " + value;
	}
}
