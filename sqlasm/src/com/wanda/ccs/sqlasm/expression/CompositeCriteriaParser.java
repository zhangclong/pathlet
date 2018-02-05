package com.wanda.ccs.sqlasm.expression;

import java.util.Map;

import com.wanda.ccs.sqlasm.Clause;
import com.wanda.ccs.sqlasm.Condition;
import com.wanda.ccs.sqlasm.CriteriaParser;


public abstract class CompositeCriteriaParser implements CriteriaParser {
	
	protected CriteriaParser parser;

	public CriteriaParser add(Clause clause) {
		return parser.add(clause);
	}

	public CriteriaParser add(Condition cond, Clause clause) {
		return parser.add(cond, clause);
	}

	public CriteriaParser add(Map<Condition, Clause> conditionClauses) {
		return parser.add(conditionClauses);
	}
	
}
