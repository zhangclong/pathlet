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


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ognl.DefaultClassResolver;
import ognl.NullHandler;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.TypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Utility class that provides common access to the Ognl APIs for
 * setting and getting properties from objects (usually Actions).
 *
 * @author Charlie Zhang
 */
public class OgnlUtil {

    private static final Log log = LogFactory.getLog(OgnlUtil.class);
    private ConcurrentHashMap<String, Object> expressions = new ConcurrentHashMap<String, Object>();

    private TypeConverter defaultConverter;
    private DefaultClassResolver defaultClassResolver;
    private ReflectionProvider reflectionProvider;
    
    private boolean enableExpressionCache = true;
    private boolean enableDev = false;

    public OgnlUtil(TypeConverter defaultConverter, DefaultClassResolver defaultClassResolver) {
		NullHandler nh = new InstantiatingNullHandler();
		OgnlRuntime.setNullHandler(Object.class, nh);
		OgnlRuntime.setNullHandler(byte[].class, nh);
		OgnlRuntime.setNullHandler(short[].class, nh);
		OgnlRuntime.setNullHandler(char[].class, nh);
		OgnlRuntime.setNullHandler(int[].class, nh);
		OgnlRuntime.setNullHandler(long[].class, nh);
		OgnlRuntime.setNullHandler(float[].class, nh);
		OgnlRuntime.setNullHandler(double[].class, nh);
		OgnlRuntime.setNullHandler(Object[].class, nh);
		
		this.reflectionProvider = new ReflectionProvider();
		
		this.defaultConverter = defaultConverter;
		this.defaultClassResolver = defaultClassResolver;

    }
    
    /**
     * Sets the properties on the object using the default context, defaulting to not throwing
     * exceptions for problems setting the properties.
     *
     * @param properties
     * @param o
     */
    public void setProperties(Map<String, ?> properties, Object o) throws OgnlException {
        setProperties(properties, o, false);
    }

    /**
     * Sets the object's properties using the default type converter.
     *
     * @param props                   the properties being set
     * @param o                       the object
     * @param context                 the action context
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context, boolean throwPropertyExceptions) 
    		throws OgnlException 
    {
        if (props == null) {
            return;
        }

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        for (Map.Entry<String, ?> entry : props.entrySet()) {
            String expression = entry.getKey();
            internalSetProperty(expression, entry.getValue(), o, context, throwPropertyExceptions);
        }

        Ognl.setRoot(context, oldRoot);
    }


    /**
     * Sets the properties on the object using the default context.
     *
     * @param properties              the property map to set on the object
     * @param o                       the object to set the properties into
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    public void setProperties(Map<String, ?> properties, Object o, boolean throwPropertyExceptions) throws OgnlException {
        Map context = Ognl.createDefaultContext(o, this.defaultClassResolver, this.defaultConverter);
        setProperties(properties, o, context, throwPropertyExceptions);
    }

    /**
     * Sets the named property to the supplied value on the Object, defaults to not throwing
     * property exceptions.
     *
     * @param name    the name of the property to be set
     * @param value   the value to set into the named property
     * @param o       the object upon which to set the property
     * @param context the context which may include the TypeConverter
     */
    public void setProperty(String name, Object value, Object o, Map<String, Object> context) throws OgnlException {
        setProperty(name, value, o, context, false);
    }

    /**
     * Sets the named property to the supplied value on the Object.
     *
     * @param name                    the name of the property to be set
     * @param value                   the value to set into the named property
     * @param o                       the object upon which to set the property
     * @param context                 the context which may include the TypeConverter
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the property
     */
    public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws OgnlException {
        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        internalSetProperty(name, value, o, context, throwPropertyExceptions);

        Ognl.setRoot(context, oldRoot);
    }


    /**
     * Wrapper around Ognl.setValue() to handle type conversion for collection elements.
     * Ideally, this should be handled by OGNL directly.
     */
    public void setValue(String name, Map<String, Object> context, Object root, Object value) throws OgnlException {
        Ognl.setValue(compile(name), context, root, value);
    }

