package com.wanda.ccs.sqlasm.expression;

import java.util.Date;

import com.wanda.ccs.sqlasm.CriteriaParseException;
import com.wanda.ccs.sqlasm.DataType;


public class CriteriaUtils {
	/**
	 * Convert the string value into specified type object
	 * @param type
	 * @param value
	 * @return
	 */
	public static Object convertParameter(Operator op, DataType type, String value) {
        Object ret = null;
        switch(type) {
            case INTEGER:
            	ret = new Integer(value);
            	break;
            case LONG:
            	ret = new Long(value);
            	break;
            case FLOAT:
            	ret = new Float(value);
            	break;
            case DOUBLE:
            	ret = new Double(value);
            	break;
            case SHORT:
            	ret = new Short(value);
            	break;
            case DATE:
            	ret = DateUtils.parseMediumDate(value);
                break;
            case DATE_TIME:
            	ret = DateUtils.parseMediumDateTime(value);
                break;
            case STRING:
            	if(op == Operator.LIKE) {
            		ret = "%" + value + "%";
            	}
            	else {
            		ret = value;
            	}
                break;
            case SQL:
            	ret = value;
                break;
            case BOOLEAN:
                ret = ("true".equals(value) || "1".equals(value)) ? "1" : "0";
                break;
            default:
                ret = null;
                assert false; //it will never go here
                break;
        }
        
        return ret;
	}
	
	/**
	 * Convert the value object into place in SQL parameter string
	 * @param type
	 * @param value
	 * @return
	 */
	public static String parameterToSql(Object value, DataType type) {
		if(value == null) {
			return "!!NULL!!";
		}
		
        String ret;
        switch(type) {
            case INTEGER: 
            case LONG:
            case FLOAT:
            case DOUBLE:
            case SHORT:
                ret = value.toString();
                break;
            case DATE:
                ret = "to_date('" + DateUtils.dateToMediumStr((Date)value) + "', 'YYYY-MM-DD')";
                break;
            case DATE_TIME:
                ret = "to_date('" + DateUtils.dateTimeToMediumStr((Date)value) + "', 'YYYY-MM-DD HH24:MI:SS')";
                break;
            case STRING:
                ret = "'" + CriteriaUtils.escapeSQLParam(value.toString()) + "'";
                break;
            case SQL:
            	ret = value.toString();
            	break;
            case BOOLEAN:
                ret = (((Boolean)value).booleanValue()) ? "'1'" : "'0'";
                break;
            default:
                ret = null;
                assert false : type; //it will never go here
                break;
        }
        
        return ret;
	}
	
	

	/**
	 * Escape to SQL string parameter
	 * 如果字符串中带单引号',则将其变为'',比如串为"wang'pei",则变为"wang''pei"
	 * @param str
	 * @return
	 */
	public static String escapeSQLParam(String str) {
		StringBuffer buf = new StringBuffer(str.length() + 20);
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char a = str.charAt(i);
			if (a == '\'') {
				buf.append(a);
			}
			buf.append(a);
		}
		return buf.toString();
	}
	
	public static ExpressionCriterionType getCriteriaType(ExpressionCriterion crit) {
		if(crit instanceof SingleExpCriterion) {
			return ExpressionCriterionType.SINGLE;
		}
		else if(crit instanceof ArrayExpCriterion) {
			return ExpressionCriterionType.ARRAY;
		}
		else if(crit instanceof CompositeExpCriterion) {
			return ExpressionCriterionType.COMPOSITE;
		}
		else {
			throw new CriteriaParseException("Could not support the ExpressionCriterion type: " + crit.getClass().getCanonicalName());
		}
	}
	
}
