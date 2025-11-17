package com.hayden.hap.common.dict.entity;

import com.hayden.hap.common.formmgr.entity.PureVO;
import com.hayden.hap.common.utils.DictUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 字典数据包装类，方便使用
 * @author zhangfeng
 * @date 2015年12月7日
 */
public class DictDataWarperVO extends PureVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String code;
		
	private String name;
	
	private Long dictdataid;
	
	private Integer isleaf;
	
	private List<DictDataWarperVO> children;
	
	public DictDataWarperVO(String code,String name,Long id) {
		this.code = code;
		this.name = name;
		this.dictdataid = id;
	}
	
	public DictDataWarperVO(String code,String name,Long id,Integer isleaf) {
		this.code = code;
		this.name = name;
		this.dictdataid = id;
		this.isleaf = isleaf;
	}
	
	public DictDataWarperVO(DictDataVO dictDataVO) {
		this.code = dictDataVO.getDict_data_code();
		this.name = dictDataVO.getDict_data_name();
		this.dictdataid = dictDataVO.getDictdataid();
		this.isleaf = dictDataVO.getDict_data_isleaf();
		this.children = DictUtils.warpDictData(dictDataVO.getChildren());
	}
	
	public DictDataWarperVO() {}

	/**
	 * 字典数据编码
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 字典数据中文名
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DictDataWarperVO> getChildren() {
		return children;
	}

	public void setChildren(List<DictDataWarperVO> children) {
		this.children = children;
	}

	public Long getDictdataid() {
		return dictdataid;
	}

	public void setDictdataid(Long dictdataid) {
		this.dictdataid = dictdataid;
	}

	public Integer getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(Integer isleaf) {
		this.isleaf = isleaf;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		
		if(this==obj)
			return true;
		
		if(!(obj instanceof DictDataWarperVO))
			return false;
		
		DictDataWarperVO copy = (DictDataWarperVO)obj;				
		if(this.code!=null && this.code.equals(copy.getCode())) 
			return true;
		
		if(this.dictdataid!=null && this.dictdataid.equals(copy.getDictdataid()))
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		if(this.code!=null)
			return this.code.hashCode();
		if(this.dictdataid!=null)
			return this.dictdataid.toString().hashCode();
		return 1;
	}
}
