package com.google.code.pathlet.config.impl;

import static com.google.code.pathlet.config.convert.ValueNode.ValueNodeType;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.config.anno.ContainerIn;
import com.google.code.pathlet.config.anno.DestroyMethod;
import com.google.code.pathlet.config.anno.InitMethod;
import com.google.code.pathlet.config.anno.InstanceIn;
import com.google.code.pathlet.config.anno.Lifecycle;
import com.google.code.pathlet.config.anno.ResourceIn;
import com.google.code.pathlet.config.anno.SpaceIn;
import com.google.code.pathlet.config.convert.BeanInfoWrapper;
import com.google.code.pathlet.config.convert.Property;
import com.google.code.pathlet.config.convert.ValueNode;
import com.google.code.pathlet.config.def.BeanResourceConfig;
import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletConstants;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.util.ValueUtils;

/**
 * 
 * Wrap the configurations operations for BeanResource. 
 * Support the runtime accessing to configurations and annotations.
 * 
 * @author Charlie Zhang
 *
 */
public class BeanResourceConfigHandler {
	
	private BeanResourceConfig rawConfig;
	
	private Class<?> beanClazz;
	
	private String scope;
	
	private Method initMethod = null;
	
	private Method destroyMethod = null;

	private Map<Property, ValueNode> properties;
	
	public BeanResourceConfigHandler(BeanResourceConfig rawConfig, String creatorDefaultScope, Class<?> beanClazz) {
		this.rawConfig = rawConfig;
		this.beanClazz = beanClazz;
		this.properties = new LinkedHashMap<Property, ValueNode>();
		
		parseBeanScope(creatorDefaultScope);
		parseConfigurations();
		parseAnnotations();
	}
	
	public String getScope() {
		return this.scope;
	}
	
	public Class<?> getBeanClazz() {
		return this.beanClazz;
	}
	
	public Path getPath() {
		return rawConfig.getPath();
	}
	
	public String getClassName() {
		return rawConfig.getClassName();
	}
	
	public Map<Property, ValueNode> getProperties() {
		return this.properties;
	}
	
	public Method getInitMethod() {
		return initMethod;
	}

	public Method getDestroyMethod() {
		return destroyMethod;
	}


	/**
	 * Parse the scope from annotation or configuration. <br/>
	 * If could not found from them, the scope will be set as creator.getDefaultScope().
	 */
	private void parseBeanScope(String defaultScope) {
		String theScope = rawConfig.getScope();
		
		if(ValueUtils.isEmpty(theScope)) {
			//If could not get the scope setting from the rawConfig, try to get it from the annotation.
			Lifecycle lifecycle = this.beanClazz.getAnnotation(Lifecycle.class);
			if(lifecycle != null) {
				theScope = lifecycle.scope();
			}
			else {
				theScope = defaultScope;
			}
		}

		this.scope = theScope;
	}
	
	/**
	 * Parse the initialization, destroy event method from configuration of class annotations.
	 */
	private void parseConfigurations() {
		try {
			JsonNode propertiesNode = rawConfig.getProperties();
			
			if(propertiesNode != null && propertiesNode.size() > 0) {
				BeanInfoWrapper wrapper = new BeanInfoWrapper(this.beanClazz);
				Iterator<Map.Entry<String, JsonNode>> it = propertiesNode.fields();

				while(it.hasNext()) {
					Map.Entry<String, JsonNode> entry = it.next();
					String propName = entry.getKey();
					PropertyDescriptor propDesc = wrapper.getPropertyDesc(propName);
					if(propDesc == null) {
						throw new ConfigException("Failed to found the property name='" + propName + ", in class '" + beanClazz + "'");
					}
					Property prop = new Property(this.beanClazz, propDesc);
					ValueNode valueNode = getNodeValue(prop.getType(), entry.getValue(), prop.getContentParamType(), prop.getKeyParamType());
					
					properties.put(prop, valueNode);
				}
				
			}
			
			if(ValueUtils.notEmpty(rawConfig.getDestroyMethod())) {
				destroyMethod = beanClazz.getMethod(rawConfig.getDestroyMethod());
			}
			
			if(ValueUtils.notEmpty(rawConfig.getInitMethod())) {
				initMethod = beanClazz.getMethod( rawConfig.getInitMethod());
			}
		} catch (NoSuchMethodException e) {
			throw new ConfigException("Failed to found the method in the class '" + beanClazz.getClass() + "'", e);
			
		} catch (SecurityException e) {
			throw new ConfigException("Failed to access the method in the class  '" + beanClazz.getClass() + "'", e);
		}
		
	}

