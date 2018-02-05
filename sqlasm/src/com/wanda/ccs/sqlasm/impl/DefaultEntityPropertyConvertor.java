package com.wanda.ccs.sqlasm.impl;

import com.wanda.ccs.sqlasm.EntityPropertyConvertor;


public class DefaultEntityPropertyConvertor implements EntityPropertyConvertor {
	

	public String propertyToColumn(String propertyName) {
		char[] origin = propertyName.toCharArray();
		int length = origin.length;
		StringBuffer ret = new StringBuffer(length + 8);
		
		//StringBuffer buffer = null;
		for (int i = 0; i < length; i++) {
			if (Character.isUpperCase(origin[i]) && i != 0) {
				ret.append('_');
			}
			ret.append(Character.toUpperCase(origin[i]));
		}

		return ret.toString();
	}


	public String columnToProperty(String columnName) { 
		char[] origin = columnName.toCharArray();
		int length = origin.length;
		StringBuffer retBuf = new StringBuffer(length);
		
		boolean uppercaseNext = false;
		for (int i = 0; i < length; i++) {
			if (origin[i] == '_') {
				uppercaseNext = true;
			}
			else {
				if(uppercaseNext == true) {
					retBuf.append(Character.toUpperCase(origin[i]));
					uppercaseNext = false;
				}
				else {
					retBuf.append(Character.toLowerCase(origin[i]));
				}
			}
		}
		return retBuf.toString();
	}
	
	

}
