package com.wanda.ccs.sqlasm;

import java.util.List;
import java.util.Map;

public interface CriteriaResult {
	
	List<Object> getParameters();
	
	List<DataType> getParameterTypes();
	
	String getComposedText();
	
	String getParameterizeText();
	
	/**
	 * 得到分组好的ClauseResult列表。
	 * @return
	 */
	Map<String, List<ClauseResult>> getParagraphedResults();
	
	boolean isEmpty();
	
	
}
