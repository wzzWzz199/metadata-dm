package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.attach.entity.AttachDfsShowVO;
import com.hayden.hap.common.attach.itf.IAttachService;
import com.hayden.hap.common.attach.utils.AttachUtils;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.form.entity.FormItemMVO;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.ListUtil;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VariableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表单验证工具类
 * 
 * @author zhangfeng
 * @date 2016年4月8日
 * @since 2.0.1
 */
public class ValidateUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ValidateUtils.class);

	/**
	 * 日期格式常量
	 */
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final Pattern PHONE_NUMBER_PATTERN = Pattern
			.compile("^1([38][0-9]|4[579]|5[^4]|66|7[0135678]|9[89])[0-9]{8}$");

	/**
	 * 表单字段校验
	 * 
	 * @param vo
	 * @param formItemVOs
	 * @param tenantid
	 * @return
	 * @throws HDException
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	public static List<Message> validate(AbstractVO vo, List<? extends FormItemVO> formItemVOs, Long tenantid)
			throws HDException {
		List<Message> result = new ArrayList<>();

		for (FormItemVO formItemVO : formItemVOs) {

			Message message = validate(vo, formItemVO);
			if (message != null) {
				result.add(message);
			}
		}

		String uniquerMessage = validateUnique(vo, formItemVOs, tenantid);
		if (uniquerMessage != null) {
			result.add(new Message(uniquerMessage, MessageLevel.ERROR));
		}

		return result;
	}

	/**
	 * 批量表单字段校验
	 * 
	 * @param voList
	 * @param formItemVOs
	 * @param tenantid
	 * @return
	 * @throws HDException
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	public static List<Message> validate(List<? extends AbstractVO> voList, List<? extends FormItemVO> formItemVOs,
			Long tenantid) throws HDException {
		List<Message> result = new ArrayList<>();

		for (AbstractVO vo : voList) {
			for (FormItemVO formItemVO : formItemVOs) {
				Message message = validate(vo, formItemVO);
				if (message != null) {
					result.add(message);
				}
			}
		}

		return result;
	}

	/**
	 * 批量审批流元素属性表单字段校验
	 * 
	 * @param voList
	 * @param formItemVOs
	 * @param tenantid
	 * @return
	 * @throws HDException
	 * @author liyan
	 * @date 2018年5月4日
	 */
	public static List<Message> validateWfFormItems(List<? extends AbstractVO> voList,
			List<? extends FormItemVO> formItemVOs, Long tenantid) throws HDException {
		List<Message> result = new ArrayList<>();

		for (AbstractVO vo : voList) {
			for (FormItemVO formItemVO : formItemVOs) {
				Message message = validate(vo, formItemVO);
				if (message != null) {
					message.setMessage("【" + vo.getString("element_name") + "】" + message.getMessage());
					result.add(message);
				}
			}
		}

		return result;
	}

	/**
	 * 一致性校验
	 * 
	 * @param vo
	 * @throws HDException
	 *             如果有并发操作，则抛出海顿异常
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	public static void validateConsistency(AbstractVO vo) throws HDException {
		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class);
		boolean consistencyResult = baseService.validateConsistency(vo);
		if (!consistencyResult) {
			throw new HDException("数据状态已变更，请刷新");
		}
	}

	/**
	 * 检验附件字段非空,只检验未配置共享的，如果配置了共享池， 则可以配置附件源功能的附件字段为必填，如果是单向共享，
	 * 则值检验当前功能的附件，可以在所有共享功能的附件字段配置必填。
	 * 
	 * @param formParamVO
	 * @param vo
	 * @param formItemVOs
	 * @param pkColName
	 * @param tenantid
	 * @return
	 * @author liyan
	 * @throws HDException
	 * @date 2017年9月18日
	 */
	public static List<Message> validateAttachNotNull(FormParamVO formParamVO, AbstractVO vo,
			List<? extends FormItemVO> formItemVOs, String pkColName, Long tenantid) throws HDException {

		List<Message> result = new ArrayList<>();
		Boolean needValidate = false;
		String moduleCode = formParamVO.getModuleCode();
		String funcCode = formParamVO.getFuncCode();
		IFuncService funcService = (IFuncService) AppServiceHelper.findBean("funcService");
		funcCode = funcService.getSourcePCFunccode(funcCode, tenantid);
		List<String> biz_created_list = new ArrayList<>();
		List<String> colcode_list = new ArrayList<>();
		List<String> colname_list = new ArrayList<>();
		Map<String,String> code2nameMap = new HashMap<>();
		List<String> attach_editid = formParamVO.getReqParamVO().getAttach_editid();
		Long pkValue = vo.getLong(pkColName);
		Map<String,String> fastDfsCode2name = new HashMap<>();
		for (FormItemVO formItemVO : formItemVOs) {
			if (formItemVO.getFitem_input_element().equals(ElementTypeEnum.FILE.getCode())
					|| formItemVO.getFitem_input_element().equals(ElementTypeEnum.IMG.getCode())) {
				if (ObjectUtil.isTrue(formItemVO.getFitem_notnull())) {
					String colCode = formItemVO.getFitem_code();
					String colName = formItemVO.getFitem_name();
					// 判断有无主键，没有则为新增，附件ownerid为attach_editid，虽时数组，但所有附件字段都用一个值，所以取第一个即可
					if (!ObjectUtil.isNotNull(pkValue)) {
						if (ObjectUtil.isNotNull(attach_editid) && ObjectUtil.isNotNull(attach_editid.get(0))) {
							pkValue = Long.parseLong(attach_editid.get(0));
						} else {
							result.add(new Message(colName + ": 不能为空", MessageLevel.ERROR));
							continue;
						}
					}
					needValidate = true;
					colcode_list.add(colCode);
					colname_list.add(colName);
					code2nameMap.put(colCode, colName);

					Object created_dt_Obj = vo.get("created_dt");
					if (created_dt_Obj != null) {
						SimpleDateFormat sdf = new SimpleDateFormat(SyConstant.DATE_TIME_PATTERN);
						if (created_dt_Obj instanceof Date) {
							biz_created_list.add(sdf.format((Date) created_dt_Obj));
						} else if (created_dt_Obj instanceof Long) {
							biz_created_list.add(sdf.format(new Date((Long) created_dt_Obj)));
						} else {
							try {
								Long t = Long.parseLong(vo.get("created_dt").toString());
								biz_created_list.add(sdf.format(new Date(t)));
							} catch (Exception e) {
								biz_created_list.add(vo.get("created_dt").toString());
							}
						}
					}
				}
			}
			if (ElementTypeEnum.FAST_FILE.getCode().equals(formItemVO.getFitem_input_element()) ||
					  ElementTypeEnum.FAST_IMG.getCode().equals(formItemVO.getFitem_input_element())) {
				if (ObjectUtil.isTrue(formItemVO.getFitem_notnull())) {
					fastDfsCode2name.put(formItemVO.getFitem_code(),formItemVO.getFitem_name());
				}
			}
		}
		if (needValidate) {
			if(vo.getLong(pkColName)==null) {//新增时候读库
				List<Message> returnResult = AttachUtils.validateAttach(tenantid, moduleCode, funcCode, colcode_list,
						colname_list, pkValue, biz_created_list);
				if (ObjectUtil.isNotEmpty(returnResult)) {
					result.addAll(returnResult);//
				}
			}else {//修改时候根据数量查
				//得到卡片页面的附件字段的附件数量	
				IAttachService attachService = AppServiceHelper.findBean(IAttachService.class);
				attachService.getCardAttachNum(funcCode, formParamVO.getModuleCode(), vo, tenantid);
				
				for(Entry<String, String> entry : code2nameMap.entrySet()) {
					String code = entry.getKey();
					String name = entry.getValue();
					Object value = vo.get(code);
					if(value==null) {
						Message msg = new Message(name+": 不能为空", MessageLevel.ERROR);
						result.add(msg);
					}else if(value instanceof Integer && (Integer)value<=0) {
						Message msg = new Message(name+": 不能为空", MessageLevel.ERROR);
						result.add(msg);
					}
				}
			}
		
		}
		if (fastDfsCode2name.size()>0) {
			for(Entry<String, String> entry : fastDfsCode2name.entrySet()) {
				String fastDfs = vo.getString(entry.getKey());
				if (StringUtils.isEmpty(fastDfs)) {
					Message msg = new Message(entry.getValue()+": 不能为空", MessageLevel.ERROR);
					result.add(msg);
				} else{
					List<AttachDfsShowVO> attachDfsList =null;
					attachDfsList = JsonUtils.parseArray(fastDfs, AttachDfsShowVO.class);

					if(attachDfsList==null || attachDfsList.isEmpty()) {
						Message msg = new Message(entry.getValue()+": 不能为空", MessageLevel.ERROR);
						result.add(msg);
					}
				}

			}
		}
		return result;
	}

	/**
	 * 验证提交的VO对象<br/>
	 * 验证非空、长度、正则验证、取值范围
	 * 
	 * @param vo
	 *            需要进行验证的vo对象
	 * @param formItemVO
	 *            表单字段
	 * @return 如果验证通过返回空，若没通过，返回包含错误信息的消息结果对象
	 * @throws HDException
	 * @author zhangfeng
	 * @date 2016年4月8日
	 */
	private static Message validate(AbstractVO vo, FormItemVO formItemVO) throws HDException {
		String validateResult = null;
		Object value = vo.get(formItemVO.getFitem_code());

		/* 首先判断是否有值，若没值则根据字段配置进行非空验证 */
		if ((value == null || (value instanceof String && value.equals("")))) {
			// 附件类型有单独检验
			if (formItemVO.getFitem_input_element().equals(ElementTypeEnum.FILE.getCode())
					|| formItemVO.getFitem_input_element().equals(ElementTypeEnum.IMG.getCode())
					|| formItemVO.getFitem_input_element().equals(ElementTypeEnum.FAST_FILE.getCode())
					|| formItemVO.getFitem_input_element().equals(ElementTypeEnum.FAST_IMG.getCode())) {
				return null;
			}

			validateResult = validateNotNull(value, formItemVO);
			if (validateResult != null) {// 如果验证没通过，则记录错误信息，返回；下边的验证结果同样的处理
				return new Message(validateResult, MessageLevel.ERROR);
			}
		} else {
			/* 有值，进行长度验证 */
			validateResult = validateLength(value, formItemVO);
			if (validateResult != null) {
				return new Message(validateResult, MessageLevel.ERROR);
			}
			/* 正则表达式验证其正确性 */
			validateResult = validateRegex(value, formItemVO);
			if (validateResult != null) {
				return new Message(validateResult, MessageLevel.ERROR);
			}
			/* 验证取值范围 */
			validateResult = validateRange(value, formItemVO, vo);
			if (validateResult != null) {
				return new Message(validateResult, MessageLevel.ERROR);
			}
		}
		return null;
	}

	private static String validateNotNull(Object value, FormItemVO formItemVO) {
		if (ObjectUtil.isTrue(formItemVO.getFitem_notnull())) {
			return formItemVO.getFitem_name() + ": 不能为空";
		}
		return null;
	}

	private static String validateLength(Object value, FormItemVO formItemVO) {
		if (value instanceof Date) {// 日期不验证长度
			return null;
		}
		if ( formItemVO.getFitem_input_element().equals(ElementTypeEnum.FAST_FILE.getCode())
				|| formItemVO.getFitem_input_element().equals(ElementTypeEnum.FAST_IMG.getCode())||formItemVO.getFitem_input_element().equals(ElementTypeEnum.DATE.getCode())) {
			//fastdfs附件存储为数组类型，不是真正的附件key 不校验长度
			return null;
		}
		int length = value.toString().length();
		if (length > formItemVO.getFitem_length()) {
			return formItemVO.getFitem_name() + ": 只允许输入" + formItemVO.getFitem_length() + "个字符";
		}

		return null;
	}

	/**
	 * 校验取值范围
	 * 
	 * @return
	 * @author zhangfeng
	 * @throws HDException
	 * @date 2016年4月8日
	 */
	private static String validateRange(Object value, FormItemVO formItemVO, AbstractVO vo) throws HDException {
		if (StringUtils.isNotEmpty(formItemVO.getFitem_value_scope())) {
			String express = VariableUtils.replaceSystemParam(formItemVO.getFitem_value_scope());
			express = VariableUtils.replaceFormItemParam(express, vo);

			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			if (value instanceof Date) {
				engine.put("n", DateFormatUtils.format((Date) value, DATE_TIME_PATTERN));
			} else {
				engine.put("n", value);
			}

			Object result;
			try {
				result = engine.eval(express);
			} catch (ScriptException e) {
				logger.error(e.getMessage(), e);
				throw new HDException(e);
			}
			if (!Boolean.valueOf(result.toString())) {
				if (StringUtils.isNotEmpty(formItemVO.getFitem_value_scope_msg())) {
					return formItemVO.getFitem_name() + ":" + formItemVO.getFitem_value_scope_msg();
				}
				return formItemVO.getFitem_name() + ": 取值范围不正确,需匹配" + formItemVO.getFitem_value_scope();
			}
		}
		return null;
	}

	/**
	 * 正则校验
	 * 
	 * @param value
	 * @param formItemVO
	 * @return
	 * @author zhangfeng
	 * @date 2016年4月8日
	 */
	private static String validateRegex(Object value, FormItemVO formItemVO) {
		if ("password".equals(formItemVO.getFitem_code())) {// 不校验密码的正则
			return null;
		}
		if (StringUtils.isNotEmpty(formItemVO.getFitem_value_regexp())) {
			Pattern pattern = Pattern.compile(formItemVO.getFitem_value_regexp());
			Matcher matcher = pattern.matcher(value.toString());
			if (!matcher.matches()) {
				if (StringUtils.isNotEmpty(formItemVO.getFitem_value_regexp_msg())) {
					return formItemVO.getFitem_name() + ":" + formItemVO.getFitem_value_regexp_msg();
				}
				return formItemVO.getFitem_name() + ": 格式不正确,需匹配" + formItemVO.getFitem_value_regexp();
			}
		}
		return null;
	}

	private static String validateUnique(AbstractVO vo, List<? extends FormItemVO> formItemVOs, Long tenantid) {
		Map<String, List<FormItemVO>> uniqueGroupMap = new HashMap<>();
		for (FormItemVO formItemVO : formItemVOs) {
			if (StringUtils.isNotEmpty(formItemVO.getFitem_unique_group())) {
				List<FormItemVO> samegroupItems = uniqueGroupMap.get(formItemVO.getFitem_unique_group());
				if (samegroupItems == null) {
					samegroupItems = new ArrayList<>();
					uniqueGroupMap.put(formItemVO.getFitem_unique_group(), samegroupItems);
				}
				samegroupItems.add(formItemVO);
			}
		}

		if (uniqueGroupMap.size() > 0) {
			Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
			return validateUnique(vo, uniqueGroupMap, currentDataTenantid);
		}

		return null;
	}

	private static String validateUnique(AbstractVO vo, Map<String, List<FormItemVO>> uniqueGroupMap, Long tenantid) {
		ITableDefService tableDefService = AppServiceHelper.findBean(ITableDefService.class);
		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class);
		Long pk = (Long) baseService.getVOPkColValue(vo);
		String pkColumn = tableDefService.getPkColName(vo.getTableName());

		for (List<FormItemVO> formItemVOs : uniqueGroupMap.values()) {

			// 然后再去数据库查重复
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			StringBuilder sqlBuilder = new StringBuilder();

			// 是否需要验证的标记
			boolean needValidate = false;
			for (FormItemVO formItemVO : formItemVOs) {
				String fitem_code = formItemVO.getFitem_code();
				Object value = vo.get(fitem_code);
				if (value == null || "".equals(value.toString()))// 当没值的时候不验证重复
					continue;

				sqlBuilder.append(" and ");
				sqlBuilder.append(fitem_code);
				sqlBuilder.append(" = ");
				if (!DataTypeEnum.NUMBER.getCode().equals(formItemVO.getFitem_data_type())) {
					sqlBuilder.append("'");
				}
				sqlBuilder.append(value.toString());
				if (!DataTypeEnum.NUMBER.getCode().equals(formItemVO.getFitem_data_type())) {
					sqlBuilder.append("' ");
				}

				if (ObjectUtil.isTrue(formItemVO.getFitem_unique_info())) {
					needValidate = true;
				}
			}

			if (!needValidate) {
				continue;
			}

			if (pk != null) {// 修改的验证重复逻辑
				sqlBuilder.append(" and ");
				sqlBuilder.append(pkColumn);
				sqlBuilder.append(" != ");
				sqlBuilder.append(pk);
			}

			if (sqlBuilder.length() > 0) {
				sqlBuilder.delete(0, 4);// 去掉第一个and
			}

			dynaSqlVO.addWhereClause(sqlBuilder.toString());

			if (!SyConstant.NONTENANTID_TABLE.containsKey(vo.getTableName())) {
				dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
			}

			int count = baseService.getCount(vo.getTableName(), dynaSqlVO);

			if (count > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (FormItemVO formItemVO : formItemVOs) {
					if (!ObjectUtil.isTrue(formItemVO.getFitem_unique_info()))
						continue;
					sb.append(formItemVO.getFitem_name());
					sb.append(" ,");
				}
				if (sb.length() > 1) {
					sb.delete(sb.length() - 2, sb.length());
				}
				sb.append("]");
				sb.append("不能重复");
				return sb.toString();
			}

		}
		return null;
	}

	/**
	 * 校验查询非空
	 * 
	 * @param itemVOs
	 * @param queryMap
	 * @return
	 * @author zhangfeng
	 * @date 2017年6月15日
	 */
	public static List<Message> validateQueryNotnull(List<? extends FormItemVO> itemVOs, Map<String, Object> queryMap) {
		if (ObjectUtil.isEmpty(itemVOs))
			return null;

		if (itemVOs.get(0) instanceof FormItemMVO)
			return null;

		List<Message> result = new ArrayList<>();
		for (FormItemVO itemVO : itemVOs) {
			FormItemPCVO pcItem = (FormItemPCVO) itemVO;
			Object value = queryMap.get(itemVO.getFitem_code());

			if (ObjectUtil.isTrue(pcItem.getFitem_com_query()) && ObjectUtil.isTrue(pcItem.getFitem_query_notnull())) {// 校验非空
				if (value == null || "".equals(value)) {
					Message message = new Message("[" + itemVO.getFitem_name() + "]必填", MessageLevel.ERROR);
					result.add(message);
				}
			}

			if (DataTypeEnum.NUMBER.getCode().equals(itemVO.getFitem_data_type())) {// 校验数字
				boolean isNumber = isNumber(value);
				if (!isNumber) {
					String errorMsg = MessageFormat.format("[{0}]输入不正确，应该为数字类型", itemVO.getFitem_name());
					Message message = new Message(errorMsg, MessageLevel.ERROR);
					result.add(message);
				}
			}
		}
		return result;
	}

	private static final Pattern numberPattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
	/**
	 * 是否数字
	 * 
	 * @param test
	 * @return
	 * @author zhangfeng
	 * @date 2017年7月20日
	 */
	public static boolean isNumber(Object test) {
		if (test == null||"".equals(test)) {
			return true;
		}
//		Pattern numberPattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
		Matcher matcher = numberPattern.matcher(test.toString());
		return matcher.matches();
	}

	/**
	 * 是否手机号
	 * 
	 * @param phoneNumber
	 * @return
	 * @author zhangfeng
	 * @date 2018年4月2日
	 */
	public static boolean isPhoneNumber(String phoneNumber) {
		if (StringUtils.isEmpty(phoneNumber))
			return false;
		Matcher m = PHONE_NUMBER_PATTERN.matcher(phoneNumber);
		if (!m.matches()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 本地校验数据重复
	 * @param voList
	 * @param fitems
	 * @param infoFunc 提示信息标记函数
	 * @return 
	 * @author zhangfeng
	 * @date 2018年11月7日
	 */
	public static <T extends AbstractVO> List<Message> validateLocalUnique(List<T> voList, 
			List<? extends FormItemVO> fitems, 
			Function<T,String> infoFunc) {
		
		if(ObjectUtil.isEmpty(fitems) || ObjectUtil.isEmpty(voList))
			return null;
		
		Map<String, List<FormItemVO>> uniqueGroupMap = new HashMap<>();
		for (FormItemVO formItemVO : fitems) {
			if (StringUtils.isNotEmpty(formItemVO.getFitem_unique_group())) {
				List<FormItemVO> samegroupItems = uniqueGroupMap.get(formItemVO.getFitem_unique_group());
				if (samegroupItems == null) {
					samegroupItems = new ArrayList<>();
					uniqueGroupMap.put(formItemVO.getFitem_unique_group(), samegroupItems);
				}
				samegroupItems.add(formItemVO);
			}
		}

		List<Message> result = new ArrayList<>();
		
		if (uniqueGroupMap.size() > 0) {
			for(Entry<String, List<FormItemVO>> entry : uniqueGroupMap.entrySet()) {
				List<FormItemVO> items = entry.getValue();
				if(items.size()>1) {
					//TODO... 多字段在同一个唯一组的验证
				}else {
					FormItemVO item = items.get(0);
					
					Map<Object, List<T>> groupMap = ListUtil.group(voList, x->x.get(items.get(0).getFitem_code()));
					groupMap.remove(null);
					StringBuilder errbBuilder = new StringBuilder();
					errbBuilder.append(item.getFitem_name());
					errbBuilder.append("存在重复:");
					boolean hasDouble = false;
					for(Entry<Object, List<T>> groupEntry : groupMap.entrySet()) {
						List<T> groupList = groupEntry.getValue();
						if(groupList.size()>1) {
							hasDouble = true;
							errbBuilder.append("[");
							for(T t : groupList) {
								errbBuilder.append(infoFunc.apply(t));
								errbBuilder.append(",");
							}
							errbBuilder.append("],");
						}
					}
					
					if(hasDouble) {
						Message message = new Message(errbBuilder.toString(), MessageLevel.ERROR);
						result.add(message);
					}
				}
			}
		}
		return result;
	}

	public static void main(String[] args) throws ScriptException {
		// ScriptEngineManager factory = new ScriptEngineManager();
		// ScriptEngine engine = factory.getEngineByName("JavaScript");
		// engine.put("n", DateFormatUtils.format(new Date(), DATE_TIME_PATTERN));
		//// Object result = engine.eval("n+1!=2 && n==2 && 1==2");
		// Object result = engine.eval("n > '2016-01-03'");
		// System.out.println(result);
		//
		// String reg = "^\\d{4}-[0-1]\\d-[0-3]\\d [0-2][0-4]:[0-6]\\d:[0-6]\\d$";
		// String s = "2016-01-01 15:00:59";
		// Pattern pattern = Pattern.compile(reg);
		// Matcher matcher = pattern.matcher(s);
		// System.err.println(matcher.matches());
		//
		// String integerReg = "^[1-9]\\d*$";
		// String a = "12300";
		// Pattern pattern2 = Pattern.compile(integerReg);
		// Matcher matcher2 = pattern2.matcher(a);
		// System.err.println(matcher2.matches());
		//
		// StringBuilder sb = new StringBuilder(" or123abc或者");
		//// o.delete(0, 3);
		// sb.delete(sb.length()-2, sb.length());
		// System.err.println(sb.toString());

//		String numbReg = "^(\\-?)[0-9]{0,5}+(\\.[0-9]{0})?$";
		String dateReg = "^((([0-9]{2})(0[48]|[2468][048]|[13579][26]))"
				+ "|((0[48]|[2468][048]|[13579][26])00)-02-29)"
				+ "|([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
		System.out.println(dateReg.length());
		String s = "2010";
		Pattern pattern2 = Pattern.compile(dateReg);
		Matcher matcher2 = pattern2.matcher(s);
		System.err.println(matcher2.matches());

		// Scanner sc = new Scanner(System.in);
		// String s = sc.next();
		// sc.close();
		//// String[] arr = s.split(",");
		//// String source = arr[0];
		//// String scale = arr[1];
		//// String reg = "^[0-9]+(\\.[0-9]{1,"+scale+"})?$";
		// String reg =
		// "^([0-9a-zA-Z]+[0-9a-zA-Z_\\-]*|(([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})))$";
		//// String s = "0102";
		// Pattern pattern = Pattern.compile(reg);
		// Matcher matcher = pattern.matcher(s);
		// System.err.println(matcher.matches());

		// ^([0-9a-zA-Z]+[0-9a-zA-Z_\-]*|(([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})))$
	}

}
