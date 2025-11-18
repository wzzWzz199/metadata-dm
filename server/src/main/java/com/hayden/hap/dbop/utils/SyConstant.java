package com.hayden.hap.dbop.utils;

import java.util.*;

/**
 * 全局的常量定义
 * @author zhangfeng
 * @date 2015年9月16日
 */
public class SyConstant {

	/**
	 * 常量：是
	 */
	public static final int SY_TRUE = 1;
	
	/**
	 * 常量：否
	 */
	public static final int SY_FALSE = 0;
	
	
	/**
	 * 海顿租户id
	 */
	public static final Long TENANT_HD = 1L;
	
	/**
	 * 海顿租户code
	 */
	public static final String TENANT_HD_CODE = "hd";
	
	/*系统管理员*/
	public static final Long USER_HD_ADMIN_SUPER = 1L;
	/*系统管理员*/
	public static final Long USER_HD_ADMIN_SY =100L;
	//sy系统管理员编码及名称
	public static final String USER_HD_ADMIN_SY_CODE = "sy";
	public static final String USER_HD_ADMIN_SY_NAME = "系统管理员";
	
	/**
	 * 租户
	 */
	public static final String TENANT_STR = "tenantid";
	
	/**
	 * 租户编码
	 */
	public static final String TENANTCODE_STR = "tenantCode";
	/**
	 * 用户
	 */
	public static final String USERID_STR = "userId";
	
	/**
	 * 用户名称.
	 */
	public static final String USERNAME_STR = "userName";
	/**
	 * 用户编码.
	 */
	public static final String USERCODE_STR = "userCode";
	
	/**
	 * 微信昵称
	 */
	public static final String USER_WEIXIN_NICK = "nickname";
	/**
	 * 用户所属组织id
	 */
	public static final String ORGID = "orgid";
	/**
	 * 用户所属组织名称
	 */
	public static final String ORGNAME = "orgName";

	/**
	 * 系统管理员角色编码
	 */
	public static final String SY_ADMIN_ROLE_CODE="1001"; 
	
	/**
	 * 用户组织编码
	 */
	public static final String ORGCODE="orgCode";
	/*app版本*/
	public static final String APPVERSION="appVersion";
	/*应用所属模块*/
	public static final String APP_MOUDLE_CODE="appMoudleCode";

	/**
	 * NONTENANTID_TABLE:(没有租户id的表).
	 */
	public static final Map<String,String> NONTENANTID_TABLE = new HashMap<String,String>();
	static{
		NONTENANTID_TABLE.put("sy_table_def", "sy_table_def");
		NONTENANTID_TABLE.put("sy_table_column", "sy_table_column");
		NONTENANTID_TABLE.put("sy_serial_generator", "sy_serial_generator");
		NONTENANTID_TABLE.put("sy_tenant", "sy_tenant");
		NONTENANTID_TABLE.put("mgr_id_mapper", "mgr_id_mapper");
		NONTENANTID_TABLE.put("sy_phone_location", "sy_phone_location");
		NONTENANTID_TABLE.put("sy_sms_config", "sy_sms_config");
		NONTENANTID_TABLE.put("sy_sms_app", "sy_sms_app");
		NONTENANTID_TABLE.put("sy_sms_template", "sy_sms_template");
		NONTENANTID_TABLE.put("uc_cuser", "uc_cuser");
	}
	
	/**
	 * NONSQLFILTER_TABLE:(日志过滤时没有租户id的表).
	 */
	public static final List<String> NONSQLFILTER_TABLE = new ArrayList<String>();
	static{
		NONSQLFILTER_TABLE.add("sy_table_def");
		NONSQLFILTER_TABLE.add("sy_table_column");
		NONSQLFILTER_TABLE.add("sy_serial_generator");
		NONSQLFILTER_TABLE.add("sy_tenant");
		NONSQLFILTER_TABLE.add("sy_ipfirewall");
		NONSQLFILTER_TABLE.add("uc_cuser");
	}
	
	/**
	 * 上边没有租户id的表，所对应的功能编码
	 */
	public static final List<String> NONTENANTID_FUNC = new ArrayList<>();
	static {
		NONTENANTID_FUNC.add("SY_TENANT");
		NONTENANTID_FUNC.add("SY_TABLE_DEF");
		NONTENANTID_FUNC.add("SY_TABLE_COLUMN");
		NONTENANTID_FUNC.add("UC_CUSER");
	}
	
	/**
	 * 系统模块下不允许添加导出按钮的功能
	 */
	public static final List<String> NO_EXPORT_BUTTON = new ArrayList<>();
	static {
		NO_EXPORT_BUTTON.add("SY_FORM");
	}
	
