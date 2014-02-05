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
package com.google.code.pathlet.core;

import com.google.code.pathlet.core.exception.PathException;
import com.google.code.pathlet.util.AntPathMatcher;
import com.google.code.pathlet.util.PathMatcher;
import com.google.code.pathlet.util.ValueUtils;

/**
 * 
 * Match pattern for Path. <br/>
 * 
 * includePaths will be used to find the match paths.
 * excludePaths will be used to exclude the match paths which match includePaths.
 * 
 * Each match pattern is ant like path pattern.
 * 
 * @author Charlie Zhang
 * 
 */
public class PathPattern {
	
	private final String[] includes;
	
	private final String[] excludes;
	
	private final PathMatcher pathMatcher = new AntPathMatcher();
	
	public PathPattern(String[] includes, String[] excludes) {
		this.includes = includes;
		this.excludes = excludes;
	}
	
	public PathPattern(String[] includes) {
		this(includes, null);
	}

	public String[] getIncludes() {
		return includes;
	}

	public String[] getExcludes() {
		return excludes;
	}
	
	public boolean match(String targetString) {
		
		if(ValueUtils.isEmpty(includes)) {
			throw new PathException("Must provide one validate path matchPattern in includePaths!", null);
		}
		
    	boolean match = false;
    	//find include match 
    	for(String include : includes) {
    		match = pathMatcher.match(include, targetString);
    		if(match == true) { break; }
    	}
    	
    	if(match == true && excludes != null) {
    		for(String exclude : excludes) {
    			boolean excludeMatch = pathMatcher.match(exclude, targetString);
    			if(excludeMatch == true) { 
    				match = false;
    				break; 
    			}
    		}
    	}
    	
    	return match;
	}
	
	public boolean isMatch(Path targetPath) {
		return match(targetPath.getFullPath());
	}
	
}
