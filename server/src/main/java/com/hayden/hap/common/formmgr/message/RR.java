package com.hayden.hap.common.formmgr.message;

import java.io.Serializable;

/**
 * @ClassName RR, RemoteResult, dubbo远程调用的返回数据结构
 * @Description TODO
 * @Author icc
 * @Date 2019-11-22 14:30
 **/
public class RR<T> implements Serializable {
    /**
     * 状态码
     */
    private int status = Status.SUCCESS;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * 错误消息
     */
    private String errorMsg;

    public RR(T t) {
        this.data = t;
    }

    public RR(T t, int status, String errorMsg ) {
        this.data = t;
        this.status = status;
        this.errorMsg = errorMsg;
    }

    public RR(int status, String errorMsg ) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    public static <T> RR<T> ok(T t) {
        return new RR<>(t);
    }

    public static <T> RR<T> error(String msg) {
        return new RR(Status.FAIL, msg);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