	/**
	 * 附件导出图片类型,均为小写
	 */
	public static final List<String> EXPORT_TYPE_LIST = new ArrayList<>();
	static {
		EXPORT_TYPE_LIST.add(".jpg");
		EXPORT_TYPE_LIST.add(".png");
		EXPORT_TYPE_LIST.add(".jpeg");
		EXPORT_TYPE_LIST.add(".bmp");
	}
	
	
	
	
	/**
	 * DATA_STATS_DEFAULT:(标记数据的默认值，没有更多意义[select]).
	 */
	public static final Integer DATA_STATS_DEFAULT = 0;
	/**
	 * DATA_STATS_ADDED:(标记数据是新增未保存的[insert]).
	 */
	public static final Integer DATA_STATS_ADDED = 1;
	/**
	 * DATA_STATS_UPDATED:(标记数据是修改后未保存的[update]).
	 */
	public static final Integer DATA_STATS_UPDATED = 3;
	/**
	 * DATA_STATS_DELETED:(标记数据是要删除未保存的[delete]).
	 */
	public static final Integer DATA_STATS_DELETED = 5;
	
	/**
	 * ROOT_NODE_ID:树的根节点id
	 */
	public static final Long ROOT_NODE_ID = 0L;
	
	/**
	 * 内部字典数据表表名
	 */
	public static final String INNER_DICT_TABLE = "sy_dict_data";
	public static final String DICT_TYPE_1 = "1";//列表字典
	public static final String DICT_TYPE_2 = "2";//树形字典
	
	/**
	 * 缓存键：属性值和租户id的分隔符
	 */
	public static final String CACHE_SEPARATOR = "|";
	
	/**
	 * 查询策略功能字段名
	 */
	public static final String FORM_QUERY_FUNC_FIELD = "func_code";
	
	public static final String SPLIT_SEPARATOR = "@@";
	
	public static final String SPLIT_SEPARATOR_QR = "@#";
	
	/**
	 * 系统常量或变量分隔符
	 */
	public static final String SY_VARIABLE_SEPARATOR = "@";
	
	/**
	 * 字段变量分隔符
	 */
	public static final String SY_FITEM_SEPARATOR = "#";
	
	/**
	 * 特殊变量分隔符，目前用于审批人员扩展字段
	 */
	public static final String SY_CUSTOM_SEPARATOR = "%";
	
	/**
	 * 私钥modulus名称，常量
	 */
	public static final String PRIKEYSTRMOD="PriKeyStrModulus" ;
	
	/**
	 * 私钥Exponent名称，常量
	 */
	public static final String PRIKEYSTREXP="PriKeyStrExponent";
	
	/**
	 * stokenkey
	 */
	public static final String ST = "st";
	
	public static final String TID = "tid";
	
//	/**
//	 * 临时stokenkey
//	 */
//	public static final String STOKENKEYTEMP="stokenkeytemp";
	
	/**
	 * 注册用户stoken
	 */
	public static final String REGSTOKENKEY = "regStoken";
	
	/**
	 * 查询模板，"全部数据"模板的id
	 */
	public static final Long FORM_QUERY_ALL_ID = 0L;
	
	/**
	 * pc服务地址
	 */
	public static final String PCCONTEXT = "pcContext";
	
	/**
	 * ts字段名
	 */
	public static final String TS_COLUMN = "ts";
	
	/**
	 * 日期格式
	 */
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * sy模块
	 */
	public static final String SY = "sy";
	/**
	 * 逻辑删除
	 */
	public static final String DF = "df";
	
	/**
	 * mgr模块
	 */
	public static final String MGR = "mgr";
	
	/**
	 *多个集群
	 */
	public static final String SCHEDULE_MANY = "scheduler";
	
	/**
	 * 一个集群
	 */
	public static final String SCHEDULE_ONE = "all";
	
	/**
	 * 已有的表，部门字段不是orgid的，需要通过这个map来映射其关系，键为表编码、值为部门字段编码
	 */
	public static final Map<String,String> ORGPERMISSION_COL_MAP = new HashMap<>();
	static {
		ORGPERMISSION_COL_MAP.put("eam_budget", "budget_orgid");
		ORGPERMISSION_COL_MAP.put("phd_hidden_danger", "belong_orgid");
		ORGPERMISSION_COL_MAP.put("hse_work_task", "territorialunitid");
		ORGPERMISSION_COL_MAP.put("hse_work_ticket", "territorialunitid");
		ORGPERMISSION_COL_MAP.put("hse_safety_task", "territorialunitid");
		ORGPERMISSION_COL_MAP.put("hse_week_plan", "territorialunitid");
		ORGPERMISSION_COL_MAP.put("hse_safety_analysis", "territorialunitid");
		ORGPERMISSION_COL_MAP.put("hse_work_appoint", "territorialunitid");
		ORGPERMISSION_COL_MAP.put("hse_ticket_mbcd", "territorialunitid");
		
		ORGPERMISSION_COL_MAP.put("phd_hidden_project", "belong_orgid");
		ORGPERMISSION_COL_MAP.put("phd_hidden_exam", "belong_orgid");
		ORGPERMISSION_COL_MAP.put("phd_hidden_rewards", "belong_orgid");
		//添加巡检orgid映射 by gzh
		//巡检站点
		ORGPERMISSION_COL_MAP.put("csc_station", "org_id");
		//巡检测项
		ORGPERMISSION_COL_MAP.put("csc_check_item", "org_id");
		//巡检路线
		ORGPERMISSION_COL_MAP.put("csc_line", "org_id");
		//巡检人员
		ORGPERMISSION_COL_MAP.put("csc_person", "org_id");
		//排班设置
		ORGPERMISSION_COL_MAP.put("csc_schedule_rule", "org_id");
		//排班明细
		ORGPERMISSION_COL_MAP.put("csc_on_duty", "org_id");
		//巡检任务
		ORGPERMISSION_COL_MAP.put("csc_task", "org_id");
		//巡检异常
		ORGPERMISSION_COL_MAP.put("csc_fault", "org_id");		
	}
	public static Map<String,String> DATE2DATETIME = new HashMap<>();

