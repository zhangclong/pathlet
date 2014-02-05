package com.google.code.pathlet.web.ognl.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ognl.DefaultTypeConverter;

import com.google.code.pathlet.web.ognl.ConvertException;
import com.google.code.pathlet.web.ognl.OgnlRequestConverter;


/**
 * 
 * It will automatically handle the most common type conversion for you. This includes support for converting to
 * and from Strings for each of the following:
 * <p/>
 * <ul>
 * <li>String</li>
 * <li>boolean / Boolean</li>
 * <li>char / Character</li>
 * <li>int / Integer, float / Float, long / Long, double / Double</li>
 * <li>dates - uses the SHORT format for the Locale associated with the current request</li>
 * <li>arrays - assuming the individual strings can be coverted to the individual items</li>
 * <li>collections - if not object type can be determined, it is assumed to be a String and a new ArrayList is
 * created</li>
 * </ul>
 * <p/> Note that with arrays the type conversion will defer to the type of the array elements and try to convert each
 * item individually. As with any other type conversion, if the conversion can't be performed the standard type
 * conversion error reporting is used to indicate a problem occured while processing the type conversion.
 * <p/>
 *
 * @author Charlie Zhang
 */
public class ExtensibleConverter extends DefaultTypeConverter {

	
	private Map<Class, OgnlRequestConverter> converterMap;
	
	public ExtensibleConverter(Map<Class, OgnlRequestConverter> converterMap) {
		this.converterMap = converterMap;
	}
	
	/*
	 * DefaultTypeConverter supported to Types
	 *   Integer, Double, Boolean, Byte, Character, Short, Long, Float, BigInteger, BigDecimal, String
	 * 
	 */

    public Object convertValue(Map context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;

        if (value == null || toType.isAssignableFrom(value.getClass())) {
            // no need to convert at all
            return value;
        }


        OgnlRequestConverter extensiveConvert = getExtensiveConvert(toType);
        
        if(extensiveConvert != null) {
        	result = extensiveConvert.convert(value, toType);
        }
        else if (toType.isArray()) {
            result = doConvertToArray(context, o, member, s, value, toType);
        } 
        else if (Collection.class.isAssignableFrom(toType)) {
            result = doConvertToCollection(context, o, member, s, value, toType); 
        } 
        else if (toType == Class.class) {
            result = doConvertToClass(value);
        }
        else {
        	result = super.convertValue(context, o, member, s, value, toType);
        }
        
        if (result == null) {
            if (value instanceof Object[]) {
                Object[] array = (Object[]) value;

                if (array.length >= 1) {
                    value = array[0];
                } else {
                    value = null;
                }

                // let's try to convert the first element only
                result = convertValue(context, o, member, s, value, toType);
            } else if (!"".equals(value)) { // we've already tried the types we know
                result = super.convertValue(context, value, toType);
            }

            if (result == null && value != null && !"".equals(value)) {
                throw new ConvertException("Cannot create type " + toType + " from value " + value);
            }
        }

        return result;
    }

    /**
     * Extensive convert
     * @return
     */
    public OgnlRequestConverter getExtensiveConvert(Class toType) {
    	if(converterMap != null && converterMap.size() > 0) {
    		for(Map.Entry<Class, OgnlRequestConverter> entry : converterMap.entrySet()) {
    			if(entry.getKey().isAssignableFrom(toType)) {
    				return entry.getValue();
    			}
    		}
    	}
    	
    	return null;
    }


    /**
     * Creates a Collection of the specified type.
     *
     * @param fromObject
     * @param propertyName
     * @param toType       the type of Collection to create
     * @param memberType   the type of object elements in this collection must be
     * @param size         the initial size of the collection (ignored if 0 or less)
     * @return a Collection of the specified type
     */
    private Collection createCollection(Map context, Object fromObject, String propertyName, Class toType, Class memberType, int size) {
//        try {
//            Object original = Ognl.getValue(OgnlUtil.compile(propertyName),fromObject);
//            if (original instanceof Collection) {
//                Collection coll = (Collection) original;
//                coll.clear();
//                return coll;
//            }
//        } catch (Exception e) {
//            // fail back to creating a new one
//        }

        Collection result;

        if (toType == Set.class) {
            if (size > 0) {
                result = new HashSet(size);
            } else {
                result = new HashSet();
            }
        } else if (toType == SortedSet.class) {
            result = new TreeSet();
        } else {
            if (size > 0) {
                result = new IncreasableList(context, this, memberType, size);
            } else {
                result = new IncreasableList(context, this, memberType);
            }
        }

        return result;
    }

    private Object doConvertToArray(Map<String, Object> context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;
        Class componentType = toType.getComponentType();

        if (componentType != null) {

            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                result = Array.newInstance(componentType, length);

                for (int i = 0; i < length; i++) {
                    Object valueItem = Array.get(value, i);
                    Array.set(result, i, this.convertValue(context, o, member, s, valueItem, componentType));
                }
            } else {
                result = Array.newInstance(componentType, 1);
                Array.set(result, 0, this.convertValue(context, o, member, s, value, componentType));
            }
        }

        return result;
    }



    private Class doConvertToClass(Object value) {
        Class clazz = null;

        if (value instanceof String && value != null && ((String) value).length() > 0) {
            try {
                clazz = Class.forName((String) value);
            } catch (ClassNotFoundException e) {
                throw new ConvertException(e);
            }
        }

        return clazz;
    }

    private Collection doConvertToCollection(Map<String, Object> context, Object o, Member member, String prop, Object value, Class toType) {
        Collection result;
        Class memberType = String.class;

        if (o != null) {
            //memberType = (Class) XWorkConverter.getInstance().getConverter(o.getClass(), XWorkConverter.CONVERSION_COLLECTION_PREFIX + prop);
        	
        	//TODO add type determine
            //memberType = objectTypeDeterminer.getElementClass(o.getClass(), prop, null);

            if (memberType == null) {
                memberType = String.class;
            }
        }

        if (toType.isAssignableFrom(value.getClass())) {
            // no need to do anything
            result = (Collection) value;
        } else if (value.getClass().isArray()) {
            Object[] objArray = (Object[]) value;
            result = createCollection(context, o, prop, toType, memberType, objArray.length);

            for (Object anObjArray : objArray) {
                result.add(this.convertValue(context, o, member, prop, anObjArray, memberType));
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection col = (Collection) value;

            result = createCollection(context, o, prop, toType, memberType, col.size());

            for (Object aCol : col) {
                result.add(this.convertValue(context, o, member, prop, aCol, memberType));
            }
        } else {
            result = createCollection(context, o, prop, toType, memberType, -1);
            result.add(value);
        }

        return result;
    }






}