	private ValueNode getNodeValue(Class<?> classType, JsonNode jsonNode, Class<?> contentParamType, Class<?> keyParamType) throws ConfigException {
		
		ValueNodeType type;
		if(classType != null) {
			type = estimateTypeByClass(classType, jsonNode);
		}
		else {
			type = estimateTypeByJson(jsonNode);
		}
		
		
		ValueNode valueNode = new ValueNode();
		valueNode.setType(type);
		if(jsonNode.isNull()) {
			valueNode.setValue(null);
		}
		else {
			switch(type) {
			case COLLECTION:
				int collSize = jsonNode.size();
				List<ValueNode> children = new ArrayList<ValueNode>();
				for(int i=0 ; i<collSize ; i++) {
					ValueNode chlid = getNodeValue(contentParamType, jsonNode.get(i), null, null);
					children.add(chlid);
				}
				valueNode.setChildren(children);
				if(classType != null) {
					valueNode.setValueType(classType);
				}
				else {
					valueNode.setValueType(List.class);
				}
				//For COLLECTION type, the valueNode.setValue() will not be invoked, because the Collection object must not be referenced same one in each bean.
				
				break;
			case MAP:
				List<ValueNode> mapChildren = new ArrayList<ValueNode>();
				Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
				while(fields.hasNext()) {
					Map.Entry<String, JsonNode> field = fields.next();
					ValueNode child = getNodeValue(contentParamType, field.getValue(), null, null);
					child.setKey(parseKey(keyParamType, field.getKey()));
					mapChildren.add(child);
				}
				valueNode.setChildren(mapChildren);
				if(classType != null) {
					valueNode.setValueType(classType);
				}
				else {
					valueNode.setValueType(Map.class);
				}
				//For MAP type, the valueNode.setValue() will not be invoked, because the Map object must not be referenced same one in each bean.
				
				break;
				
			case PATH:
				valueNode.setValue(new Path(jsonNode.asText()));
				break;
			case MATCH_PATTERN:
				PathPattern pathPatternNode = JsonParseHelper.parsePathPattern(jsonNode);
				valueNode.setValue(pathPatternNode);
				break;
			case STRING:
				valueNode.setValue(jsonNode.asText());
				break;
			case INSTANCE:
				String strInstanceValue = jsonNode.asText();
				String instanceValue = strInstanceValue.substring(PathletConstants.INSTANCE_INJECT.length()).trim();
				valueNode.setValue(instanceValue);
				break;
			case RESOURCE:
				String strResourceValue = jsonNode.asText();
				String resourceValue = strResourceValue.substring(PathletConstants.RESOURCE_INJECT.length()).trim();
				valueNode.setValue(resourceValue);
				break;
			case SPACE:
				String strSpaceValue = jsonNode.asText();
				String spaceValue = strSpaceValue.substring(PathletConstants.SPACE_INJECT.length()).trim();
				valueNode.setValue(spaceValue);
				break;
			case CONTAINER:
				valueNode.setValue(null);
				break;
			case BOOLEAN:
				valueNode.setValue(jsonNode.asBoolean());
				break;
			case INTEGER: 
				valueNode.setValue(jsonNode.asInt());
				break;
			case LONG:
				valueNode.setValue(jsonNode.asLong());
				break;
			case FLOAT:
				Double doubleValue = jsonNode.asDouble();
				valueNode.setValue(doubleValue.floatValue());
				break;
			case DOUBLE:
				valueNode.setValue(jsonNode.asDouble());
				break;
			default:
				throw new ConfigException("Do not support the ValueNodeType:" + valueNode.getType());
			}
		}
		
		return valueNode;
	}
	
