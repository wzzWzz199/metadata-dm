package com.hayden.hap.common.form.conver.mobile.codeconver;

import com.hayden.hap.common.form.conver.mobile.DefaultMConverter;
import com.hayden.hap.common.form.entity.FormItemMVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 丢弃的转换器，对于某些表字段，不转换成对应表单字段
 * @author zhangfeng
 * @date 2018年5月10日
 */
public class DiscardMConverter extends DefaultMConverter{

	@Override
	public List<FormItemMVO> getItems() {
		return new ArrayList<FormItemMVO>();
	}
}
