package com.hayden.hap.common.dict.entity;

import com.hayden.hap.common.utils.DictUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 全息查询树节点对象
 * @author zhangfeng
 * @date 2016年6月20日
 */
public class TreeNodeVO implements ITreeNodeVO, Serializable{

	private static final long serialVersionUID = 1L;

	private String code;
	
	private String name;
	
	private Long dictdataid;
	
	private String itemCode;
	
	private Long parentDictDataId;
	
	private List<TreeNodeVO> children;

	public TreeNodeVO() {}
	
	public TreeNodeVO(DictDataVO dictDataVO,String itemCode) {
		this.code = dictDataVO.getDict_data_code();
		this.name = dictDataVO.getDict_data_name();
		this.dictdataid = dictDataVO.getDictdataid();
		this.itemCode = itemCode;
		this.children = DictUtils.dictData2TreeNode(dictDataVO.getChildren(),itemCode);
	}
	
	public TreeNodeVO(DictDataWarperVO dictDataWarperVO,String itemCode) {
		this.code = dictDataWarperVO.getCode();
		this.name = dictDataWarperVO.getName();
		this.dictdataid = dictDataWarperVO.getDictdataid();
		this.itemCode = itemCode;
		this.children = DictUtils.dictDataWarper2TreeNode(dictDataWarperVO.getChildren(),itemCode);
	}
	
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

	public List<TreeNodeVO> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNodeVO> children) {
		this.children = children;
	}

	public Long getDictdataid() {
		return dictdataid;
	}

	public void setDictdataid(Long dictdataid) {
		this.dictdataid = dictdataid;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Long getParentDictDataId() {
		return parentDictDataId;
	}

	public void setParentDictDataId(Long parentDictDataId) {
		this.parentDictDataId = parentDictDataId;
	}
}
