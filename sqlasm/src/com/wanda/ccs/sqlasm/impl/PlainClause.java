package com.wanda.ccs.sqlasm.impl;

import com.wanda.ccs.sqlasm.ClauseResult;
import com.wanda.ccs.sqlasm.Criterion;


/**
 * 普通的SQL组装子句
 * 对子语句的拼装不做任何条件判断和变换，直接把clause属性组装商。
 * 
 * @author Charlie Zhang
 *
 */
public class PlainClause extends BaseClause {
	

	public ClauseResult toResult(Criterion crit) {
		return new ClauseResult(this, this.getClause(), null, null);
	}
	
	////////////////////////////////////////////////////////
	//下面为DSL模式函数定义，这些函数的返回值都为this
	////////////////////////////////////////////////////////
	
	public PlainClause output(String clause) {
		this.clause = clause;
		return this;
	}

	public PlainClause in(String paragraphId,
			boolean distinct) {
		super.in(paragraphId, distinct);
		return this;
	}

	public PlainClause in(String paragraphId) {
		super.in(paragraphId);
		return this;
	}


}
