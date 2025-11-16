package com.hayden.hap.common.utils.action.entity;

/**
 * @ClassName ActionResult
 * @Description 业务扩展调用返回结果
 * @Author zhangfeng
 * @Date 2020-04-20 10:17
 **/
public class ActionResult {

    private Object data;

    public ActionResult(Object obj) {
        this.data = obj;
    }

    public <T> T getResult(Class<T> type) {
        return (T)data;
    }

    public boolean hasResult() {
        return data!=null;
    }
}
