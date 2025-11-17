package com.hayden.hap.common.formmgr.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回结果对象
 * @author zhangfeng
 * @date 2016年12月1日
 */
public class ReturnResult<T> {
	
	/**
	 * 状态码
	 */
	private int status = Status.SUCCESS;
	
	/**
	 * 返回的数据
	 */
	@JsonInclude(Include.NON_NULL)
	private T data;
	
	/**
	 * 消息集合
	 */
	@JsonInclude(Include.NON_NULL)
	private List<Message> messages;
	
	/**
	 * 默认构造器，什么都没做
	 * 
	 * @author zhangfeng
	 * @date 2016年12月21日
	 */
	public ReturnResult() {}
	
	/**
	 * 只有返回数据
	 * @param data
	 * @author zhangfeng
	 * @date 2016年12月21日
	 */
	public ReturnResult(T data) {
		this.data = data;
	}
	
	/**
	 * 是否有默认“操作成功”消息的构造方法，消息级别为INFO级别
	 * @param hasDefaultMessage 是否有默认“操作成功”消息
	 * @author zhangfeng
	 * @date 2017年5月25日
	 */
	public ReturnResult(boolean hasDefaultMessage) {
		if(hasDefaultMessage) {
			Message message = new Message("操作成功");
			message.setMessageShowType(MessageShowType.FLOW);
			List<Message> messages = new ArrayList<>();
			messages.add(message);
			this.messages = messages;
		}
	}
	
	/**
	 * 包含返回数据和消息对象集合
	 * @param data
	 * @param messages
	 * @author zhangfeng
	 * @date 2016年12月21日
	 */
	public ReturnResult(T data, List<Message> messages) {
		this.data = data;
		this.messages = messages;
		this.status = calculatingStatus(messages);
	}
	
	/**
	 * 包含返回数据和字符串消息内容，消息级别为INFO级别
	 * @param data
	 * @param messageStr
	 * @author zhangfeng
	 * @date 2016年12月21日
	 */
	public ReturnResult(T data, String messageStr) {
		this.data = data;
		
		Message message = new Message(messageStr);
		message.setMessageShowType(MessageShowType.FLOW);
		List<Message> messages = new ArrayList<>();
		messages.add(message);
		this.messages = messages;
	}
	
	/**
	 * 包含返回数据和消息对象
	 * @param data
	 * @param message
	 * @author zhangfeng
	 * @date 2016年12月21日
	 */
	public ReturnResult(T data, Message message) {
		this.data = data;
		List<Message> messages = new ArrayList<>();
		messages.add(message);
		this.messages = messages;
		this.status = message.getStatus();
	}
	
	/**
	 * 只有字符串消息内容，消息级别为INFO级别
	 * @param messageStr
	 * @author zhangfeng
	 * @date 2016年12月21日
	 */
	public ReturnResult(String messageStr) {
		Message message = new Message(messageStr);
		message.setMessageShowType(MessageShowType.FLOW);
		List<Message> messages = new ArrayList<>();
		messages.add(message);
		this.messages = messages;
	}
	
	/**
	 * 包含消息内容和消息级别
	 * @param messageStr
	 * @param level
	 * @author zhangfeng
	 * @date 2016年12月21日
	 */
	public ReturnResult(String messageStr,MessageLevel level) {
		Message message = new Message(messageStr,level);
		List<Message> messages = new ArrayList<>();
		messages.add(message);
		this.messages = messages;
		this.status = message.getStatus();
	}
	
	/**
	 * 计算状态码（取其最大值，因为数值越大，越严重）
	 * @param messages
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月22日
	 */
	private int calculatingStatus(List<Message> messages) {
		int maxStatus = Status.SUCCESS;
		for(Message message : messages) {
			if(message.getStatus()>maxStatus)
				maxStatus = message.getStatus();
		}
		return maxStatus;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
		this.status = calculatingStatus(messages);
	}
	
	/**
	 * 设置消息字符串<br/>
	 * <span><font color="red">注意：若已设置消息集合，不操作</font></span>
	 * @param messageStr 
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	public void setMessage(String messageStr) {
		if(this.messages!=null) {
			return;
		}
		Message message = new Message(messageStr);
		message.setMessageShowType(MessageShowType.FLOW);
		List<Message> messages = new ArrayList<>();
		messages.add(message);
		this.messages = messages;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public static <E> ReturnResult<E> ok(E data) {
		ReturnResult<E> result = new ReturnResult<>();
		result.setData(data);
		return result;
	}
	
	public static ReturnResult<?> ok() {
		ReturnResult<?> result = new ReturnResult<>("操作成功");
		return result;
	}
}
