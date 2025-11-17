package com.hayden.hap.common.form.conver.pc.codeconver;

import com.hayden.hap.common.form.conver.pc.DefaultConverter;
import com.hayden.hap.common.form.entity.FormItemPCVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 丢弃的转换器，对于某些表字段，不转换成对应表单字段
 * @author zhangfeng
 * @date 2018年5月10日
 */
public class DiscardConverter extends DefaultConverter{

	@Override
	public List<FormItemPCVO> getItems() {
		return new ArrayList<FormItemPCVO>();
	}
}
