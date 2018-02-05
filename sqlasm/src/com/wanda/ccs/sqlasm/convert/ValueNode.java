package com.wanda.ccs.sqlasm.convert;

import java.util.Collection;

public class ValueNode {
	
	private Object value;
	
	private Object key; //If this valueNode is Map entry. this property is the Map's key  
	
	private ValueNodeType type;
	
	private Class<?> valueType;
	
	private Collection<ValueNode> children;
	
	public ValueNode() {
		
	}
	
	public ValueNode(ValueNodeType type, Object value) {
		this.type = type;
		this.value = value;
		if(value != null) {
			this.valueType = value.getClass();
		}
	}
	
	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Class<?> getValueType() {
		return valueType;
	}
	
	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		if(value != null) {
			this.valueType = value.getClass();
		}
	}

	public ValueNodeType getType() {
		return type;
	}

	public void setType(ValueNodeType type) {
		this.type = type;
	}

	public Collection<ValueNode> getChildren() {
		return children;
	}

	public void setChildren(Collection<ValueNode> children) {
		this.children = children;
	}

	public static enum ValueNodeType {
		COLLECTION,
		MAP,
		
		STRING, 
		BOOLEAN, 
		INTEGER, 
		LONG,
		FLOAT,
		DOUBLE,

		PATH,
		MATCH_PATTERN,
		INSTANCE,
		RESOURCE,
		SPACE,
		CONTAINER
	}
}
