package com.wanda.ccs.sqlasm;

import java.util.List;

/**
 * 
 * Clause的处理结果。其中包含4各成员变量。
 * srcClause：用于保存源Clause
 * parameters: 这个Clause处理后对应的参数列表。
 * parameterTypes: 参数列表的类型，
 * 
 * @author Charlie Zhang
 *
 */
public class ClauseResult {
	
	private Clause srcClause;
	
	private List<Object> parameters;
	
	private List<DataType> parameterTypes;
	
	private String composedClause;
	
	public ClauseResult(Clause srcClause, String composedClause, List<Object> parameters, List<DataType> parameterTypes) {
		this.srcClause = srcClause;
		this.parameters = parameters;
		this.parameterTypes = parameterTypes;
		this.composedClause = composedClause;
	}

	public Clause getSrcClause() {
		return srcClause;
	}

	public void setSrcClause(Clause srcClause) {
		this.srcClause = srcClause;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	public List<DataType> getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(List<DataType> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public String getComposedClause() {
		return composedClause;
	}

	public void setComposedClause(String composedClause) {
		this.composedClause = composedClause;
	}
	

}
