package com.wanda.ccs.sqlasm.impl;

import com.wanda.ccs.sqlasm.Criterion;

public class NotEmptyCond extends BaseCondition {

	private String criterionId;
	
	private String hashKey;
	
	public NotEmptyCond(String criterionId) {
		super(criterionId);
		this.hashKey = criterionId;
	}

	public String getCriterionId() {
		return criterionId;
	}
	
	public String getHashCode() {
		return hashKey;
	}

	public boolean test(Criterion crit) {
		if(crit != null && crit.isEmpty() == false) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
