package com.google.code.pathlet.jdbc;

/**
 * 
 * Convert a name in camelCase to an underscored name in lower case.
 * Any upper case letters are converted to lower case with a preceding underscore.
 * 
 * @author Zhang Chen Long
 *
 */
public interface EntityPropertyConvertor {
	
	String propertyToColumn(String property);
	
	String columnToProperty(String column);
	
}
