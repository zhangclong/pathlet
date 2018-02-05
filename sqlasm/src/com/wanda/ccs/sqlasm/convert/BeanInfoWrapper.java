package com.wanda.ccs.sqlasm.convert;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanInfoWrapper {
	
	private Class<?> beanClass;
	
	private BeanInfo beanInfo;
	
	private Map<String, PropertyDescriptor> propertiesMap = null;
	
	private PropertyDescriptor[]  properties;
	
	public BeanInfoWrapper(Class<?> beanClass) {
		this.beanClass = beanClass;
		
		try {
			this.beanInfo = Introspector.getBeanInfo(beanClass);
			this.properties = beanInfo.getPropertyDescriptors();
			
		} 
		catch (IntrospectionException e) {
			throw new PropertyConfigException("Failed parse bean info from class=" + beanClass.getName(), e);
		}
		
	}

	public Class<?> getBeanClass() {
		return this.beanClass;
	}
	
	public BeanInfo getBeanInfo	() {
		return this.beanInfo;
	}
	
	public PropertyDescriptor[] getPropertyDescs() {
		return this.properties;
	}

	public Map<String, PropertyDescriptor> getPropertyDescsMap() {
		if(propertiesMap == null) {
			PropertyDescriptor[] properties = getPropertyDescs();
			propertiesMap = new HashMap<String, PropertyDescriptor>(properties.length);
			for(PropertyDescriptor prop : properties) {
				propertiesMap.put(prop.getName(), prop);
			}
		}
		return propertiesMap;
	}
	
	public PropertyDescriptor getPropertyDesc(String propertyName) {
		return getPropertyDescsMap().get(propertyName);
	}
	
	public Class<?> getPropertyType(String propertyName) {
		return getPropertyDesc(propertyName).getPropertyType();
	}

	
	public void setProperty(String propertyName, Object bean, Object value) {
		PropertyDescriptor desc = getPropertyDesc(propertyName);
		Method writeMethod = desc.getWriteMethod();
		if(writeMethod != null) {
			try {
				writeMethod.invoke(bean, value);
			}
			catch(Exception e) {
				throw new PropertyConfigException("Failed to set property!  bean class='"
						+ bean.getClass() + "' property=" + propertyName, e);
			}
		}
		else {
			throw new PropertyConfigException("Failed to find the writable method or field!  bean class='"
					+ bean.getClass() + "' property=" + propertyName);
		}
	}

	
}
