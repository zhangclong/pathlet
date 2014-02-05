package com.google.code.pathlet.web.ognl.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import ognl.OgnlException;
import ognl.OgnlRuntime;


public class ReflectionProvider {
	
    private final ConcurrentHashMap<Class, BeanInfo> beanInfoCache = new ConcurrentHashMap<Class, BeanInfo>();
    
    public Field getField(Class inClass, String name) {
        return OgnlRuntime.getField(inClass, name);
    }

    public Method getGetMethod(Class targetClass, String propertyName)
            throws IntrospectionException, ReflectionException {
        try {
            return OgnlRuntime.getGetMethod(null, targetClass, propertyName);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    public Method getSetMethod(Class targetClass, String propertyName)
            throws IntrospectionException, ReflectionException {
        try {
            return OgnlRuntime.getSetMethod(null, targetClass, propertyName);
        } catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }
    
    
    /**
     * Get's the java beans property descriptors for the given source.
     *
     * @param source the source object.
     * @return property descriptors.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        BeanInfo beanInfo = getBeanInfo(source);
        return beanInfo.getPropertyDescriptors();
    }


    /**
     * Get's the java beans property descriptors for the given class.
     *
     * @param clazz the source object.
     * @return property descriptors.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    private PropertyDescriptor[] getPropertyDescriptors(Class clazz) throws IntrospectionException {
        BeanInfo beanInfo = getBeanInfo(clazz);
        return beanInfo.getPropertyDescriptors();
    }


    /**
     * Get's the java bean info for the given source object. Calls getBeanInfo(Class c).
     *
     * @param from the source object.
     * @return java bean info.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public BeanInfo getBeanInfo(Object from) throws IntrospectionException {
        return getBeanInfo(from.getClass());
    }


    /**
     * Get's the java bean info for the given source.
     *
     * @param clazz the source class.
     * @return java bean info.
     * @throws IntrospectionException is thrown if an exception occurs during introspection.
     */
    public BeanInfo getBeanInfo(Class clazz) throws IntrospectionException {
        synchronized (beanInfoCache) {
            BeanInfo beanInfo;
            beanInfo = beanInfoCache.get(clazz);
            if (beanInfo == null) {
                beanInfo = Introspector.getBeanInfo(clazz, Object.class);
                beanInfoCache.put(clazz, beanInfo);
            }
            return beanInfo;
        }
    }

}

