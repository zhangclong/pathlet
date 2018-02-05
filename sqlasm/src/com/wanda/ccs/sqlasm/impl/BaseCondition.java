package com.wanda.ccs.sqlasm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanda.ccs.sqlasm.Condition;
import com.wanda.ccs.sqlasm.Criterion;

/**
 * 
 * 针对
 * @author Charlie Zhang
 *
 */
public abstract class BaseCondition implements Condition {
	

	abstract public boolean test(Criterion crit);
	
	abstract public String getHashCode();

	private String criterionId;
	
	public BaseCondition(String criterionId) {
		this.criterionId = criterionId;
	}

	public String getCriterionId() {
		return criterionId;
	}

	public List<Criterion> test(Map<String, List<Criterion>> criteriaMap) {
		List<Criterion> passedCriteria = null;
		List<Criterion> criteria = criteriaMap.get(criterionId);
		if(criteria != null && criteria.size() > 0) {
			passedCriteria = new ArrayList<Criterion>(2);
			for(Criterion crit : criteria) {
				if(test(crit)) {
					passedCriteria.add(crit);
				}
			}
		}
		
		return passedCriteria;
	}
	


	public int hashCode() {
		String hashKey = getHashCode();
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hashKey == null) ? 0 : hashKey.hashCode());
		return result;
	}


	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseCondition other = (BaseCondition) obj;
		if (getHashCode() == null) {
			if (other.getHashCode() != null)
				return false;
		} else if (!getHashCode().equals(other.getHashCode()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getHashCode();
	}
	
	
	
}
