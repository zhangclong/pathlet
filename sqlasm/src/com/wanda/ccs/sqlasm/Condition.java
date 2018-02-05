package com.wanda.ccs.sqlasm;

import java.util.List;
import java.util.Map;

public interface Condition {

	/**
	 * 参数criteriaMap为条件全集，通过此函数得到吻合条件的所有Criterion>列表。
	 * 如果没有吻合条件的Criterion则返回空的列表.
	 * @param criteriaMap   所有条件集合，其中的key为criterionId, value为List<Criterion>即criterionId下对应的Criterion集合
	 * @return 吻合条件的所有Criterion>列表; 如果没有吻合条件的Criterion则返回空的列表.
	 */
	List<Criterion> test(Map<String, List<Criterion>> criteriaMap);


}