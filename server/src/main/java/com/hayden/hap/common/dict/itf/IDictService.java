package com.hayden.hap.common.dict.itf;

import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月7日
 */
@IService("dictService")
public interface IDictService {

	/**
	 * 字典表表名字段
	 */
	public static final String DICT_T_TABLE_ITEM = "dict_t_table";
	
	/**
	 * 根据字典编码获取字典
	 * @param dictCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月7日
	 */
	public DictVO getDictByCode_Cache(String dictCode,Long tenantid);
	
	/**
	 * 根据表名获取对应的字典编码
	 * @param tablecode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月9日
	 */
	public List<String> getDictcodesByTablecode_Cache(String tablecode, Long tenantid);
}
