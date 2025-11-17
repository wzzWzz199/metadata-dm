package com.hayden.hap.common.dict.service;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.dict.itf.IDictService;
import com.hayden.hap.common.utils.SyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月7日
 */
@Service("dictService")
public class DictServiceImpl implements IDictService{

	@Autowired
	private IBaseService baseService;
	
	@Cacheable(value="SY_DICT", key="#dictCode.concat('|').concat(#tenantid)")
	@Override
	public DictVO getDictByCode_Cache(String dictCode, Long tenantid) {
		DynaSqlVO dictSqlVO = new DynaSqlVO();
		dictSqlVO.addWhereParam("dict_code", dictCode);
		dictSqlVO.addWhereParam("tenantid", tenantid);
		
		VOSet<DictVO> dictVOSet = baseService.query(DictVO.class, dictSqlVO);
		
		if(ObjectUtil.isNotEmpty(dictVOSet.getVoList())) {
			return dictVOSet.getVoList().get(0);
		}
		return null;
	}

	@Cacheable(value="OUT_DICT_TABLE2CODE_BY_TENANTID", key="#tablecode.concat('|').concat(#tenantid)")
	@Override
	public List<String> getDictcodesByTablecode_Cache(String tablecode, Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(DICT_T_TABLE_ITEM, tablecode);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		List<String> queryCols = new ArrayList<>();
		queryCols.add("dict_code");
		dynaSqlVO.setSqlColumnList(queryCols);
		
		VOSet<DictVO> voSet = baseService.query(new DictVO(), dynaSqlVO);
		List<String> list = new ArrayList<>();
		if(ObjectUtil.isNotEmpty(voSet.getVoList())) {			
			for(DictVO dictVO:voSet.getVoList()) {
				list.add(dictVO.getDict_code());
			}			
		}
		return list;
	}

}
