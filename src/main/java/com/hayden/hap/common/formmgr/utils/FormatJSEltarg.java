package com.hayden.hap.common.formmgr.utils;

/**
 * 格式化输出js片段
 * @author zhangfeng
 * @date 2016年8月23日
 */
public class FormatJSEltarg {
	public static String format(String s) {
        if (s != null && s.length() > 0) {
            s = s.replaceAll("(\r|\n|\r\n|\n\r)", " ");
            s = s.replaceAll("\"", "\\\\" + "\"");
            s = s.replaceAll("\'", "\\\\" + "\'");
            return s;
        } else {
            return "";
        }
    }
}