    public Object getValue(String name, Map<String, Object> context, Object root) throws OgnlException {
        return Ognl.getValue(compile(name), context, root);
    }

    public Object getValue(String name, Map<String, Object> context, Object root, Class resultType) throws OgnlException {
        return Ognl.getValue(compile(name), context, root, resultType);
    }


    public Object compile(String expression) throws OgnlException {
        if (enableExpressionCache) {
            Object o = expressions.get(expression);
            if (o == null) {
                o = Ognl.parseExpression(expression);
                expressions.put(expression, o);
            }
            return o;
        } else
            return Ognl.parseExpression(expression);
    }

    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork2.conversion.impl.XWorkConverter} if none
     * is specified.
     *
     * @param from       the source object
     * @param to         the target object
     * @param context    the action context we're running under
     * @param exclusions collection of method names to excluded from copying ( can be null)
     * @param inclusions collection of method names to included copying  (can be null)
     *                   note if exclusions AND inclusions are supplied and not null nothing will get copied.
     */
    public void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions) {
        if (from == null || to == null) {
            log.warn("Attempting to copy from or to a null source. This is illegal and is bein skipped. This may be due to an error in an OGNL expression, action chaining, or some other event.");

            return;
        }

        TypeConverter conv = defaultConverter;
        Map contextFrom = Ognl.createDefaultContext(from);
        Ognl.setTypeConverter(contextFrom, conv);
        Map contextTo = Ognl.createDefaultContext(to);
        Ognl.setTypeConverter(contextTo, conv);

        PropertyDescriptor[] fromPds;
        PropertyDescriptor[] toPds;

        try {
            fromPds = this.reflectionProvider.getPropertyDescriptors(from);
            toPds = this.reflectionProvider.getPropertyDescriptors(to);
        } catch (IntrospectionException e) {
            log.error("An error occured", e);

            return;
        }

        Map<String, PropertyDescriptor> toPdHash = new HashMap<String, PropertyDescriptor>();

        for (PropertyDescriptor toPd : toPds) {
            toPdHash.put(toPd.getName(), toPd);
        }

        for (PropertyDescriptor fromPd : fromPds) {
            if (fromPd.getReadMethod() != null) {
                boolean copy = true;
                if (exclusions != null && exclusions.contains(fromPd.getName())) {
                    copy = false;
                } else if (inclusions != null && !inclusions.contains(fromPd.getName())) {
                    copy = false;
                }

                if (copy == true) {
                    PropertyDescriptor toPd = toPdHash.get(fromPd.getName());
                    if ((toPd != null) && (toPd.getWriteMethod() != null)) {
                        try {
                            Object expr = compile(fromPd.getName());
                            Object value = Ognl.getValue(expr, contextFrom, from);
                            Ognl.setValue(expr, contextTo, to, value);
                        } catch (OgnlException e) {
                            // ignore, this is OK
                        }
                    }

                }

            }

        }
    }


    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork2.conversion.impl.XWorkConverter} if none
     * is specified.
     *
     * @param from    the source object
     * @param to      the target object
     * @param context the action context we're running under
     */
    public void copy(Object from, Object to, Map<String, Object> context) {
        copy(from, to, context, null, null);
    }



    private void internalSetProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws OgnlException{
        try {
            setValue(name, context, o, value);
        } catch (OgnlException e) {
            Throwable reason = e.getReason();
            String msg = "Caught OgnlException while setting property '" + name + "' on type '" + o.getClass().getName() + "'.";
            Throwable exception = (reason == null) ? e : reason;

            if (throwPropertyExceptions) {
                throw new OgnlException(msg, exception);
            } else {
            	if(enableDev == true) {
            		log.warn(msg, exception);
            		System.out.println(msg);
            		exception.printStackTrace();
            	}
            }
        }
    }
    


}
