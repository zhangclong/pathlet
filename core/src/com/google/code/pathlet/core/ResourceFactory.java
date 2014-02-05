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

import java.util.List;

import com.google.code.pathlet.config.ResourceConfig;
import com.google.code.pathlet.core.exception.ResourceException;

/**
 * <p>
 * Definition of an interface for Resource Factory.
 * </p>
 * 
 * <p>
 * <p>
 * In principle, <em>ResourceFactory</em> doesn't create the actually instance
 * of resource. It only create the <code>{@link Resource}</code> which is
 * the template for creation new target(instance).<br/> 
 * The instanceTarget method of
 * <code>{@link Resource}</code> provides the only way to create the resource
 * target(instance).
 * </p>
 * <p>
 * And what's the difference between the resource and the resource target?
 * Please reference the <code>{@link Resource}</code>.
 * </P>
 * 
 * @author Charlie Zhang
 * @since 2012-11-29
 * @param <T>
 */
public interface ResourceFactory<T extends Resource> {
	
	public PathPattern DEFAULT_FILTER_MATCH_PATTERN = new PathPattern(new String[]{"/**/*"});
	
	/**
	 * get the parent PathletContainer.
	 */
	public PathletContainer getContainer();
	
	/**
	 * Set the parent PathletContainer;
	 * @param container
	 */
	public void setContainer(PathletContainer container);
	
	/**
	 * Get PathPattern to filter the match resources by paths.
	 * In generally, it could return pattern same like <code>/&#42;&#42;/&#42;Service</code>. 
	 * 
	 * @return
	 */
	public PathPattern getPathPattern();
	
	/**
	 * Set PathPattern
	 * @param pathPattern
	 */
	public void setPathPattern(PathPattern pathPattern);

	/**
	 * 
	 * Default resource scope for a none scope specified resource.
	 * 
	 * @return
	 */
	public String getDefaultScope();
	
	/**
	 * set the default scope.
	 * @param defaultScope
	 */
	public void setDefaultScope(String defaultScope);
	
	/**
	 * 
	 * Get Resource by path.
	 * 
	 * @param fullPath
	 * @return
	 * @throws ResourceException
	 */
	public T createResource(Path resourcePath) throws ResourceException;
	

	/**
	 * In generally the resources will be generated by ResourceFactory internal
	 * implementation. But in some customized requirement, some specific
	 * Resource could be added manually. This method provide a interface to add
	 * resource manually.
	 * 
	 * @param resource
	 * @return the previous value associated with <tt>resource.path</tt>, or
	 *         <tt>null</tt> if there was no mapping for <tt>resource.path</tt>.
	 * @throws ResourceException
	 */
	public T createResource(ResourceConfig resourceConfig)
			throws ResourceException;

}
