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

/**
 * Convert resourcePath to full java class name.
 * It will be used in  {@link BeanResourceFactory}
 * @author Charlie Zhang
 * @since 2011-2-16
 */
public interface BeanPathConverter {
	
	/**
	 * Convert resourcePath to full java class name.
	 * @param resourcePath
	 * @param prePath
	 * @param basePackage
	 * @return Java Class Name, the full class name. 
	 */
	String convert(Path resourcePath, Path prePath, String basePackage) throws ResourceAccessException;
	
}
