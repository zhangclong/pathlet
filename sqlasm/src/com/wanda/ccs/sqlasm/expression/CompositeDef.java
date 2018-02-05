package com.wanda.ccs.sqlasm.expression;

import java.util.List;

import com.wanda.ccs.sqlasm.CriteriaResult;

public interface CompositeDef {
	
	String getCompositeId();
	
	CriteriaResult parse(List<ExpressionCriterion> criteria);

}
