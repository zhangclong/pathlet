package com.wanda.ccs.sqlasm.impl;

import com.wanda.ccs.sqlasm.Criterion;

public class DefaultCriterion<V> implements Criterion<V> {

	private String id;
	
	private V value;
	
	private boolean empty;
	
	public DefaultCriterion(String id, V value) {
		this.id = id;
		this.value = value;
		this.empty = (value == null);
	}
	
	public String getId() {
		return id;
	}

	public V getValue() {
		return value;
	}

	public boolean isEmpty() {
		return empty;
	}

}
