package com.wanda.ccs.sqlasm;


/**
 * 
 * 
 */
public interface Criterion<V> {
	
	String getId();
	
	V getValue();
	
	boolean isEmpty();
	
}
