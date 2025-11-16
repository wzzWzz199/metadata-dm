package com.hayden.hap.common.cache.constant;

/**
 * 
 * @author zhangfeng
 * @date 2018年6月28日
 */
public class CacheConstant {

	//缓存名字
	public static final String CACHE_FORM = "SY_FORM";	
	public static final String CACHE_FORM_ITEM = "SY_FORM_ITEM";
	public static final String CACHE_FORM_BUTTON = "SY_FORM_BUTTON";//根据表单编码缓存表单按钮
	public static final String CACHE_FUNC = "SY_FUNC";
	public static final String CACHE_FUNC_LINK = "SY_FUNC_LINK";
	public static final String CACHE_FORM_MOBILE = "SY_FORM_MOBILE";	
	public static final String CACHE_FORM_ITEM_MOBILE = "SY_FORM_ITEM_MOBILE";
	public static final String CACHE_FORM_BUTTON_MOBILE = "SY_FORM_BUTTON_MOBILE";//根据表单编码缓存表单按钮
	public static final String CACHE_FUNC_MOBILE = "SY_FUNC_MOBILE";
	public static final String CACHE_FUNC_LINK_MOBILE = "SY_FUNC_LINK_MOBILE";	
	public static final String CACHE_FUNC_LINK_DATA = "SY_FUNC_LINK_DATA";//根据功能编码缓存关联数据
	public static final String CACHE_CONFIG = "SY_CONFIG";
	public static final String CACHE_DICT_DATA = "SY_DICT_DATA";
	public static final String CACHE_DICT = "SY_DICT";
	public static final String CACHE_DICTTABLE_2_CODE = "OUT_DICT_TABLE2CODE_BY_TENANTID";
	public static final String CACHE_FORM_QUERY = "SY_FORM_QUERY";	
	public static final String CACHE_NO_VALIDATE_FUNC = "noValidateFuncCache";	
	public static final String CACHE_URL_FUNC_AND_BTN = "urlFuncAndBtnCache";
	public static final String CACHE_ATTACH_DATA_BYPK = "SY_ATTACH_DATA_BYPK";//根据主键，租户id走的附件缓存
	public static final String CACHE_ATTACH_DATA_BYFUNC = "SY_ATTACH_DATA_BYFUNC";//根据功能编码和字段编码走的附件缓存
	public static final String CACHE_ATTACH_PATH = "SY_ATTACH_PATH";
	public static final String CACHE_APP_VERSION = "SY_APP_VERSION";
	public static final String CACHE_ORG_PERMISSION = "SY_ORG_PERMISSION";//部门权限缓存
	public static final String CACHE_ORG_PERMISSION_TABLE_FUNC = "SY_ORG_PERMISSION_TABLE_FUNC";//部门权限：表-功能树缓存
	public static final String CACHE_EXPORT_TEMPLATE = "SY_EXPORT_TEMPLATE";//导出模板缓存 
	public static final String CACHE_WF_DEF_BYFUNC = "WF_DEF_BYFUNC";//审批流按func缓存 
	public static final String CACHE_WF_DEF_BYID = "WF_DEF_BYID";//审批流按id缓存 
	public static final String CACHE_WF_HIPROC_INST = "WF_HIPROC_INST";//审批流实例按id和tenantid缓存 
	public static final String CACHE_WF_HIPROC_RECORD_BYPROCINSTID = "WF_HIPROC_RECORD_BYPROCINSTID";//审批流实审批日志 
	public static final String CACHE_PHONE_LOCALTION = "SY_PHONE_LOCALTION";//手机号归属地缓存
	public static final String CACHE_SMSAPP_BYCODE = "SY_SMSAPP_BYCODE";//应用授权管理按code查的缓存
	public static final String CACHE_SMSCONFIG_BYCODE = "SY_SMSCONFIG_BYCODE";//短信平台配置按code查缓存
	public static final String CACHE_SMSTEMPLATE_BYCODE = "SY_SMSTEMPLATE_BYCODE";//短信模板按code查缓存
	public static final String CACHE_APP_CONFIG = "SY_APP_CONFIG";//租户应用授权管理按app_id和租户id查缓存
	public static final String CACHE_USER = "tenantUserCache";//人员缓存，key为租户id，value是该租户的所有人员

	public static final String CACHE_PUB_SHIFT = "PUB_SHIFT";//班次缓存，key为租户id，value是该租户的所有班次
	public static final String CACHE_PUB_SCHEDULE_STRATEGY = "PUB_SCHEDULE_STRATEGY";//排班策略缓存，key为租户id，value是该租户的所有排班策略
}
