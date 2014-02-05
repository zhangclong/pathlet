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

import java.util.List;

import com.google.code.pathlet.config.ResourceConfig;
import com.google.code.pathlet.config.def.BeanResourceConfig;
import com.google.code.pathlet.config.impl.BeanResourceConfigHandler;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathClassLoader;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.ResourceFactory;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.exception.ResourceAccessException;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.core.exception.ResourceNotFoundException;
/**
 * 
 * @author Charlie Zhang 
 *
 */
public class BeanResourceFactory extends BaseResourceFactory<BeanResource> {

	public final static BeanPathConverter DEFAULT_PATH_CONVERTER = new DefaultPathConverter();
	
	// The start path will append before parse path. For instance: <br/>
	// If this argument is "/service", the all resource path which build from this factory will look like "/service/somepath".  
	private Path startPath;
	
	//The base java package to find bean class. This factory will find the classes under the descendants of this package.
	private String basePackage;
	
	// A customize class to convert from resourcePath to javaBean Define
	private BeanPathConverter pathConverter = DEFAULT_PATH_CONVERTER;
	
	public Path getStartPath() {
		return startPath;
	}

	public void setStartPath(Path startPath) {
		this.startPath = startPath;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public BeanPathConverter getPathConverter() {
		return pathConverter;
	}

	public void setPathConverter(BeanPathConverter pathConverter) {
		this.pathConverter = pathConverter;
	}

	public BeanResource createResource(Path resourcePath) {
		
		//Created the new BeanResource by resourcePath
		try {
			String className = pathConverter.convert(resourcePath, startPath, basePackage);
			BeanResourceConfig config = new BeanResourceConfig();
			config.setPath(resourcePath);
			config.setClassName(className);
			
			Class<?> beanClazz = getContainer().getPathClassLoader().loadClass(config.getPath(), config.getClassName());
			if(beanClazz == null) {
				throw new ResourceNotFoundException("Failed to find resource path=" + resourcePath, resourcePath);
			}
			
			BeanResourceConfigHandler configHandler = new BeanResourceConfigHandler(config, this.getDefaultScope(), beanClazz);

			return new BeanResource(this, configHandler);
		}
		catch(Exception e) {
			throw new ResourceAccessException("Failed to get resource path=" + resourcePath, e, resourcePath);
		}
	}
	
	
	public BeanResource createResource(ResourceConfig resourceConfig) throws ResourceException { 
		try {
			BeanResourceConfig config = (BeanResourceConfig)resourceConfig;
			
			//Create the new className by converting from the path of resourceConfig
			if(config.getClassName() == null) {
				String className = pathConverter.convert(config.getPath(), startPath, basePackage);
				config.setClassName(className);
			}
			
			Class<?> beanClazz = getContainer().getPathClassLoader().loadClass(config.getPath(), config.getClassName());
			if(beanClazz == null) {
				throw new ResourceNotFoundException("Failed to find resource path=" + config.getPath(), config.getPath());
			}
			
			BeanResourceConfigHandler configHandler = new BeanResourceConfigHandler(config, getDefaultScope(), beanClazz);

			return new BeanResource(this, configHandler);
		}
		catch(Exception e) {
			throw new ResourceAccessException("Failed to get resource path=" + resourceConfig.getPath(), 
					e, resourceConfig.getPath());
		}
	}

	
}
