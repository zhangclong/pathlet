package com.wanda.ccs.sqlasm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanda.ccs.sqlasm.ClauseParagraph;
import com.wanda.ccs.sqlasm.ClauseResult;
import com.wanda.ccs.sqlasm.CriteriaResult;
import com.wanda.ccs.sqlasm.DataType;
import com.wanda.ccs.sqlasm.expression.CriteriaUtils;
import com.wanda.ccs.sqlasm.util.ValueUtils;


public class DefaultCriteriaResult implements CriteriaResult {
	
	//Store the ClauseResult divided by groupId
	private Map<String, List<ClauseResult>> groupedResults;
	
	private List<Object> parameters; 
	
	private List<DataType> parameterTypes;
	
	private String composedClause;
	
	private String parameterizeClause;
	
	private ClauseParagraph[] paragraphs;
	
	public DefaultCriteriaResult(ClauseParagraph[] paragraphs, Map<String, List<ClauseResult>> groupedResults) {
		this.paragraphs = paragraphs;
		this.groupedResults = groupedResults;
		this.parameters = new ArrayList<Object>();
		this.parameterTypes = new ArrayList<DataType>();
		compose();
	}
	
	public List<DataType> getParameterTypes() {
		return parameterTypes;
	}


	public List<Object> getParameters() {
		return parameters;
	}

	public String getComposedText() {
		return composedClause;
	}
	
	public boolean isEmpty() {
		return ValueUtils.isEmpty(this.parameters) && ValueUtils.isEmpty(composedClause);
	}
	
	public Map<String, List<ClauseResult>> getParagraphedResults() {
		return groupedResults;
	}

	public String getParameterizeText() {
		if(parameterizeClause == null) {
			
			StringBuilder buf = new StringBuilder(composedClause.length() + 100);
			int srcLen = composedClause.length();
			
			int paramIndex = 0;
			for(int i=0 ; i<srcLen ; i++) {
				char c = composedClause.charAt(i);
				if('?' == c) {
					Object param = parameters.get(paramIndex);
					DataType paramType = parameterTypes.get(paramIndex);
					buf.append(CriteriaUtils.parameterToSql(param, paramType) );
					paramIndex ++;
				}
				else {
					buf.append(c);
				}
			}
			
			this.parameterizeClause = buf.toString();
		}
		
		return this.parameterizeClause;
	}
	
	private void compose() {
		StringBuilder buf = new StringBuilder();
		for(ClauseParagraph paragraph : paragraphs) {
			List<ClauseResult> clauseResults = groupedResults.get(paragraph.getId());
			if(clauseResults.size() > 0) {
				paragraph.compose(buf, clauseResults);
				for(ClauseResult result : clauseResults) {
					if(result.getParameters() != null && result.getParameters().size() > 0) {
						parameters.addAll(result.getParameters());
						parameterTypes.addAll(result.getParameterTypes());
					}
				}
			}
		}
		this.composedClause = buf.toString();
	}



}
