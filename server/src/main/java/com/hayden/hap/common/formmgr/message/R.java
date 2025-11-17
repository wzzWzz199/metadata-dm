package com.hayden.hap.common.formmgr.message;

import com.hayden.hap.common.enumerate.MessageLevel;

import java.util.List;

/**
 * @ClassName R
 * @Description TODO
 * @Author icc
 * @Date 2019-11-22 14:52
 **/
public class R<T> extends ReturnResult<T>{

    public R() {}
    /**
     * 包含返回数据和消息对象集合
     * @param data
     * @param messages
     * @author zhangfeng
     * @date 2016年12月21日
     */
    public R(T data, List<Message> messages) {
        super(data,messages);
    }

    /**
     * 包含返回数据和字符串消息内容，消息级别为INFO级别
     * @param data
     * @param messageStr
     * @author zhangfeng
     * @date 2016年12月21日
     */
    public R(T data, String messageStr) {
        super(data,messageStr);
    }

    /**
     * 包含返回数据和消息对象
     * @param data
     * @param message
     * @author zhangfeng
     * @date 2016年12月21日
     */
    public R(T data, Message message) {
        super(data,message);
    }

    /**
     * 只有字符串消息内容，消息级别为INFO级别
     * @param messageStr
     * @author zhangfeng
     * @date 2016年12月21日
     */
    public R(String messageStr) {
        super(messageStr);
    }

    /**
     * 包含消息内容和消息级别
     * @param messageStr
     * @param level
     * @author zhangfeng
     * @date 2016年12月21日
     */
    public R(String messageStr, MessageLevel level) {
        super(messageStr,level);
    }

    public static <E> R<E> ok(E data) {
        R<E> r = new R<>();
        r.setData(data);
        return r;
    }

    public static R<?> ok() {
        R<?> r = new R<>("操作成功");
        return r;
    }

    public static <E> R<E> ok(E data, String messageStr) {
        return new R<>(data, messageStr);
    }

    public static R<?> error(String messageStr) {
        R<?> r = new R<>(messageStr,MessageLevel.ERROR);
        return r;
    }

}
