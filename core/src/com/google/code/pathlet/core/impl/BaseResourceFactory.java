package com.google.code.pathlet.core.impl;

import java.util.List;

import com.google.code.pathlet.config.ResourceConfig;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.ResourceFactory;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.exception.ResourceException;

public abstract class BaseResourceFactory<T extends Resource> implements ResourceFactory<T> {
	
	private PathPattern pathPattern = DEFAULT_FILTER_MATCH_PATTERN;
	
	private PathletContainer container;
	
	private String defaultScope;
	
	public PathletContainer getContainer() {
		return this.container;
	}
	
	public void setContainer(PathletContainer container) {
		this.container = container;
	}

	public void setPathPattern(PathPattern pathPattern) {
		this.pathPattern = pathPattern;
	}

	public PathPattern getPathPattern() {
		return this.pathPattern;
	}
	
	public String getDefaultScope() {
		return defaultScope;
	}

	public void setDefaultScope(String defaultScope) {
		this.defaultScope = defaultScope;
	}
	
	public abstract T createResource(Path resourcePath) throws ResourceException;

	public abstract T createResource(ResourceConfig resourceConfig) throws ResourceException;


	
}
