package com.hayden.hap.common.entity.form;

import com.hayden.hap.common.entity.func.FuncVO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
/**
 * 此对象已不建议使用了
 * 
 * @author zhangfeng
 * @date 2015年12月21日
 */
public class FormConfigVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 功能信息
	 */
	private FuncVO funcVO;
	
	/**
	 * 表单信息
	 */
	private FormPCVO formVO;
	

	/**
	 * 表单字段信息
	 */
	private List<FormItemPCVO> formFields;
	
	/**
	 * 主键列列名
	 */
	private String pkColName;

	/**
	 * 所需的字典数据
	 */
//	private Map<String,List<SyDictData>> dictMap = new HashMap<String, List<SyDictData>>();
	
	/**
	 * 关联功能表单
	 */
	private List<FormConfigVO> refFormConfigVOList = new ArrayList<FormConfigVO>();
	

//	public Map<String, List<SyDictData>> getDictMap() {
//		return dictMap;
//	}

//	public void setDictMap(Map<String, List<SyDictData>> dictMap) {
//		this.dictMap = dictMap;
//	}

	public FormPCVO getFormVO() {
		return formVO;
	}

	public void setFormVO(FormPCVO formVO) {
		this.formVO = formVO;
	}

	public List<FormItemPCVO> getFormFields() {
		return formFields;
	}

	public void setFormFields(List<FormItemPCVO> formFields) {
		this.formFields = formFields;
	}

	public FuncVO getFuncVO() {
		return funcVO;
	}

	public void setFuncVO(FuncVO funcVO) {
		this.funcVO = funcVO;
	}

	public List<FormConfigVO> getRefFormConfigVOList() {
		return refFormConfigVOList;
	}

	public String getPkColName() {
		return pkColName;
	}

	public void setPkColName(String pkColName) {
		this.pkColName = pkColName;
	}
	
}
