package com.wanda.ccs.sqlasm.expression;

import java.util.Arrays;
import java.util.List;

import com.wanda.ccs.sqlasm.util.ValueUtils;



public class ArrayExpCriterion  implements ExpressionCriterion<List<String>>{
	
	private String key;
	
	private String label;
	
	private String groupId;
	
	private Operator op;
	
	private boolean empty;
	
	private List<String> value;
	
	public ArrayExpCriterion(String key, String label, String groupId, Operator op, List<String> value) {
		this.key = key;
		this.label = label;
		this.groupId = groupId;
		this.op = op;
		this.value = value;
		if(value != null && value.size() > 0) {
			boolean isEmpty = false;
			for(int i=0,len=value.size();i<len;i++) {
				if(value.get(i) == null || "".equals(value.get(i))) {
					isEmpty = true;
					break;
				}
			}
			this.empty = isEmpty;
		} else {
			this.empty = true;
		}
		
		//this.empty = ValueUtils.isEmpty(this.value);
	}
	
	public ArrayExpCriterion(String key, String label, String groupId, Operator op, String[] value) {
		this.key = key;
		this.label = label;
		this.groupId = groupId;
		this.op = op;
		this.value = Arrays.asList(value);
		this.empty = ValueUtils.isEmpty(this.value);
	}
	
	public ArrayExpCriterion(String key, String[] value) {
		this(key, null, null, null, value);
	}
	
	public ArrayExpCriterion(String key, List<String> value) {
		this(key, null, null, null, value);
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

	public List<String> getValue() {
		return value;
	}
	
}
