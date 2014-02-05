package com.google.code.pathlet.web.widget;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.code.pathlet.config.convert.BeanInfoWrapper;
import com.google.code.pathlet.config.convert.Property;
import com.google.code.pathlet.vo.QueryParamVo;
import com.google.code.pathlet.vo.QueryResultVo;
import com.google.code.pathlet.web.exception.WebWidgetException;
import com.google.code.pathlet.web.widget.JqgridQueryResult;

/**
 * 
 * Convert the JQGird parameter and result set.
 * 
 * @author Charlie Zhang
 * @since 2012-8-23
 *
 */
public class JqgridQueryConverter {
	
	public final static String PARAM_SORT_NAME = "sort";
	
	public final static String PARAM_SORT_ORDER = "order"; 
	
	public final static String PARAM_CURRENT_PAGE = "page";
	
	public final static String PARAM_RETRIEVE_SIZE = "rows";
	
	private int currentPage;
	
	private int fetchSize;
	
	public QueryParamVo convertParam(JqgridQueryParamVo jqVo)
			throws WebWidgetException {
		QueryParamVo t = new QueryParamVo();
		this.currentPage = jqVo.getPage();
		t.setFetchSize(jqVo.getRows());
		this.fetchSize = t.getFetchSize();
		t.setStartIndex((this.currentPage - 1) * t.getFetchSize());
		t.setSortName(jqVo.getSort());
		t.setSortOrder(jqVo.getOrder());

		return t;
	}
	
	
	/**
	 * Struts Action接收Map<String, String> params， 把请求的参数转换为QueryParamVo扩展类参数。
	 * 
	 * @param params  
	 * @param parametersClass
	 * @return
	 * @throws WebWidgetConvertException
	 * @since 2012-8-23
	 */
	public <T extends QueryParamVo> T convertParam(Map<String, String[]> params, Class<T> parametersClass)
			throws WebWidgetException {
		
		try {
			T t = parametersClass.newInstance();
			this.currentPage = Integer.parseInt(params.get(PARAM_CURRENT_PAGE)[0]);
			t.setFetchSize(Integer.parseInt(params.get(PARAM_RETRIEVE_SIZE)[0]));
			this.fetchSize = t.getFetchSize();
			t.setStartIndex((this.currentPage - 1) * t.getFetchSize());
			t.setSortName(params.get(PARAM_SORT_NAME)[0]);
			t.setSortOrder(params.get(PARAM_SORT_ORDER)[0]);
			
			BeanInfoWrapper beanInfoWrapper = new BeanInfoWrapper(parametersClass);
			
			for(Map.Entry<String, String[]> entry : params.entrySet() ) {
				String name = entry.getKey();
				String value = getFirst(entry.getValue());
				PropertyDescriptor prop = beanInfoWrapper.getPropertyDesc(name);

				if(prop != null 
						&& "fetchSize".equals(name) == false 
						&& "startIndex".equals(name) == false 
						&& "sortName".equals(name) == false 
						&& "sortOrder".equals(name) == false) {
					beanInfoWrapper.setProperty(name, t, value);			
				}
			}
			
			return t;
		} 
		catch(Exception e) {
			throw new WebWidgetException("Failed to convert into QueryParameters!", e);
		}
	}
	
	/**
	 * 把分页查询的结果集合，转换为需要页面显示使用的分页数据格式
	 * F is the row data set object. It could be Map<String, Object> or java bean. 
	 * 
	 * @param queryResult
	 * @return
	 * @since 2012-8-23
	 */
	public <F> JqgridQueryResult<F> convertResult(QueryResultVo<F> queryResult, ResultRowMapper<F> rowMapper) {
		
		JqgridQueryResult<F> jqgridResult = new JqgridQueryResult<F>();
		
		if(rowMapper != null) {
			List<F> dataList = queryResult.getDataList();
			List<F> newDataList = new ArrayList<F>(dataList.size());
			for(F row: dataList) {
				F newRow = rowMapper.convert(row);
				newDataList.add(newRow);
			}
			jqgridResult.setRows(newDataList);
		}
		else {
			jqgridResult.setRows(queryResult.getDataList());
		}
		
		jqgridResult.setUserdata(queryResult.getFooterData());

		
		jqgridResult.setPage(currentPage);
		jqgridResult.setRecords(queryResult.getRowCount());
		
		//Compute the total page count.
		int pageCount = (int)(queryResult.getRowCount() / this.fetchSize);
		if((queryResult.getRowCount() % this.fetchSize) > 0) {
			pageCount ++;
		}
		jqgridResult.setTotal(pageCount);//Total pages count
		
		return jqgridResult;
	}
	
	private String getFirst(String[] values) {
		if(values != null && values.length >= 1) {
			String value = values[0];
			if(value != null) {
				return value.trim();
			}
		}
		
		return null;
	}
	
}
