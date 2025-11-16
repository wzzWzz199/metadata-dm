package com.hayden.hap.common.utils;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.exception.HDRuntimeException;
import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.utils.date.DateUtils;
import com.hayden.hap.common.utils.properties.EnvPropertiesUtil;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import com.hayden.hap.common.utils.variable.VarFunctionFactory;
import com.hayden.hap.common.utils.variable.itf.IVarFunction;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import javax.script.ScriptException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 变量处理工具类
 * @author zhangfeng
 * @date 2016年4月8日
 */
public class VariableUtils {
	
	private static final String USER_ID = "user_id";
	private static final String USER_CODE = "user_code";
	private static final String USER_NAME = "user_name";
	private static final String TENANTID = "tenantid";
	private static final String DATETIME = "datetime";
	private static final String ORG_ID = "org_id";
	private static final String ORG_NAME = "org_name";
	private static final String ORG_CODE = "org_code";
	private static final String IS_PRIVATE = "isprivate";
	private static final String IS_PUBLIC = "ispublic";
	
	/**
	 * 函数前缀
	 */
	private static final String FUNCTION_PRE = "f:";

//	private static Map<String,Object> getSyVariable() {
//		Map<String,Object> syVariable = new HashMap<>();
//		syVariable.put(USER_ID, CurrentEnvUtils.getUserId());
//		syVariable.put(USER_CODE,CurrentEnvUtils.getUserCode());
//		syVariable.put(USER_NAME,CurrentEnvUtils.getUserName());
//		syVariable.put(TENANTID,CurrentEnvUtils.getTenantId());
//		syVariable.put(ORG_ID, CurrentEnvUtils.getOrgId());
//		syVariable.put(ORG_NAME, CurrentEnvUtils.getOrgName());
//		syVariable.put(ORG_CODE, CurrentEnvUtils.getOrgCode());
//		syVariable.put(DATETIME,new Date());
//		syVariable.put(IS_PRIVATE,EnvPropertiesUtil.getIsPrivate());
//		syVariable.put(IS_PUBLIC,!EnvPropertiesUtil.getIsPrivate());
//		
//		return syVariable;
//	}
	
	private static Set<String> variableSet = new HashSet<>();
	static {
		variableSet.add(USER_ID);
		variableSet.add(USER_CODE);
		variableSet.add(USER_NAME);
		variableSet.add(TENANTID);
		variableSet.add(ORG_ID);
		variableSet.add(ORG_NAME);
		variableSet.add(ORG_CODE);
		variableSet.add(DATETIME);
		variableSet.add(IS_PRIVATE);
		variableSet.add(IS_PUBLIC);
	}
	
	private static Object getSyVariable(String param) {
		if(USER_ID.equals(param)) {
			return CurrentEnvUtils.getUserId();
		}
		else if(USER_CODE.equals(param)) {
			return CurrentEnvUtils.getUserCode();
		}
		else if(USER_NAME.equals(param)) {
			return CurrentEnvUtils.getUserName();
		}
		else if(TENANTID.equals(param)) {
			return CurrentEnvUtils.getTenantId();
		}
		else if(ORG_NAME.equals(param)) {
			return CurrentEnvUtils.getOrgName();
		}
		else if(ORG_ID.equals(param)) {
			return CurrentEnvUtils.getOrgId();
		}
		else if(ORG_CODE.equals(param)) {
			return CurrentEnvUtils.getOrgCode();
		}
		else if(DATETIME.equals(param)) {
			return new Date();
		}
		else if(IS_PRIVATE.equals(param)) {
			return EnvPropertiesUtil.getIsPrivate();
		}
		else if(IS_PUBLIC.equals(param)) {
			return !EnvPropertiesUtil.getIsPrivate();
		}
		return null;
	}
	
