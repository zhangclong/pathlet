package com.google.code.pathlet.vo;

import java.io.Serializable;

/**
 * 
 * 在做数据库分页查询时传入参数的基类。
 * 其中包括：取得数据的开始行号和结束行号，数据结果集的排序方式，返回数据的最大行数。
 * 
 * @author Charlie Zhang
 * 
 */
public class QueryParamVo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public final static String SORT_ORDER_ASC = "ASC";
	
	public final static String SORT_ORDER_DESC = "DESC";
	
	/** 排序名称，一般以排序的列明命名 */
	private String sortName;
	
	/** 排序顺序，ASC 和  DESC */
	private String sortOrder;
	
	/** 取得数据开始行的索引, 以0开始 */
	private int startIndex;
	
	/** 要取得数据的行数 */
	private int fetchSize;

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
	
}
