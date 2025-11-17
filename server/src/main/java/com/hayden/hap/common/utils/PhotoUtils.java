package com.hayden.hap.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 头像算法逻辑
 * 
 * @author zhenjianting
 * @date 2018年4月26日
 */
public class PhotoUtils {
	private static final Logger logger = LoggerFactory.getLogger(PhotoUtils.class);

	/**
	 * 昵称的简称算法
	 * 
	 * @param name
	 * @return
	 */
	public static String getSimpleName(String name) {
		if (StringUtils.isBlank(name))
			return name;

		// 预处理，替换掉非法字符
		name = name.replace(" ", "");
		if (name.length() <= 2)
			return name;

		int lastDotIndex = name.lastIndexOf(".");
		// 点前沒有字符，取后两个
		if (lastDotIndex == 0)
			return name.substring(1, 3);

		// 点后沒有字符，取前两个
		if (lastDotIndex == name.length() - 1)
			return name.substring(name.length() - 3, name.length() - 1);

		// 无点，纯英文取前两个，纯中文和混合的取后两个
		if (lastDotIndex == -1) {
			boolean isEn = name.matches("[a-zA-Z]+");
			if (isEn)
				return name.substring(0, 2);
			else
				return name.substring(name.length() - 2);
		}

		// 有一个和多个点，前后均英文、点前为纯英文取英文首字母和点后首字符。前混合后英文、前英文后混合、前混合后混合取点前一字符和点后一字符
		int preDotIndex = name.substring(0, lastDotIndex).lastIndexOf(".");
		String preNamePart = name.substring(preDotIndex + 1, lastDotIndex);
		boolean isPreEn = preNamePart.matches("[a-zA-Z]+");
		if (isPreEn) {
			return preNamePart.substring(0, 1) + name.substring(lastDotIndex + 1, lastDotIndex + 2);
		} else {
			return preNamePart.substring(preNamePart.length() - 1) + name.substring(lastDotIndex + 1, lastDotIndex + 2);
		}
	}

}
