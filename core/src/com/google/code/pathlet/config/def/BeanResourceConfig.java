package com.google.code.pathlet.config.def;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.code.pathlet.config.ResourceConfig;
import com.google.code.pathlet.core.Path;

public class BeanResourceConfig implements ResourceConfig {
	
	private Path path;
	
	private String className;
	
	private String scope;
	
	private String initMethod;
	
	private String destroyMethod;
	
	private boolean initInstance = false;
	
	/**
	 * To complete parse the bean properties, the bean properties type must be retrieved. 
	 * The json define is the part of the information, so we just store the JsonNode from json tree, 
	 * and parse the properties configuration in <code>BeanResource</code> creating phase.
	 */
	private JsonNode properties;

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public boolean isInitInstance() {
		return initInstance;
	}

	public void setInitInstance(boolean initInstance) {
		this.initInstance = initInstance;
	}

	public String getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	/**
	 * To complete parse the bean properties, the bean properties type must be retrieved. 
	 * The json define is the part of the information, so we just store the JsonNode from json tree, 
	 * and parse the properties configuration in <code>BeanResource</code> creating phase.
	 */
	public JsonNode getProperties() {
		return properties;
	}

	/**
	 * To complete parse the bean properties, the bean properties type must be retrieved. 
	 * The json define is the part of the information, so we just store the JsonNode from json tree, 
	 * and parse the properties configuration in <code>BeanResource</code> creating phase.
	 */
	public void setProperties(JsonNode properties) {
		this.properties = properties;
	}

}