	private ValueNodeType estimateTypeByClass(Class<?> classType, JsonNode jsonNode) {

		ValueNodeType type = null;
		ValueNodeType injectType = getInjectType(jsonNode);
		if(injectType != null) {
			type = injectType;
		}
		else if(Collection.class.isAssignableFrom(classType)) {
			type = ValueNodeType.COLLECTION;
		}
		else if(Map.class.isAssignableFrom(classType)) {
			type = ValueNodeType.MAP;
		}
		else if(String.class.isAssignableFrom(classType)) {
			type = ValueNodeType.STRING;
		}
		else if((classType == Integer.class) || (classType == Integer.TYPE)) {
			type = ValueNodeType.INTEGER;
		}
		else if((classType == Boolean.class) || (classType == Boolean.TYPE)) {
			type = ValueNodeType.BOOLEAN;
		}
		else if((classType == Long.class) || (classType == Long.TYPE)) {
			type = ValueNodeType.LONG;
		}
		else if((classType == Float.class) || (classType == Float.TYPE)) {
			type = ValueNodeType.FLOAT;
		}
		else if((classType == Double.class) || (classType == Double.TYPE)) {
			type = ValueNodeType.DOUBLE;
		}
		else if(Path.class.isAssignableFrom(classType)) {
			type = ValueNodeType.PATH;
		}
		else if(PathPattern.class.isAssignableFrom(classType)) {
			type = ValueNodeType.MATCH_PATTERN;
		}
		else if(InstanceSpace.class.isAssignableFrom(classType)) {
			type = ValueNodeType.SPACE;
		}
		else if(Resource.class.isAssignableFrom(classType)) {
			type = ValueNodeType.RESOURCE;
		}
		else if(PathletContainer.class.isAssignableFrom(classType)) {
			type = ValueNodeType.CONTAINER;
		}

		
		if(type == null) {
			throw new ConfigException("Not support value parse for class: '" + classType + "' against the JSON node:'" + jsonNode.toString() + "'");
		}
		
		return type;
	}
	
	private ValueNodeType estimateTypeByJson(JsonNode jsonNode) {
		ValueNodeType type;
		
		//Types: PATH, MATCH_PATTERN, FLOAT  could not be parse from the JsonNode. 
		
		if(jsonNode.isArray()) { 
			type = ValueNodeType.COLLECTION;
		}
		else if(jsonNode.isObject()) {
			type = ValueNodeType.MAP;
		}
		else if(jsonNode.isTextual()) {
			type = ValueNodeType.STRING;
		}
		else if(jsonNode.isBoolean()) {
			type = ValueNodeType.BOOLEAN;
		}
		else if(jsonNode.isInt()) {
			type = ValueNodeType.INTEGER;
		}
		else if(jsonNode.isLong()) {
			type = ValueNodeType.LONG;
		}
		else if(jsonNode.isDouble()) {
			type = ValueNodeType.DOUBLE;
		}
		else {
			type = ValueNodeType.STRING;
		}
		
		if(type == ValueNodeType.STRING) {
			ValueNodeType injectType = getInjectType(jsonNode);
			if(injectType != null) {
				type = injectType;
			}
		}
		
		return type;
	}
	
	private ValueNodeType getInjectType(JsonNode jsonNode) {
		ValueNodeType type = null;
		if(jsonNode.isTextual()) {
			if(startWith(PathletConstants.INSTANCE_INJECT, jsonNode.asText())) {
				type = ValueNodeType.INSTANCE;
			}
			else if(startWith(PathletConstants.SPACE_INJECT, jsonNode.asText())) {
				type = ValueNodeType.SPACE;
			}
			else if(startWith(PathletConstants.RESOURCE_INJECT, jsonNode.asText())) {
				type = ValueNodeType.RESOURCE;
			}
			else if(PathletConstants.CONTAINER_INJECT.equalsIgnoreCase(jsonNode.asText())) {
				type = ValueNodeType.CONTAINER;
			}
		}
		return type;
	}
	
	/**
	 * Parse the map key from String to required type.
	 * It only support following types: String, integer, boolean, long, float, double.
	 * @param keyType class of key. If it is null, the String type will be returned.
	 * @param key
	 * @return
	 */
	private Object parseKey(Class<?> keyType, String key) {
		Object value;
		if(keyType == null) {
			value = new String(key);
		}
		else if(keyType.isAssignableFrom(String.class)) {
			value = new String(key);
		}
		else if(keyType.isAssignableFrom(Path.class)) {
			value = new Path(key);
		}
		else if((keyType == Integer.class) || (keyType == Integer.TYPE)) {
			value = Integer.parseInt(key);
		}
		else if((keyType == Boolean.class) || (keyType == Boolean.TYPE)) {
			value = Boolean.parseBoolean(key);
		}
		else if((keyType == Long.class) || (keyType == Long.TYPE)) {
			value =Long.parseLong(key);
		}
		else if((keyType == Float.class) || (keyType == Float.TYPE)) {
			value = Float.parseFloat(key);
		}
		else if((keyType == Double.class) || (keyType == Double.TYPE)) {
			value = Double.parseDouble(key);
		}
		else {
			throw new ConfigException("Not support class type in map key! class='" + keyType + "'");
		}
		
		return value;
	}
	
	
	private static boolean startWith(String keySuffix, String textValue) {
		int keySuffixLen = keySuffix.length();
		if(textValue != null && textValue.length() > keySuffixLen ) {
			if(keySuffix.equalsIgnoreCase(textValue.substring(0, keySuffixLen))) {
				return true;
			}
		}
		return false;	
	}


