package com.google.code.pathlet.web.widget;

/**
 * 
 * JQGrid AJAX request common parameters for paging and sort.
 * 
 * @author Charlie Zhang
 *
 */
public class JqgridQueryParamVo {
	
	private String sort;
	
	private String order;
	
	private Integer page;
	
	private Integer rows;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	
	
	
}
