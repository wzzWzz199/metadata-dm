package com.hayden.hap.dbop.utils;


import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.resp.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 业务断言工具类
 */
@SuppressWarnings("all")
public class HDBizAssert {

    private static final Logger logger = LoggerFactory.getLogger(HDBizAssert.class);

    private HDBizAssert() {
    }

    /**
     * 为true正常  false报错
     * ======================================== True ===================================================
     */
    public static void isTrue(boolean expression, String message) {
        ifNoPassThenThrow(expression, message);
    }


    /**
     * 为false正常  true报错
     * ======================================== False ===================================================
     */

    public static void isFalse(boolean expression, String message) {
        ifNoPassThenThrow(!expression, message);
    }


    /**
     * 不为空正常，为空报错
     * ======================================== NotBlank ===================================================
     */

    public static void isNotBlank(String str, String message) {
        ifNoPassThenThrow(StringUtils.isNotBlank(str), message);
    }


    /**
     * 不为null正常，为null报错
     * ======================================== NotNull ===================================================
     */

    public static void isNotNull(Object obj, String message) {
        ifNoPassThenThrow(!Objects.isNull(obj), message);
    }


    /**
     * 为null正常，不为null报错
     * ======================================== Null ===================================================
     */

    public static void isNull(Object obj, String message) {
        ifNoPassThenThrow(Objects.isNull(obj), message);
    }


    /**
     * 是数字正常，不是数字报错
     * ======================================== Number ===================================================
     */

    public static void isNumber(String str, String message) {
        boolean isNumber = StringUtils.isNotBlank(str) && Pattern.compile("^[-+]?[\\d]*$").matcher(str).matches();
        ifNoPassThenThrow(isNumber, message);
    }

    /**
     * 大于某个数
     * ======================================== GreaterThen ===================================================
     */

    public static void greaterThen(Long value, Long target, int code, String message, Object... parameters) {
        ifNoPassThenThrow(value != null && target != null && value > target, message);
    }

    /**
     * 相等正常，不相等报错
     * ======================================== equals ===================================================
     */
    private static <T> void equals(T o1, T o2, String message) {
        if (o1 == null) {
            isNull(o2, message);
        } else {
            ifNoPassThenThrow(o1.equals(o2), message);
        }
    }

    /**
     * collection == null 或者 collection size = 0时报错
     * ======================================== NotEmpty ===================================================
     */
    public static void isNotEmpty(Collection<?> obj, String message) {
        ifNoPassThenThrow(!(Objects.isNull(obj) || obj.isEmpty()), message);
    }

    /**
     * 占位符式消息替换, 然后抛出
     */
    private static void ifNoPassThenThrow(boolean pass, String message) {
        if (pass) return;
        logger.error(message);
        throw new HDRuntimeException(String.valueOf(Status.FAIL), message);
    }

}
