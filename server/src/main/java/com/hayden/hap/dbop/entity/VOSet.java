package com.hayden.hap.dbop.entity;

import com.hayden.hap.dbop.db.orm.sql.Page;
import com.hayden.hap.dbop.db.util.ObjectUtil;

import java.util.List;

/** 
 * @ClassName: VOSet 
 * @Description: 
 * @author LUYANYING
 * @date 2015年3月20日 下午2:39:00 
 * @version V1.0   
 *  
 */
public class VOSet<T extends AbstractVO> {
	private List<T> voList = null;
	
	private String sql = null;
	
	private Page page = null;
	
	public boolean isEmpty(){
		return !ObjectUtil.isNotEmpty(voList);
	}
	public T getVO(int index){
		if(!this.isEmpty())
			return voList.get(index);
		return null;
	}


	public List<T> getVoList() {
		return voList;
	}

	public void setVoList(List<T> voList) {
		this.voList = voList;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
