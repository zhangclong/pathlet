package com.wanda.ccs.sqlasm.impl;

import com.wanda.ccs.sqlasm.Criterion;

public class EqualsValueCond extends BaseCondition {
	
	private Object value;
	
	private String hashKey;
	
	public EqualsValueCond(String criterionId, Object value) {
		super(criterionId);
		this.value = value;
		this.hashKey = "EqualsValueCond[" + criterionId + ":" + value + "]";
	}

	public String getHashCode() {
		return hashKey;
	}

	public Object getValue() {
		return value;
	}

	public boolean test(Criterion crit) {
		if(crit != null && crit.getValue() != null) {
			return value.equals(crit.getValue());
		}
		else {
			return false;
		}
	}
}
