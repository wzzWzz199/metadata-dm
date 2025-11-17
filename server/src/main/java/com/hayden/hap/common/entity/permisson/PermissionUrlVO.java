package com.hayden.hap.common.entity.permisson;

import com.hayden.hap.dbop.entity.CommonVO;
import lombok.Data;

@Data
public class PermissionUrlVO extends CommonVO {
	/**
	 * 用于缓存func或button定义为url的数据
	 */
	private static final long serialVersionUID = 1L;
	private String func_code;
	private String btn_code;
}
