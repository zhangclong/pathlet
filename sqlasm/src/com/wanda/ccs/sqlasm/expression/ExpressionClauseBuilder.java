package com.wanda.ccs.sqlasm.expression;

import com.wanda.ccs.sqlasm.impl.PlainClause;

public class ExpressionClauseBuilder {

	public static PlainClause newPlain() {
		return new PlainClause();
	}
	
	public static ExpressionClause newExpression() {
		return new ExpressionClause();
	}
	
	public static ValueClause newValue() {
		return new ValueClause();
	}
	
}