	/**
	 * 替换系统变量
	 * @param str
	 * @param dataType
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年7月1日
	 */
	public static Object replaceSystemParam4Obj(String str,String dataType) throws HDException {
		if(!hasSysParam(str)) 
			return str;
		
		StringBuilder result = new StringBuilder();		
//		Map<String,Object> syVariable = getSyVariable();		
		
		String[] arr = str.split(SyConstant.SY_VARIABLE_SEPARATOR);
		for(int i=0;i<arr.length;i++) {
			if(i%2==0) {
				result.append(arr[i]);
			}else {
				String param = arr[i];
				if(hasFunction(param)) {
					Object value = getVarFunctionValue(param);
					return value;
				}else if(variableSet.contains(param.toLowerCase())) {
					if(DataTypeEnum.DATE.getCode().equals(dataType)
							|| DataTypeEnum.NUMBER.getCode().equals(dataType)) {
						return getSyVariable(param.toLowerCase());
					}else {
						result.append(getSyVariable(param.toLowerCase())==null?"":getSyVariable(param.toLowerCase()));
					}					
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 替换系统变量
	 * @param str
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年7月1日
	 */
	public static String replaceSystemParam(String str) throws HDException {
		if(!hasSysParam(str)) 
			return str;
		
		StringBuilder result = new StringBuilder();		
//		Map<String,Object> syVariable = getSyVariable();		
		
		String[] arr = str.split(SyConstant.SY_VARIABLE_SEPARATOR);
		for(int i=0;i<arr.length;i++) {
			if(i%2==0) {
				result.append(arr[i]);
			}else {
				String param = arr[i];
				if(hasFunction(param)) {
					String value = getVarFunctionStringValue(param);
					result.append(value);
				}else if(variableSet.contains(param.toLowerCase())) {
					Object value = getSyVariable(param.toLowerCase());
					if(value==null) {
						result.append("");
					}else if(value instanceof Date) {
						String dateStr = DateFormatUtils.format((Date)value, SyConstant.DATE_TIME_PATTERN);
						result.append(dateStr);
					}else {
						result.append(getSyVariable(param.toLowerCase()));
					}
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 替换系统变量
	 * @param str
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年7月1日
	 */
	public static String replaceSystemParam(String str,FormItemVO formItemVO) throws HDException {
		if(!hasSysParam(str)) 
			return str;
		
		StringBuilder result = new StringBuilder();		
//		Map<String,Object> syVariable = getSyVariable();		
		
		String[] arr = str.split(SyConstant.SY_VARIABLE_SEPARATOR);
		for(int i=0;i<arr.length;i++) {
			if(i%2==0) {
				result.append(arr[i]);
			}else {
				String param = arr[i];
				if(hasFunction(param)) {
					String value = getVarFunctionStringValue(param);
					result.append(value);
				}else if(variableSet.contains(param.toLowerCase())) {
					Object value = getSyVariable(param.toLowerCase());
					if(value==null) {
						result.append("");
					}else if(value instanceof Date) {
						DateInputConfigVO inputConfigVO = InputConfigUtils.getDateInputConfigVO(formItemVO.getFitem_input_config());
						String dateStr = DateUtils.getDateStr((Date)value, inputConfigVO);
						result.append(dateStr);
					}else {
						result.append(value);
					}
				}
			}
		}
		return result.toString();
	}
	

	/**
	 * 是否含有函数
	 * @param str
	 * @return 
	 * @author zhangfeng
	 * @date 2018年7月9日
	 */
	private static boolean hasFunction(String express) {
		if(StringUtils.isEmpty(express))
			return false;
		return express.indexOf(FUNCTION_PRE)>-1;
	}
	
	private static final Pattern p = Pattern.compile("(?<=')[^']*(?=')"); 
	
	private static Object getVarFunctionValue(String express) throws HDException {
		String funcStr = express.substring(express.indexOf(FUNCTION_PRE)+2, express.length());
		String funcname = funcStr.substring(0, funcStr.indexOf("("));
		
		List<String> params = new ArrayList<>();
//		Pattern p = Pattern.compile("(?<=')[^']*(?=')");  
		Matcher m = p.matcher(funcStr);  
		while(m.find()){  
			String param = m.group().trim();
			if(!",".equals(param)) {
				params.add(param);
			}
		}
		
		IVarFunction varFunction = VarFunctionFactory.getInstance().getVarFunction(funcname);
		return varFunction.getValue(params);
	}
	
	private static String getVarFunctionStringValue(String express) throws HDException {
		String funcStr = express.substring(express.indexOf(FUNCTION_PRE)+2, express.length());
		String funcname = funcStr.substring(0, funcStr.indexOf("("));
		
		List<String> params = new ArrayList<>();
//		Pattern p = Pattern.compile("(?<=')[^']*(?=')");  
		Matcher m = p.matcher(funcStr);  
		while(m.find()){  
			String param = m.group().trim();
			if(!",".equals(param)) {
				params.add(param);
			}
		}
		
		IVarFunction varFunction = VarFunctionFactory.getInstance().getVarFunction(funcname);
		return varFunction.getStringValue(params);
	}
	
	
	/**
	 * 替换字段变量
	 * @param str
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月12日
	 */
	public static String replaceFormItemParam(String str,AbstractVO vo) {
		if(vo==null)
			return str;
		
		if(!hasFitemParam(str)) 
			return str;
		
		StringBuilder result = new StringBuilder();		
		String[] arr = str.split(SyConstant.SY_FITEM_SEPARATOR);
		for(int i=0;i<arr.length;i++) {
			if(i%2==0) {
				result.append(arr[i]);
			}else {		
				Object value = vo.get(arr[i]);
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
	 * 替换字段变量  data类型翻译为指定字符串
	 * @param str
	 * @param dateFormatStr 日期格式化字符串默认 yyyy-MM-dd HH:mm:ss
	 * @return
	 * @author guozhiheng
	 * @date 2020年9月24日
	 */
	public static String replaceFormItemParam(String str,AbstractVO vo,String dateFormatStr) {
		if(vo==null)
			return str;

		if(!hasFitemParam(str))
			return str;
		if(dateFormatStr==null ||dateFormatStr.equals("")){
			dateFormatStr =  SyConstant.DATE_TIME_PATTERN;
		}

		StringBuilder result = new StringBuilder();
		String[] arr = str.split(SyConstant.SY_FITEM_SEPARATOR);
		for(int i=0;i<arr.length;i++) {
			if(i%2==0) {
				result.append(arr[i]);
			}else {
				Object value = vo.get(arr[i]);
				if(value==null) {
					result.append("");
				}else if(value instanceof Date) {
					String dateStr = DateFormatUtils.format((Date)value,dateFormatStr);
					result.append(dateStr);
				}else {
					result.append(value);
				}

			}
		}
		return result.toString();
	}
	
	/**
	 * 更新字段变量修改vo中对应字段(a=1,b=2)
	 * @param str
	 * @return 
	 * @author liyan
	 * @throws HDException 
	 * @date 2018年3月12日
	 */
	public static void updateVoByFormItemParam(String updateItem,AbstractVO vo) throws HDException {
		if(StringUtils.isEmpty(updateItem)){
			return;
		}
		updateItem = replaceSystemParam(updateItem);
		updateItem = replaceFormItemParam(updateItem, vo);
		String[] arrAll = updateItem.split(",");
		for(int i=0;i<arrAll.length;i++) {
			String[] arrItem = arrAll[i].split("=");//a=1
			if(arrItem.length == 1){
				vo.set(arrItem[0], null);
			}else{
				vo.set(arrItem[0], arrItem[1]);	
			}
		}
	}
	
	/**
	 * 判定是否含有字段变量
	 * @param express
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月1日
	 */
	public static boolean hasFitemParam(String express) {
		if(StringUtils.isEmpty(express))
			return false;
		
		int count = StringUtils.countMatches(express, SyConstant.SY_FITEM_SEPARATOR);
		if(count>=2) 
			return true;
		return false;
	}
	
	/**
	 * 判定是否含有自定义字段变量
	 * @param express
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月1日
	 */
	public static boolean hasCustomFitemParam(String express) {
		if(StringUtils.isEmpty(express))
			return false;
		
		int count = StringUtils.countMatches(express, SyConstant.SY_CUSTOM_SEPARATOR);
		if(count>=2) 
			return true;
		return false;
	}
	
	/**
	 * 判定是否含有系统变量
	 * @param express
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月1日
	 */
	public static boolean hasSysParam(String express) {
		if(StringUtils.isEmpty(express))
			return false;
		
		int count = StringUtils.countMatches(express, SyConstant.SY_VARIABLE_SEPARATOR);
		if(count>=2) 
			return true;
		return false;
	}
	
	/**
	 * 替换系统、字段参数，同时计算表达式的boolean型结果
	 * @param express
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年7月1日
	 */
	public static boolean replaceAllParamAndEval(String express,AbstractVO vo) throws ScriptException {
		try {
			express = replaceSystemParam(express);
		} catch (HDException e) {
			throw new HDRuntimeException(e);
		}
		express = replaceFormItemParam(express, vo);
		return JavaScriptExpressUtils.eval(express);
	}
}
