package com.hayden.hap.common.utils.properties;


/**
 * 消息推送，配置使用推送方式
 * @author wangyi
 * @date 2017年5月10日
 */
public class MsgPushPropertiesUtil {

	private static String msgPushType;

	public static String getMsgPushType() {
		return msgPushType;
	}

	public void setMsgPushType(String msgPushType) {
		MsgPushPropertiesUtil.msgPushType = msgPushType;
	}
    
}
