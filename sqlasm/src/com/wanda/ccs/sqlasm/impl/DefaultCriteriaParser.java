package com.wanda.ccs.sqlasm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanda.ccs.sqlasm.Clause;
import com.wanda.ccs.sqlasm.ClauseParagraph;
import com.wanda.ccs.sqlasm.ClauseResult;
import com.wanda.ccs.sqlasm.Condition;
import com.wanda.ccs.sqlasm.CriteriaParseException;
import com.wanda.ccs.sqlasm.CriteriaParser;
import com.wanda.ccs.sqlasm.CriteriaResult;
import com.wanda.ccs.sqlasm.Criterion;
import com.wanda.ccs.sqlasm.util.ValueUtils;

public class DefaultCriteriaParser implements CriteriaParser {
	
	private ClauseParagraph[] paragraphs;
	
	private List<CondClause> condClauseList;

	public DefaultCriteriaParser(ClauseParagraph[] paragraphs) {
		this.paragraphs = paragraphs;
		this.condClauseList = new ArrayList<CondClause>();
	}

	////////////////////////////////////////////////////////
	//下面为DSL模式函数定义，这些函数的返回值都为this
	////////////////////////////////////////////////////////
	public CriteriaParser add(Clause clause) {
		add(null, clause);
		return this;
	}

	public CriteriaParser add(Condition cond, Clause clause) {
		condClauseList.add(new CondClause(cond, clause));
		return this;
	}

	public CriteriaParser add(Map<Condition, Clause> conditionClauses) {
		Set<Map.Entry<Condition, Clause>> entrys = conditionClauses.entrySet();
		for(Map.Entry<Condition, Clause> entry : entrys) {
			add(entry.getKey(), entry.getValue());
		}
		
		return this;
	}

	@SuppressWarnings("rawtypes")
	public <C extends Criterion> CriteriaResult parse(List<C> criteria) {
		Map<String, List<Criterion>> criteriaMap = new HashMap<String, List<Criterion>>();
		for(Criterion c: criteria) {
			List<Criterion> cs = criteriaMap.get(c.getId());
			if(cs == null) {
				cs = new ArrayList<Criterion>(3);
				criteriaMap.put(c.getId(), cs);
			}
			cs.add(c);
		}
		return parse(criteriaMap);
	}
	

	public <C extends Criterion> CriteriaResult parse(Map<String, List<Criterion>> criteriaMap)
			throws CriteriaParseException {
		//用于记录已经加入结果集的 clause.id, 并用于排重那些有 distinct=true的ClauseResult
		Set<Clause> distinctResults = new HashSet<Clause>();
		
		//Store the ClauseResult group by paragraph
		Map<String, List<ClauseResult>> paragraphedResults = new LinkedHashMap<String, List<ClauseResult>>(paragraphs.length);
		for(ClauseParagraph paragraph : paragraphs) {
			paragraphedResults.put(paragraph.getId(), new ArrayList<ClauseResult>());
		}
		
		//处理每一个子句子
		for(CondClause condClause : condClauseList) {
			recursiveAddClause(condClause, criteriaMap, distinctResults, paragraphedResults);
		}
		
		return new DefaultCriteriaResult(paragraphs, paragraphedResults);
	}
	
	private List<Criterion> getFromCriteria(String[] fromCritIds, Map<String, List<Criterion>> criteriaMap) {
		List<Criterion> fromCriteria = new ArrayList<Criterion>();
		for(String fromCritId : fromCritIds) {
			List<Criterion> fc = criteriaMap.get(fromCritId);
			if(fc != null) {
				fromCriteria.addAll(fc);
			}
		}
		
		if(fromCriteria.size() <= 0) {
			throw new CriteriaParseException("Failed to find the criterion: criterionId=" + Arrays.toString(fromCritIds) 
					+ ", which defined in clause \"from\" property!");
		}
		
		return fromCriteria;
	}
	
	private void recursiveAddClause(CondClause condClause, Map<String, List<Criterion>> criteriaMap, 
			Set<Clause> distinctResults, Map<String, List<ClauseResult>> paragraphedResults) {

		Clause clause = condClause.getClause();
		Condition cond = condClause.getCondition();
		List<Criterion> crits = null;
		
		//如果条件存在，且不满足直接返回不在生子句结果，也不递归查找其依赖子句。
		if(cond != null) { //定义了条件的情况
			crits = cond.test(criteriaMap);
			if(crits == null || crits.size() <= 0) {
				return;
			}
		}
		
		//Try to get the List<Criterion> from the "from" property in Clause
		String[] fromCritIds = clause.getFromCriterionIds();
		if(ValueUtils.notEmpty(fromCritIds)) { 
			crits = getFromCriteria(fromCritIds, criteriaMap);
		}

		//处理依赖的子句
		Collection<CondClause> depends = condClause.getClause().getDepends();
		for(CondClause depend : depends) {
			//Recursive parse and add the dependent clauses
			recursiveAddClause(depend, criteriaMap, distinctResults, paragraphedResults);
		}
		
		//进行重复检查，保证加入没有重复子句，也保证不加入重复的依赖子句
		if(clause.isDistinct() == false ||
				(clause.isDistinct() == true && distinctResults.contains(clause) == false)) {
			List<ClauseResult> results = clause.toResult(crits);
			if(results != null && results.size() > 0) {
				String paragraphId = clause.getParagraphId();
				if(paragraphedResults.containsKey(paragraphId) == false) {
					throw new CriteriaParseException("Undefined paragraphId='" + paragraphId + 
							"' in clause:" + clause.toString() + "!" );
				}
				
				paragraphedResults.get(paragraphId).addAll(results);
				if(clause.isDistinct() == true) {
					distinctResults.add(clause);
				}
			}
			else {
				throw new CriteriaParseException("Failed to get result, when parsing the clause:" + clause.toString() + "!" );
			}
		}
		
	}


}
