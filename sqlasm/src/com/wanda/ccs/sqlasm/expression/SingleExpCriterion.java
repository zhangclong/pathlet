package com.wanda.ccs.sqlasm.expression;

import com.wanda.ccs.sqlasm.util.ValueUtils;

public class SingleExpCriterion implements ExpressionCriterion<String> {
	
	private String id;
	
	private String label;
	
	private String groupId;
	
	private Operator op;
	
	private String value;
	
	private boolean empty;
	
	public SingleExpCriterion(String id, String value) {
		this(id, null, null, null, value);
	}

	public SingleExpCriterion(String id, String label, String groupId, Operator op, String value) {
		this.id = id;
		this.label = label;
		this.groupId = groupId;
		this.op = op;
		this.value = value.trim();
		this.empty = ValueUtils.isEmpty(this.value);
	}

	public String getId() {
		return id;
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

	public String getValue() {
		return value;
	}

	public boolean isEmpty() {
		return empty;
	}
	
}
