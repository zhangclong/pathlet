package com.google.code.pathlet.config.convert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.util.ReflectionUtils;
import com.google.code.pathlet.util.StringUtils;


//import org.springframework.util.ReflectionUtils;
//import org.springframework.util.StringUtils;

/**
 * A description of a JavaBeans Property facility. .
 *
 * @author Charlie Zhang
 * 
 */
public final class Property {
	
	private final String name; 

	private final Class<?> objectType;

	private Method readMethod;

	private Method writeMethod;
	
	private Field field; 
	
	private boolean fieldAccessing; //accessing this property by field
	
	private MethodParameter methodParameter;
	
	//If objectType is java.util.Collection or subclass, this value is generic parameter type of this objectType.
	//For instance: objectType is List<String>, this will be <code>java.lang.String</code>. 
	//If objectType is java.util.Map or subclass, this value is generic 'V' parameter type of this objectType.
	//For instance: objectType is Map<String, Integer>, this will be <code>java.lang.Integer</code>. 
	private Class<?> contentParamType = null; 
	
	//If objectType is java.util.Map or subclass, this value is generic 'K' parameter type of this objectType.
	//For instance: objectType is Map<String, Integer>, this will be <code>java.lang.String</code>. 
	private Class<?> keyParamType = null;
	
	//Indicate the objectType is java.util.Map or its subclass
	private boolean isCollection = false;
	
	//Indicate the objectType is java.util.Collection or its subclass
	private boolean isMap = false;


	public Property(Class<?> objectType, PropertyDescriptor propDesc) {
		this.name = propDesc.getName();
		this.objectType = objectType;
		this.readMethod = propDesc.getReadMethod();
		this.writeMethod = propDesc.getWriteMethod();
		
		this.methodParameter = resolveMethodParameter();
		
		if( Collection.class.isAssignableFrom(propDesc.getPropertyType()) ) {
			//this.contentParamType = GenericCollectionTypeResolver.getCollectionParameterType(
			//		new MethodParameter(propDesc.getWriteMethod(), 0));
			//TODO NEED TEST
			this.contentParamType = GenericCollectionTypeResolver.getCollectionParameterType(methodParameter);
			this.isCollection = true;
		}
		else if(Map.class.isAssignableFrom(propDesc.getPropertyType())) {
			//this.contentParamType = GenericCollectionTypeResolver.getMapValueParameterType(new MethodParameter(propDesc.getWriteMethod(), 0));
			//this.keyParamType = GenericCollectionTypeResolver.getMapKeyParameterType(new MethodParameter(propDesc.getWriteMethod(), 0));
			//TODO NEED TEST
			this.contentParamType = GenericCollectionTypeResolver.getMapValueParameterType(methodParameter);
			this.keyParamType = GenericCollectionTypeResolver.getMapKeyParameterType(methodParameter);
			this.isMap = true;
		}
	}
	
	/**
	 * Access this property will only by the field. 
	 * In this construction, it will not check the getter and setter method for this property.
	 * @param objectType
	 * @param field
	 */
	public Property(Class<?> objectType, Field field) {
		this.objectType = objectType;
		this.name = field.getName();
		this.field = field;
		if(field != null) {
			this.fieldAccessing = true;
		}
	}
	
	public Property(Class<?> objectType, Method writeMethod) {
		this.objectType = objectType;
		this.writeMethod = writeMethod;
		if(writeMethod.getName().startsWith("set") == false) {
			throw new ConfigException("Property should be used on methods with 'set' start with its name." + writeMethod);
		}
		
		//Get property name within the java bean regular
		String propertyName = writeMethod.getName().substring(3);
		this.name = StringUtils.uncapitalize(propertyName);
		
		//Get readMethod, if it exists.
		Method readMd = null;
		try { readMd = objectType.getMethod("get" + propertyName); } 
		catch (NoSuchMethodException e) { /*Ignored this exception for none finding method. */ } 
		try { readMd = objectType.getMethod("is" + propertyName); } 
		catch (NoSuchMethodException e) { /*Ignored this exception for none finding method. */ } 
		if(readMd != null) {
			this.readMethod = readMd;
		}
		
		this.fieldAccessing = false;
		
		this.methodParameter = resolveMethodParameter();
	}

	/**
	 * The object declaring this property, either directly or in a superclass the object extends.
	 */
	public Class<?> getObjectType() {
		return this.objectType;
	}

	/**
	 * The name of the property: e.g. 'foo'
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The property type: e.g. <code>java.lang.String</code>
	 */
	public Class<?> getType() {
		if(this.field != null) {
			return this.field.getType();
		}
		else {
			return this.methodParameter.getParameterType();
		}
	}

	/**
	 * The property getter method: e.g. <code>getFoo()</code>
	 */
	public Method getReadMethod() {
		return this.readMethod;
	}

	/**
	 * The property setter method: e.g. <code>setFoo(String)</code>
	 */
	public Method getWriteMethod() {
		return this.writeMethod;
	}
	
	public Class<?> getContentParamType() {
		return contentParamType;
	}

	public Class<?> getKeyParamType() {
		return keyParamType;
	}
	
	public void setProperty(Object bean, Object value) {
		if(this.fieldAccessing) {
			ReflectionUtils.makeAccessible(field);
			try {
				field.set(bean, value);
			}
			catch(Exception e) {
				throw new PropertyConfigException("Failed to set field!  bean class='"
						+ bean.getClass() + "' property=" + this.name);
			}
		}
		else   if(writeMethod != null) {
			try {
				writeMethod.invoke(bean, value);
			}
			catch(Exception e) {
				throw new PropertyConfigException("Failed to set property!  bean class='"
						+ bean.getClass() + "' property=" + this.name, e);
			}
		}
		else {
			throw new PropertyConfigException("Failed to find the writable method or field!  bean class='"
						+ bean.getClass() + "' property=" + this.name);
		}
	}
	
	public boolean isCollection() {
		return isCollection;
	}

	public boolean isMap() {
		return isMap;
	}
	
	/**
	 * Test this property could be write or not.
	 * @return
	 */
	public boolean isWriteable() {
		if(this.fieldAccessing || writeMethod != null) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Property other = (Property) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	private MethodParameter resolveMethodParameter() {
		MethodParameter read = resolveReadMethodParameter();
		MethodParameter write = resolveWriteMethodParameter();
		if (write == null) {
			if (read == null) {
				throw new IllegalStateException("Property is neither readable nor writeable");
			}
			return read;
		}
		if (read != null) {
			Class<?> readType = read.getParameterType();
			Class<?> writeType = write.getParameterType();
			if (!writeType.equals(readType) && writeType.isAssignableFrom(readType)) {
				return read;
			}
		}
		return write;
	}
	
	private MethodParameter resolveReadMethodParameter() {
		if (getReadMethod() == null) {
			return null;
		}
		return resolveParameterType(new MethodParameter(getReadMethod(), -1));			
	}

	private MethodParameter resolveWriteMethodParameter() {
		if (getWriteMethod() == null) {
			return null;
		}
		return resolveParameterType(new MethodParameter(getWriteMethod(), 0));			
	}

	private MethodParameter resolveParameterType(MethodParameter parameter) {
		// needed to resolve generic property types that parameterized by sub-classes e.g. T getFoo();
		GenericTypeResolver.resolveParameterType(parameter, getObjectType());
		return parameter;			
	}

	
	

}