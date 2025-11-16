/**
 * 
 */
package com.hayden.hap.common.formmgr.inputconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;

import java.util.List;
import java.util.Map;

/**
 * @author zhangfeng
 * 字典输入设定VO
 */
@JsonInclude(Include.NON_EMPTY)
public class DictInputConfigVO {
	public static final String BRANCH_AND_LEAF = "1";
	
	public static final String LEAF = "2";
	
	public static final String BRANCH = "3";	
	
	/**
	 * 字典编码
	 */
	private String dictcode;
	
	/**
	 * 映射关系，键为字典字段，值为当前表单字段
	 */
	private Map<String,String> map;
	
	/**
	 * 显示的字段
	 */
	private String title;
	
	/**
	 * 是否多选
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean ismulti = false;
	
	/**
	 * 移动端是否加载
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean isload4m = true;
	
	/**
	 * 回调
	 */
	private String callback;
	
	/**
	 * 字典数据
	 */
	private List<DictDataWarperVO> dictdata;
	
	/**
	 * 是否显示搜索
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean hassearch = false;
	
	/**
	 * 内部编码字段
	 */
	private String innercode;
	
	/**
	 * 选择类型（叶子 or 枝干 or 都可）
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String selecttype = BRANCH_AND_LEAF;

	/**
	 * 是否可选表达式
	 */
	@JsonInclude(Include.NON_EMPTY)
	private String selectexp;
	
	/**
	 * 分隔符
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String split = ",";
	
	/**
	 * 全息查询展开级次
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private Integer expandlevel = 1;
	
	private String levelcode;
	
	private String querycode;
	
	public String getQuerycode() {
		return querycode;
	}

	public void setQuerycode(String querycode) {
		this.querycode = querycode;
	}

	public String getLevelcode() {
		return levelcode;
	}

	public void setLevelcode(String levelcode) {
		this.levelcode = levelcode;
	}

	public String getDictcode() {
		return dictcode;
	}

	public void setDictcode(String dictcode) {
		this.dictcode = dictcode;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public List<DictDataWarperVO> getDictdata() {
		return dictdata;
	}

	public void setDictdata(List<DictDataWarperVO> dictdata) {
		this.dictdata = dictdata;
	}

	public boolean isIsmulti() {
		return ismulti;
	}

	public void setIsmulti(boolean ismulti) {
		this.ismulti = ismulti;
	}

	public boolean isIsload4m() {
		return isload4m;
	}

	public void setIsload4m(boolean isload4m) {
		this.isload4m = isload4m;
	}

	public boolean isHassearch() {
		return hassearch;
	}

	public void setHassearch(boolean hassearch) {
		this.hassearch = hassearch;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public String getSelecttype() {
		return selecttype;
	}

	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}

	public String getSelectexp() {
		return selectexp;
	}

	public void setSelectexp(String selectexp) {
		this.selectexp = selectexp;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public Integer getExpandlevel() {
		return expandlevel;
	}

	public void setExpandlevel(Integer expandlevel) {
		this.expandlevel = expandlevel;
	}
}
