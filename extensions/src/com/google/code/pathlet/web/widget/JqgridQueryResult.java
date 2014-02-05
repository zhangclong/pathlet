package com.google.code.pathlet.web.widget;

import java.util.List;
import java.util.Map;


/**
 * 
 * Response data set for JQGrid one request. 
 * It includes current page number, total page count, total row count and current page rows data. <br/>
 * It is the customized format to compatible JQGrid receiving JSON which set the "jsonReader.repeatitems" property as false. <br/>
 * For example: <br/>
 *  $("#gridTagId").jqgrid{ ....  jsonReader: {repeatitems: false},  ....}
 *   
 * @author Charlie Zhang
 * @since 2012-03-22
 */
public class JqgridQueryResult<T> {
	
	private List<T> rows;
	
	private T userdata;//Data for jqgrid footer row data.
	
	//Current page No.
	private Integer page;
	
	//Total pages count
	private Integer total;
	
	//Total rows count
	private Long records;
	
	public JqgridQueryResult(){}
	
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Long getRecords() {
		return records;
	}
	public void setRecords(Long records) {
		this.records = records;
	}

	public T getUserdata() {
		return userdata;
	}

	public void setUserdata(T userdata) {
		this.userdata = userdata;
	}


	
}
