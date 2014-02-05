package com.google.code.pathlet.web.ognl;

/**
 * 
 * Convert String into another type.
 * 
 * @author Charlie Zhang
 *
 */
public interface OgnlRequestConverter {
	
	/**
	 * 
	 * @param value the value to be converted
	 * @param toType the result type Class object.
	 * @return Converted result
	 */
	Object convert(Object value, Class toType);

}
