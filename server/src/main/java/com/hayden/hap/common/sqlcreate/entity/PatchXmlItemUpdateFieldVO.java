package com.hayden.hap.common.sqlcreate.entity;

import com.hayden.hap.dbop.entity.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 升级，xml中使用了U操作，记录字段更新时需满足更新数据的条件。
 * @author wangyi
 * @date 2017年9月4日
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PatchXmlItemUpdateFieldVO extends BaseVO {

	private static final long serialVersionUID = 1L;

	public String fieldname;// 需要过滤的field名称
	public String whereclase;// 过滤条件

}