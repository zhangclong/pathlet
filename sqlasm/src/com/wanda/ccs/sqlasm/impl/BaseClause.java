package com.wanda.ccs.sqlasm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.wanda.ccs.sqlasm.Clause;
import com.wanda.ccs.sqlasm.ClauseResult;
import com.wanda.ccs.sqlasm.Condition;
import com.wanda.ccs.sqlasm.CriteriaDefineException;
import com.wanda.ccs.sqlasm.CriteriaParseException;
import com.wanda.ccs.sqlasm.Criterion;

public abstract class BaseClause implements Clause {
	
	protected String paragraphId;
	
	protected String clause;
	
	protected boolean distinct;
	
	protected List<CondClause> depends;

	protected String[] fromCriterionIds;
	
	public BaseClause() {
		this.distinct = true;
		this.fromCriterionIds = null;
		this.depends = new ArrayList<CondClause>();
	}

	public String getParagraphId() {
		return paragraphId;
	}
	
	public String[] getFromCriterionIds() {
		return fromCriterionIds;
	}

	public String getClause() {
		return clause;
	}

	public List<CondClause> getDepends() {
		return depends;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public Clause depends(Clause clause) {
		return depends(null, clause);
	}
	
	public Clause depends(Condition cond, Clause clause) {
		depends.add(new CondClause(cond, clause));
		return this;
	}
	
	
	abstract public ClauseResult toResult(Criterion crit);
	
	public List<ClauseResult> toResult(List<Criterion> crits)
			throws CriteriaParseException {
		List<ClauseResult> results = new ArrayList<ClauseResult>();
		if(crits != null && crits.size() > 0) {
			for(Criterion crit : crits) {
				ClauseResult result = toResult(crit);
				if(result != null) {
					results.add(result);
				}
			}

		}
		else {
			ClauseResult result = toResult((Criterion)null);
			if(result != null) {
				results.add(result);
			}
		}
		
		return results;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clause == null) ? 0 : clause.hashCode());
		result = prime * result
				+ ((paragraphId == null) ? 0 : paragraphId.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseClause other = (BaseClause) obj;
		if (clause == null) {
			if (other.clause != null)
				return false;
		} else if (!clause.equals(other.clause))
			return false;
		if (paragraphId == null) {
			if (other.paragraphId != null)
				return false;
		} else if (!paragraphId.equals(other.paragraphId))
			return false;
		return true;
	}

	public String toString() {
		return "Clause[paragraphId=" + getParagraphId()
				+ ", fromCriterionId=" + Arrays.toString(getFromCriterionIds())
				+ ", clause=" + getClause() + ", isDistinct="
				+ isDistinct() + "]";
	}

	public Clause from(String... criterionId) {
		this.fromCriterionIds = criterionId;
		return this;
	}

	public Clause in(String paragraphId, boolean distinct) {
		this.paragraphId = paragraphId;
		this.distinct = distinct;
		return this;
	}

	public Clause in(String paragraphId) {
		this.paragraphId = paragraphId;
		this.distinct = true;
		return this;
	}
	
}
