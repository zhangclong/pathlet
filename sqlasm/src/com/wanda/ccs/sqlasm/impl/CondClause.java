package com.wanda.ccs.sqlasm.impl;

import com.wanda.ccs.sqlasm.Clause;
import com.wanda.ccs.sqlasm.Condition;

/**
 * Store the Clause and its condition pair.
 * @author clzhang
 *
 */
public class CondClause {
	
	private Clause clause;
	
	private Condition condition;
	
	public CondClause(Condition condition, Clause clause) {
		this.clause = clause;
		this.condition = condition;
	}
	
	public Clause getClause() {
		return clause;
	}
	
	public Condition getCondition() {
		return condition;
	}
}