	private void parseAnnotations() {
		Class<?> clazz = beanClazz;

		do {
			for (Field field : clazz.getDeclaredFields()) {
				InstanceIn annoInstanceIn = field.getAnnotation(InstanceIn.class);
				if(annoInstanceIn != null) {
					if (Modifier.isStatic(field.getModifiers())) {
						throw new ConfigException("InstanceIn annotation is not supported on static fields: " + field);
					}
					Property prop = new Property(beanClazz, field);
					ValueNode valueNode = new ValueNode(ValueNodeType.INSTANCE, annoInstanceIn.path());
					properties.put(prop, valueNode);
					continue;
				}
				
				ResourceIn annoResourceIn = field.getAnnotation(ResourceIn.class);
				if(annoResourceIn != null) {
					if (Modifier.isStatic(field.getModifiers())) {
						throw new ConfigException("ResourceIn annotation is not supported on static fields: " + field);
					}
					Property prop = new Property(beanClazz, field);
					ValueNode valueNode = new ValueNode(ValueNodeType.RESOURCE, annoResourceIn.path());
					properties.put(prop, valueNode);
					continue;
				}
				
				SpaceIn annoSpackeIn = field.getAnnotation(SpaceIn.class);
				if(annoSpackeIn != null) {
					if (Modifier.isStatic(field.getModifiers())) {
						throw new ConfigException("SpaceIn annotation is not supported on static fields: " + field);
					}
					Property prop = new Property(beanClazz, field);
					ValueNode valueNode = new ValueNode(ValueNodeType.SPACE, annoSpackeIn.scope());
					properties.put(prop, valueNode);
					continue;
				}
				
				ContainerIn annoContainerIn = field.getAnnotation(ContainerIn.class);
				if(annoContainerIn != null) {
					if (Modifier.isStatic(field.getModifiers())) {
						throw new ConfigException("ContainerIn annotation is not supported on static fields: " + field);
					}
					Property prop = new Property(beanClazz, field);
					ValueNode valueNode = new ValueNode(ValueNodeType.CONTAINER, null);
					properties.put(prop, valueNode);
					continue;
				}

			}
			for (Method method : clazz.getDeclaredMethods()) {
				
				//1. Parse the DestoryMethod annotation
				DestroyMethod destroyMethod = method.getAnnotation(DestroyMethod.class);
				if(destroyMethod != null) {
					this.destroyMethod = method;
				}
				
				//2. Parse the DestoryMethod annotation
				InitMethod initMethod = method.getAnnotation(InitMethod.class);
				if(initMethod != null) {
					this.initMethod = method;
				}

				//3. Parse the InstanceIn, ResourceIn, SpaceIn Annotations
				Annotation anno = method.getAnnotation(InstanceIn.class);
				ValueNodeType type = null;
				String value = null;
				if(anno != null) {
					type = ValueNodeType.INSTANCE;
					value = ((InstanceIn)anno).path();
				}
				else {
					anno = method.getAnnotation(ResourceIn.class);
					if(anno != null) {
						type = ValueNodeType.RESOURCE;
						value = ((ResourceIn)anno).path();
					}
					else {
						anno = method.getAnnotation(SpaceIn.class);
						if(anno != null) {
							type = ValueNodeType.SPACE;
							value = ((SpaceIn)anno).scope();
						}
						else {
							anno = method.getAnnotation(ContainerIn.class);
							if(anno != null) {
								type = ValueNodeType.CONTAINER;
								value = null;
							}
						}
					}
				}
				
				if (anno != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanClazz))) {
					if (Modifier.isStatic(method.getModifiers())) {
						throw new ConfigException(anno.getClass().getName() + " annotation is not supported on static methods: " + method);
					}
					if (method.getParameterTypes().length == 0) {
						throw new ConfigException(anno.getClass().getName() + " annotation should be used on methods with actual parameters: " + method);
					}
					if(method.getName().startsWith("set") == false) {
						throw new ConfigException(anno.getClass().getName() + " annotation should be used on methods with 'set' start with its name." + method);
					}
					
					Property prop = new Property(beanClazz, method);
					ValueNode valueNode = new ValueNode(type, value);
					properties.put(prop, valueNode);
				}
				
			}

			clazz = clazz.getSuperclass();
		}
		while (clazz != null && clazz != Object.class);
		
	}
	

}
