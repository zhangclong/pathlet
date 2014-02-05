/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.pathlet.web.ognl.impl;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.NullHandler;
import ognl.Ognl;
import ognl.TypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.code.pathlet.web.ognl.ConvertException;


/**
 * <!-- START SNIPPET: javadoc -->
 *
 * OGNL expressions
 * that have caused a NullPointerException will be temporarily stopped for evaluation while the system automatically
 * tries to solve the null references by automatically creating the object.
 *
 * <p/> The following rules are used when handling null references:
 *
 * <ul>
 *
 * <li>If the property is declared <i>exactly</i> as a {@link Collection} or {@link List}, then an ArrayList shall be
 * returned and assigned to the null references.</li>
 *
 * <li>If the property is declared as a {@link Map}, then a HashMap will be returned and assigned to the null
 * references.</li>
 *
 * <li>If the null property is a simple bean with a no-arg constructor, it will simply be created using the {@link
 * Object#newInstance()} method.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: example -->
 *
 * For example, if a form element has a text field named <b>person.name</b> and the expression <i>person</i> evaluates
 * to null, then this class will be invoked. Because the <i>person</i> expression evaluates to a <i>Person</i> class, a
 * new Person is created and assigned to the null reference. Finally, the name is set on that object and the overall
 * effect is that the system automatically created a Person object for you, set it by calling setUsers() and then
 * finally called getUsers().setName() as you would typically expect.
 *
 * <!-- END SNIPPET: example>
 *
 * @author Charlie Zhang
 */
public class InstantiatingNullHandler implements NullHandler {


    private static final Log LOG = LogFactory.getLog(InstantiatingNullHandler.class);
    
    private ReflectionProvider reflectionProvider = new ReflectionProvider();
    //private ReflectionProvider reflectionProvider;
    //private ObjectFactory objectFactory;
    //private ObjectTypeDeterminer objectTypeDeterminer = new DefaultObjectTypeDeterminer();

    //@Inject
    //public void setObjectTypeDeterminer(ObjectTypeDeterminer det) {
    //    this.objectTypeDeterminer = det;
    //}


	public Object nullMethodResult(Map context, Object target,
			String methodName, Object[] args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering nullMethodResult ");
        }
		return null;
	}

	public Object nullPropertyValue(Map context, Object target, Object property) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering nullPropertyValue [target="+target+", property="+property+"]");
        }

        if ((target == null) || (property == null)) {
            return null;
        }

        try {
            String propName = property.toString();

            Class clazz = null;

            if (target != null) {
            	PropertyDescriptor[] descs = reflectionProvider.getPropertyDescriptors(target);
            	
            	for(PropertyDescriptor desc : descs) {
            		if(propName.equals(desc.getName())) {
            			clazz = desc.getPropertyType();
            		}
            	}
            }
            
            if (clazz == null) {
                // can't do much here!
                return null;
            }

            Object param = createObject(clazz, target, propName, context);
            Ognl.setValue(propName, context, target, param);

            return param;
        } catch (Exception e) {
            LOG.error("Could not create and/or set value back on to object", e);
        }

        return null;
	}

    private Object createObject(Class clazz, Object target, String property, Map<String, Object> context) throws Exception {
        if (Collection.class.isAssignableFrom(clazz)) {
        	TypeConverter typeConvert = Ognl.getTypeConverter(context);
        	Class elementClazz = getClass(target.getClass(), property, true);
        	if(elementClazz == null) {
        		elementClazz = String.class;
        	}
            return new IncreasableList(context, typeConvert, elementClazz);
        } 
        else if (clazz == Map.class) {
            return new HashMap();
        } 
        
        return clazz.newInstance();
    }
    
    /**
     * Returns the class for the given field via generic type check.
     *
     * @param parentClass the Class which contains as a property the Map or Collection we are finding the key for.
     * @param property    the property of the Map or Collection for the given parent class
     * @param element     <tt>true</tt> for indexed types and Maps.
     * @return Class of the specified field.
     */
    private Class getClass(Class parentClass, String property, boolean element) {


        try {

            Field field = reflectionProvider.getField(parentClass, property);

            Type genericType = null;

            // Check fields first
            if (field != null) {
                genericType = field.getGenericType();
            }

            // Try to get ParameterType from setter method
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method setter = reflectionProvider.getSetMethod(parentClass, property);
                    genericType = setter.getGenericParameterTypes()[0];
                }
                catch (ReflectionException ognle) {
                    ; // ignore
                }
                catch (IntrospectionException ie) {
                    ; // ignore
                }
            }

            // Try to get ReturnType from getter method
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method getter = reflectionProvider.getGetMethod(parentClass, property);
                    genericType = getter.getGenericReturnType();
                }
                catch (ReflectionException ognle) {
                    ; // ignore
                }
                catch (IntrospectionException ie) {
                    ; // ignore
                }
            }

            if (genericType instanceof ParameterizedType) {


                ParameterizedType type = (ParameterizedType) genericType;

                int index = (element && type.getRawType().toString().contains(Map.class.getName())) ? 1 : 0;

                Type resultType = type.getActualTypeArguments()[index];

                if ( resultType instanceof ParameterizedType) {
                    return (Class) ((ParameterizedType) resultType).getRawType();
                }
                return (Class) resultType;

            }
        } catch (Exception e) {
            throw new ConvertException(e);
        }
        return null;
    }
    
    /*
    public Object nullMethodResult(Map<String, Object> context, Object target, String methodName, Object[] args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering nullMethodResult ");
        }

        return null;
    }

    public Object nullPropertyValue(Map<String, Object> context, Object target, Object property) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering nullPropertyValue [target="+target+", property="+property+"]");
        }

        boolean c = ReflectionContextState.isCreatingNullObjects(context);

        if (!c) {
            return null;
        }

        if ((target == null) || (property == null)) {
            return null;
        }

        try {
            String propName = property.toString();
            Object realTarget = reflectionProvider.getRealTarget(propName, context, target);
            Class clazz = null;

            if (realTarget != null) {
                PropertyDescriptor pd = reflectionProvider.getPropertyDescriptor(realTarget.getClass(), propName);
                if (pd == null) {
                    return null;
                }

                clazz = pd.getPropertyType();
            }

            if (clazz == null) {
                // can't do much here!
                return null;
            }

            Object param = createObject(clazz, realTarget, propName, context);

            reflectionProvider.setValue(propName, context, realTarget, param);

            return param;
        } catch (Exception e) {
            LOG.error("Could not create and/or set value back on to object", e);
        }

        return null;
    }

    private Object createObject(Class clazz, Object target, String property, Map<String, Object> context) throws Exception {
        if (Collection.class.isAssignableFrom(clazz)) {
            return new ArrayList();
        } else if (clazz == Map.class) {
            return new HashMap();
        } else if (clazz == EnumMap.class) {
            Class keyClass = objectTypeDeterminer.getKeyClass(target.getClass(), property);
            return new EnumMap(keyClass);
        }

        return objectFactory.buildBean(clazz, context);
    }
    */
}
