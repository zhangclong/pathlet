/*
 * Copyright 2010-2012 the original author or authors.
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
package com.google.code.pathlet.config.def;

import java.util.HashSet;
import java.util.Set;

import com.google.code.pathlet.config.anno.Pointcut;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.util.ValueUtils;

/**
 * 
 * @author Charlie Zhang
 *
 */
public class PointcutConfig {
	
	private String id;
	
	private String[] scopes;

	private PathPattern pathPattern;
	
	private String[] methods;
	
	private final Set<String> scopesSet;
	
	public PointcutConfig(Pointcut pointcutAnno) {
		if(ValueUtils.notEmpty(pointcutAnno.scopes())) {
			this.scopes = pointcutAnno.scopes().split(",");
		}
		
		String[] includes = {};
		if(ValueUtils.notEmpty(pointcutAnno.includes())) {
			includes = pointcutAnno.includes().split(",");
		}
		
		String[] excludes = {};
		if(ValueUtils.notEmpty(pointcutAnno.excludes())) {
			excludes = pointcutAnno.excludes().split(",");
		}
		this.pathPattern = new PathPattern(includes, excludes);
		
		if(ValueUtils.notEmpty(pointcutAnno.methods())) {
			this.methods = pointcutAnno.methods().split(",");
		}
		
		this.scopesSet = parseScopes(this.scopes);
	}
	
	public PointcutConfig(String id, String[] scopes, PathPattern pathPattern, String[] methods) {
		super();
		this.id = id;
		this.scopes = scopes;
		this.pathPattern = pathPattern;
		this.methods = methods;
		this.scopesSet = parseScopes(this.scopes);
	}
	
	private Set<String> parseScopes(String[] scopesArray) {
		Set<String> scopesSet;
		if(ValueUtils.notEmpty(scopesArray)) {
			scopesSet = new HashSet<String>(scopesArray.length);
			for(String scope : scopesArray) {
				scopesSet.add(scope);
			}
		}
		else {
			scopesSet = new HashSet<String>(0);
		}
		return scopesSet;
	}
	
	/**
	 * Decision a scope is in the cutpoint configuration.
	 * It will be return true, if the scopes hasn't be set in the cutpoint configuration.
	 * @param scope 
	 * @return true for found match scope, false for not match scope be found.
	 */
	public boolean isMatchByScope(String scope) {
		if(scopesSet.size() > 0) {
			return scopesSet.contains(scope);
		}
		else {
			return true;
		}
	}
	
	
	public boolean isMatchByPath(Path path) {
		return this.pathPattern.isMatch(path);
	}
	
	
	public String getId() {
		return id;
	}

	/**
	 * The InstanceSpace scopes to be effect on. It could be one or more string separated by comma.
	 * If it's empty, means all scopes is available.
	 */
	public String[] getScopes() {
		return scopes;
	}

	/** Ant like string to match path, to filter out the resources.*/
	public PathPattern getPathPattern() {
		return pathPattern;
	}

	/** Ant like string to match method name. could be one or more string, separated by comma. */
	public String[] getMethods() {
		return methods;
	}


}
