package com.wanda.ccs.sqlasm.impl;

import java.util.Collection;

import com.wanda.ccs.sqlasm.Criterion;

/**
 * 用于判断指定的条件中，是否包含对应的值。
 * 
 * Criterion.getValue() 返回的值必须下面两种类型：是Collection或者衍生类， 或是数组。
 * test()函数会判别是否其中包含了指定值。
 * 
 * @author Charlie Zhang
 *
 */
public class ContainsValueCond extends BaseCondition {
	
	private Object value;
	
	private String hashKey;
	
	public ContainsValueCond(String criterionId, Object value) {
		super(criterionId);
		this.value = value;
		this.hashKey = "ContainsValueCond[" + criterionId + ":" + value + "]";
	}

	public String getHashCode() {
		return hashKey;
	}

	public Object getValue() {
		return value;
	}

	public boolean test(Criterion crit) {
		if(crit != null && crit.getValue() != null) {
			
			if(Collection.class.isAssignableFrom(crit.getValue().getClass()) ) {
				Collection col = (Collection)crit.getValue();
				return col.contains(value);
			}
			else if (crit.getValue().getClass().isArray()) {
				Object[] array = (Object[])crit.getValue();	
				for(Object e : array) {
					if(value.equals(e)) {
						return true;
					}
				}
				return false;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
}
