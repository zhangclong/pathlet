package com.google.code.pathlet.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.code.pathlet.config.convert.CollectionFactory;
import com.google.code.pathlet.config.convert.Property;
import com.google.code.pathlet.config.convert.ValueNode;
import com.google.code.pathlet.config.impl.BeanResourceConfigHandler;
import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.ResourceInstanceProcessor;
import com.google.code.pathlet.core.impl.BeanResource;
import com.google.code.pathlet.util.PathletUtils;

public class ConfigInstanceEvent implements ResourceInstanceEvent {
	
	public Object instanceTarget(ResourceInstanceProcessor processor, InstanceSpace space, Resource resource) {
		
		Object ret = processor.process(space);
		
		weaveProperties(space, ret, resource);
		
		return ret;
	}

	public void flush(String scope, PathPattern matchPattern) {
		//This ResourceInstanceEvent has no cached object in it. 
		//Do nothing in this callback method.
	}

	protected void weaveProperties(InstanceSpace space, Object target, Resource resource) {
		
		if(resource instanceof BeanResource) {
			BeanResource beanResource = (BeanResource)resource;
			BeanResourceConfigHandler configHandler = beanResource.getConfigHandler();
			if(configHandler.getProperties() == null) {
				//ignore for none configuration resource
				return;
			}
			
			Map<Property, ValueNode> properties = configHandler.getProperties();
			
			for( Map.Entry<Property, ValueNode> entry : properties.entrySet()) {

				Property prop = entry.getKey();
				ValueNode valueNode = entry.getValue();

				Object propValue = getNodeValue(space, resource, valueNode);
				
				prop.setProperty(target, propValue);
			}
		
		}

	}
	
	private Object getNodeValue(InstanceSpace space,  Resource resource, ValueNode valueNode) throws ConfigException {

		Object value;
		switch(valueNode.getType()) {
		case COLLECTION:
			Collection<ValueNode> listNodes = valueNode.getChildren();
			int listSize = listNodes.size();
			Collection collValue = CollectionFactory.createCollection(valueNode.getValueType(), listSize);		
			for(ValueNode childNode : listNodes) {
				Object listElement = getNodeValue(space, resource, childNode);
				collValue.add(listElement);
			}
			value = collValue;
			break;
		case MAP:
			Collection<ValueNode> mapNodes = valueNode.getChildren();
			int mapSize = mapNodes.size();
			Map mapValue = CollectionFactory.createMap(valueNode.getValueType(), mapSize);	
			for(ValueNode childNode : mapNodes) {
				Object key = childNode.getKey();
				Object element = getNodeValue(space, resource, childNode);
				mapValue.put(key, element);
			}
			value = mapValue;
			break;
		case INSTANCE: 	
			value = PathletUtils.getInstance((String)valueNode.getValue(), resource, space);
			if(value == null) {
				throw new ConfigException("Failed to get instance by path: '" + (String)valueNode.getValue() + "'");
			}
			break;
		case RESOURCE:
			value = PathletUtils.getResource((String)valueNode.getValue(), resource, space);
			if(value == null) {
				throw new ConfigException("Failed to get Resource by path: '" + (String)valueNode.getValue() + "'");
			}
			break;
		case SPACE:
			value = PathletUtils.getSpace((String)valueNode.getValue(), space);
			if(value == null) {
				throw new ConfigException("Failed to get InstanceSpace from the name: '" + (String)valueNode.getValue() + "'");
			}
			break;
		case CONTAINER:
			value = space.getContainer();
			if(value == null) {
				throw new ConfigException("Failed to get PathletContainer instance");
			}
			break;
		case STRING: 
		case BOOLEAN:
		case INTEGER: 
		case LONG:
		case FLOAT:
		case DOUBLE:
		case PATH:
		case MATCH_PATTERN:
			value = valueNode.getValue();
			break;	
		default: 
			throw new ConfigException("Not support the value type: " + valueNode.getType());
		}
		return value;
	}
	

}
