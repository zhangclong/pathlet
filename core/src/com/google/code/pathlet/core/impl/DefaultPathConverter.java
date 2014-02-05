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
package com.google.code.pathlet.core.impl;

import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.exception.ResourceAccessException;
import com.google.code.pathlet.core.exception.ResourceNotFoundException;
/**
 * 
 * @author Charlie Zhang
 *
 */
public class DefaultPathConverter implements BeanPathConverter {

	public String convert(Path resourcePath, Path startPath, String basePackage) throws ResourceAccessException {
		String prePathStr = startPath.getFullPath();
		if(prePathStr.endsWith("/") == false) {
			prePathStr = prePathStr + "/";
		}
		
		//Validate the resourcePath is start with prePath
		if(resourcePath.getDirectory().indexOf(prePathStr) != 0) {
			throw new ResourceAccessException("Failed to find resource path=" 
						+ resourcePath + ", The path must be start with '" + prePathStr + "'", resourcePath);
		}
		
		return convertToClassName(resourcePath, prePathStr, basePackage);
	}
	
	
	protected String convertToClassName(Path resourcePath, String prePathStr, String basePackage) {
		String relativePackage = (resourcePath.getDirectory().substring(prePathStr.length())).replace(Path.SEPARATOR, '.');
		return basePackage + '.' + relativePackage + resourcePath.getNameWithoutSuffix();
	}

}
