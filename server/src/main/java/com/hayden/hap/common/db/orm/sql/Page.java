package com.hayden.hap.common.db.orm.sql;

/** 
 * 
 * @ClassName: Page 
 * @Description: 分页信息
 * @author LUYANYING
 * @date 2015年4月20日 上午11:32:30 
 * @version V1.0   
 * 
 * @param <T>
 */

public class Page {
	
	public static final String PAGE_NO="pn";
	
	public static final String PAGE_LIMIT="limit";

	// 一页显示的记录数
	private int limit = 10;
	// 记录总数
	private int totalRows=-1;
	// 当前页码
	private int pageNo;
	
	public Page(int pageNo, int limit){
		this.pageNo = pageNo;
		this.limit = limit;
	}
	public Page(int pageNo, int limit, int totalRows){
		this.pageNo = pageNo;
		this.limit = limit;
		this.totalRows = totalRows;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @Title: getTotalPages 
	 * @Description: 计算总页数
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int getTotalPages() {
		int totalPages;
		if (totalRows % limit == 0) {
			totalPages = totalRows / limit;
		} else {
			totalPages = (totalRows / limit) + 1;
		}
		return totalPages;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getOffset() {
		return (pageNo - 1) * limit;
	}

	public int getEndIndex() {
		if (getOffset() + limit > totalRows) {
			return totalRows;
		} else {
			return getOffset() + limit;
		}
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
}
