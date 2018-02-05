package com.wanda.ccs.sqlasm;

import java.util.Collection;
import java.util.List;

import com.wanda.ccs.sqlasm.impl.CondClause;

/**
 * 分句类，用于设置和组装分句。
 * @author Charlie Zhang
 * @since 2013-07-25
 */
public interface Clause {
	
	/**
	 * 段落id，用于指定所属的段落，组装时会把该子句放入该段落中。
	 * @return
	 */
	String getParagraphId();
	
	/**
	 * 条件来源 criterionId
	 * @return
	 */
	String[] getFromCriterionIds();
	
	/**
	 * 分句的表达式，根据分句的类型不同不同分句有不同的表达式规则。
	 * @return
	 */
	String getClause();
	
	/**
	 * TODO直接插入到paragraph中。
	 * 返回Clause的处理结果ClauseResult, 
	 * 如果返回null表示不组装该Clause，并且不组装该Clause依赖的各个子Clause。
	 * @param crit 对应的条件值其中包含前台传入的 inputId和
	 * @param parser 对应的解析器，主要用于有些数据需要存解析器中读取
	 * @return
	 * @throws CriteriaParseException
	 */
	List<ClauseResult> toResult(List<Criterion> crits) throws CriteriaParseException;
	
	
	/**
	 * 得到依赖的子语句。
	 * 在toResult()函数在返回为非空值时，CriteriaParser会通过该函数读取Clause列表，进行拼装。
	 * @return
	 */
	List<CondClause> getDepends();
	
	/**
	 * 是否去除重复保证，这条子语句只能出现一次。
	 * 判断重复的标准为：两个Clause对象的 paragraphId 和 clause两个属性是否同时相等
	 * 一般缺省为true
	 * @return
	 */
	boolean isDistinct(); 
	
	////////////////////////////////////////////////////////
	//下面为DSL模式函数定义，这些函数的返回值都为this
	////////////////////////////////////////////////////////
	
	/**
	 * 加入依赖的子语句
	 * @param clause
	 * @return
	 */
	Clause depends(Condition cond, Clause clause);
	
	/**
	 * 加入依赖的子语句
	 * @param clause
	 * @return
	 */
	Clause depends(Clause clause);
	
	/**
	 * 设置取得条件的来源 criterionId
	 * @param criterionId
	 * @return
	 */
	Clause from(String... criterionId);
	
	/**
	 * 指定Clause将会输出出现的位置，和是否会重复出现。
	 * @param paragraphId
	 * @param distinct
	 * @return
	 */
	Clause in(String paragraphId, boolean distinct);
	
	
	Clause in(String paragraphId);
	

}
