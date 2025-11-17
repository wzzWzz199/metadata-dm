package com.hayden.hap.common.utils;

/**
 * 
* Copyright: Copyright (c) 2019 北京海顿中科技术有限公司
* 
* @ClassName: KedacomPropertiesUtil.java
* @Description: 科达视频属性配置工具类
*
* @version: v3
* @author: 王振军
* @date: 2019年7月8日 下午2:56:35 
*
 */
public class KedacomPropertiesUtil {
	private static final String wenhao = "?";
	private static final String douhao = ",";
	private static final String aite = "@";
	
	private static final String ands = "&";

	
	/**
	 * 监控平台地址
	 */
	private static String addr;
	/**
	 * 用户名
	 */
	private static String mcuUesr;
	/**
	 * 密码
	 */
	private static String mcuPwd;
	/**
	 * 域id
	 */
	private static String domainid;
	/**
	 * 域名（在管理平台的平台信息中心可查看）
	 */
	private static String domainName;
	/**
	 * 信源号
	 */
	private static String src;
	/**
	 * 通道号
	 */
	private static String chan;
	/**
	 * 解码厂商
	 */
	private static String manu;
	/**
	 * 是否清晰，1-是
	 */
	private static final String clear = "1";
	/**
	 * 传输模式0:udp,1:tcp
	 */
	private static final String protocol= "0";
	
	
	public static String getAddr() {
		return addr;
	}
	public static void setAddr(String addr) {
		KedacomPropertiesUtil.addr = addr;
	}
	public static String getMcuUesr() {
		return mcuUesr;
	}
	public static void setMcuUesr(String mcuUesr) {
		KedacomPropertiesUtil.mcuUesr = mcuUesr;
	}
	public static String getMcuPwd() {
		return mcuPwd;
	}
	public static void setMcuPwd(String mcuPwd) {
		KedacomPropertiesUtil.mcuPwd = mcuPwd;
	}
	public static String getDomainid() {
		return domainid;
	}
	public static void setDomainid(String domainid) {
		KedacomPropertiesUtil.domainid = domainid;
	}
	public static String getDomainName() {
		return domainName;
	}
	public static void setDomainName(String domainName) {
		KedacomPropertiesUtil.domainName = domainName;
	}
	public static String getSrc() {
		return src;
	}
	public static void setSrc(String src) {
		KedacomPropertiesUtil.src = src;
	}
	public static String getChan() {
		return chan;
	}
	public static void setChan(String chan) {
		KedacomPropertiesUtil.chan = chan;
	}
	public static String getManu() {
		return manu;
	}
	public static void setManu(String manu) {
		KedacomPropertiesUtil.manu = manu;
	}
	
	
	public static String getSimpleURL(String puid) {
		if(puid == null || "".equals(puid)) {
			return "";
		}
		
		StringBuffer s = new StringBuffer();
		s.append(addr).append(wenhao).append("user="+mcuUesr)
			.append(ands).append("password="+mcuPwd)
			.append(ands).append("domainid="+domainid)
			.append(ands).append("devid="+puid)
			.append(aite).append(domainName)
			.append(ands).append("chnid="+src)
			.append(ands).append("vsid="+chan)
			.append(ands).append("manu="+manu)
			.append(ands).append("high="+clear)
			.append(ands).append("transmode="+protocol)
			.append(ands).append("easymode=real");
		
		return s.toString();
	}
	
	public static void main(String[] args) {
		setAddr("http://219.142.40.247:8090/playweb/index_play.html");
		setMcuUesr("admin@zsy");
		setMcuPwd("kedacom@123");
		setDomainid("f2475a91dabc4cb6b851e422e863280a");
		setDomainName("zsy");
		setChan("1");
		setSrc("1");
		setManu("kedacom");
		System.out.println(getSimpleURL("bd9128f49bd7462fbe6b9a455d4d1b5c"));
	}
	
}
