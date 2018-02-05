package com.wanda.ccs.sqlasm;

import java.util.List;
import java.util.Map;

public interface CriteriaParser {
	
	public <C extends Criterion> CriteriaResult parse(List<C> criteria) throws CriteriaParseException;
	
	/**
	 * 用于批量设置paragraphId； <br/>
	 * 当调用此函数之后加入的Clause如果没有设置paragraphId(即<code>Clause.in(String)</code>函数设置)，则自动沿用这里设置的paragraphId.
	 * @param paragraphId
	 * @return
	 */
	//public CriteriaParser in(String paragraphId);
	
	/**
	 * 用于批量设置criterionId；<br/>
	 * 当调用此函数之后加入的Clause如果没有设置criterionId(即<code>Clause.from(String)</code>函数设置)，则自动沿用这里设置的criterionId
	 * @param criterionId
	 * @return
	 */
	//public CriteriaParser from(String criterionId);
	
	public CriteriaParser add(Clause clause);
	
	public CriteriaParser add(Condition condition, Clause clause);
	
	public CriteriaParser add(Map<Condition, Clause> conditionClauses);


//	/**
//	 * 得到所包含的 clause group
//	 * @return
//	 */
//	public ClauseParagraph[] getClauseGroups();
//
//	/**
//	 * 得到Criteria和Clause的映射列表。
//	 * @return Map.key是 Criterion中的inputId, Map.value是相应的Clause
//	 */
//	public Map<String, Clause> getCriteriaClauseMap();

	
}