	  static{
	    DATE2DATETIME.put("HSE_WORK_APPOINT","appointstarttime");
	    DATE2DATETIME.put("CBS_VIOLATIONPROBLEMRECORD","checkdate");
	    DATE2DATETIME.put("HSE_WORK_APPOINT_PROJECT","appointstarttime");
	    DATE2DATETIME.put("HSE_RAPNET_TASK","issuedate");
	    DATE2DATETIME.put("CBS_VIO_EXP","checkdate");


	  }
	
	/**
	 * mycat数据源名称：dataSource
	 */
	public static final String MYCAT_DATASOURCE = "dataSource";
	
	/**
	 * 部门id字段的字段编码：orgid
	 */
	public static final String ORG_ID_COL = "orgid";
	
	/**
	 * 附件相关常量
	 */
	public static final Long ATTACH_SIZE_LIMIT = 20971520l;//上传单个附件的最大size,默认20MB

	public static final Long THUMBNAIL_SIZE_LIMIT = 20480l;//上传图片的默认缩略图大小限制,20KB

	public static final String ATTACH_EMPTY_COLCODE = "AEColcode";//附件数据表，字段编码为空时候的填充字符串
	
	public static final String ATTACH_THUMBNAIL_STR = "_Thumbnail";//附件缩略图路径后缀
	
	public static final String ATTACH_PREVIEW_STR = "_Preview";//附件预览文件路径后缀
	/*试用登录获取短信验证码的应用编码*/
    public static final String SMS_APP_CODE="trail_login";
    
    /*试用登录获取短信验证码的短信模板编码*/
    public static final String SMS_TRAIL_LOGIN_SMSTEMPLATE_CODE="SMS_52805002";
    
    public static final String MSGPUSH_BYJIGUANG = "jiguang";//使用极光推送消息
    
    public static final String MSGPUSH_BYEMQ = "emq";//使用emq推送消息
	
    //消息推送时传递的参数，包括消息类型，包括通知和提醒，消息标题、消息内容
    public static final String MESSAGETYPE = "messageType";
    //消息类型包含两种，通知和提醒
    public static final String NOTICETYPE = "notice";
    public static final String REMINDTYPE = "remind";
    public static final String MESSAGETITLE = "messageTitle";
    public static final String MESSAGEBODY = "messageBody";
    
    //消息推送，传递触发消息推送的功能code和用户id
    public static final String MSG_SEND_USERID = "msge_send_userid";
    public static final String MSG_SEND_FUNC_CODE = "msg_send_func_code";
    //具体功能中，是哪个操作触发的。
    public static final String MSG_SEND_OPER_CODE = "msg_send_oper_code";
    public static final String MSG_SEND_MODULE_CODE = "msg_send_module_code";
    //消息接收功能编码
    public static final String MSG_RECEIVE_FUNC_CODE = "msg_receive_func_code";
    
    //定义字符串可用长度,包含中文是最大长度限制为2000，不含时为4000
    public static final Integer STRMAXLENG_CONTAINCHIN = 2000;
    public static final Integer STRMAXLENG_NOCONTAINCHIN = 4000;
    // 导出单次循环数
    public static final Integer MAX_NUMS = 500;
    // 超过此数启用异步导出 
    public static final Integer MAX_EXPORT_NUMS = 5000;
    //审批流相关变量
    public static final String WF_ACTION_PATH = "com.hayden.hap.wf.action.WfBaseAction";//审批流action父类路径
    
    //图形验证码stoken前缀
    public static final String CAPTCHA_PREFIX = "captcha_stoken_";
    
    //现场总管模块名，推送时使用
    public static final String APP_MODULE = "fv";
	
	// 过长的导入模板funcCode
	public static final List<String> TOO_LONG_EXCEL_FUNC_CODE = Arrays.asList(new String[]{"PHD_DP_RISK_CONTROL_MEASURE_LIST"});
    
}
