package com.wanda.ccs.sqlasm.expression;

import java.util.List;

import com.wanda.ccs.sqlasm.util.ValueUtils;


public class CompositeExpCriterion implements ExpressionCriterion<CompositeValue> {
	
	private String key;
	
	private String label;
	
	private String groupId;
	
	private Operator op;
	
	private boolean empty;
	
	private CompositeValue value;
	
	public CompositeExpCriterion(String key, String label, String groupId, Operator op, CompositeValue value) {
		this.key = key;
		this.label = label;
		this.groupId = groupId;
		this.op = op;
		this.value = value;
		this.empty = parseEmpty(value);
	}

	public String getId() {
		return key;
	}
	
	public String getLabel() {
		return label;
	}

	public String getGroupId() {
		return groupId;
	}
	
	public Operator getOp() {
		return op;
	}

	public boolean isEmpty() {
		return empty;
	}

	public CompositeValue getValue() {
		return value;
	}

	private boolean parseEmpty(CompositeValue value) {
		if(value.isSelTarget()) {
			return ValueUtils.isEmpty(value.getSelections().getValue());
		}
		else {
			boolean isComositeCriteriaEmpty = true;
			List<ExpressionCriterion> comositeCriteria = value.getCriteria();
			for(ExpressionCriterion crit : comositeCriteria) {
				if(crit.isEmpty() == false) {
					isComositeCriteriaEmpty = false;
					break;
				}
			}
			return isComositeCriteriaEmpty;
		}
	}
}
