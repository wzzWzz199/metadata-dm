package com.hayden.hap.common.resp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hayden.hap.common.enumerate.MessageShowType;

/**
 * @author zhangfeng
 * @date 2016年12月1日
 */
public class Message {

	/**
	 * 消息内容
	 */
	private String message;
	
	/**
	 * 消息提醒级别
	 */
	private MessageLevel messageLevel = MessageLevel.INFO;
	
	/**
	 * 消息显示类型
	 */
	private MessageShowType messageShowType = MessageShowType.POPUP;
	
	@JsonIgnore
	/**
	 * 状态码
	 */
	private int status = Status.SUCCESS;

	public Message() {
		
	}
	
	public Message(String message) {
		this.message = message;
	}
	
	public Message(String message, MessageLevel messageLevel) {
		this.message = message;
		this.messageLevel = messageLevel;
		
		if(MessageLevel.ERROR==messageLevel) {
			this.status = Status.FAIL;
		}
	}
	
	public Message(String message, MessageLevel messageLevel, int status) {
		this.message = message;
		this.messageLevel = messageLevel;		
		this.status = status;
	}
	
	public Message(String message, MessageLevel messageLevel, MessageShowType messageShowType) {
		this.message = message;
		this.messageLevel = messageLevel;
		this.messageShowType = messageShowType;
		
		if(MessageLevel.ERROR==messageLevel) {
			this.status = Status.FAIL;
		}
	}
	
	public Message(String message, MessageLevel messageLevel, MessageShowType messageShowType, int status) {
		this.message = message;
		this.messageLevel = messageLevel;
		this.messageShowType = messageShowType;
		this.status = status;
	}
	
	public static Message info(String message) {
		return new Message(message);		
	}
	
	public static Message warn(String message) {
		return new Message(message, MessageLevel.WARN);		
	}
	
	public static Message error(String message) {
		return new Message(message, MessageLevel.ERROR);		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MessageLevel getMessageLevel() {
		return messageLevel;
	}

	public void setMessageLevel(MessageLevel messageLevel) {
		this.messageLevel = messageLevel;
	}

	public MessageShowType getMessageShowType() {
		return messageShowType;
	}

	public void setMessageShowType(MessageShowType messageShowType) {
		this.messageShowType = messageShowType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
