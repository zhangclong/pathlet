package com.google.code.pathlet.vo;

import java.io.Serializable;
import java.util.List;

public class QueryResultVo <T> implements Serializable  {
	
	private static final long serialVersionUID = 6443505680305222964L;

	/** total result row count */
	private Long rowCount;
	
	/** list of result data */
	private List<T> dataList;
	
	/** footer row data */
	private T footerData;
	
	public QueryResultVo() { }
	
	public QueryResultVo(Long rowCount, List<T> dataList) {
		this.rowCount = rowCount;
		this.dataList = dataList;
		this.footerData = null;
	}
	
	public QueryResultVo(Long rowCount, List<T> dataList, T footerData) {
		this.rowCount = rowCount;
		this.dataList = dataList;
		this.footerData = footerData;
	}

	public Long getRowCount() {
		return rowCount;
	}

	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public T getFooterData() {
		return footerData;
	}

	public void setFooterData(T footerData) {
		this.footerData = footerData;
	}
	
}
