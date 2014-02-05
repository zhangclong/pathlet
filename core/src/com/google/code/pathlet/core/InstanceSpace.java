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
import com.google.code.pathlet.core.exception.ResourceDuplicationException;
import com.google.code.pathlet.core.exception.ResourceNotFoundException;


/**
 * API for accessing resource instance that
 * are currently associated with a particular lifecycle scope.
 * 
 * @author Charlie Zhang
 */
public interface InstanceSpace {
	
	
	/**
	 * Get PathletContainer of this space. 
	 * The {@link PathletContainer}.
	 * @return
	 */
	public PathletContainer getContainer();
	
	/**
	 * Change the current directory to dir.
	 * @param path
	 * @throws PathException
	 */
	public void setWorkingDirectory(Path path) throws PathException;
	
	/**
	 * retrun the path of the current working directory.
	 * 
	 * @return
	 */
	public Path getWorkingDirectory();
	
	
	/**
	 * Context has hierarchical relationship, this method return parent hierarchical one.
	 * If this class is root of hierarchy, it will return null. 
	 * @return 
	 */
	public InstanceSpace getParent();
	
	
	/**
	 * Add the instance into this InstanceSpace cache. 
	 * It add an target instance into the cache, and will not effect the corresponding path resource.
	 * 
	 * If already existed a target in the path, it will throw the ResourceDuplicationException.
	 * @param path
	 * @param target
	 * @throws ResourceDuplicationException
	 */
	public void addInstance(Path path, Object target) throws ResourceDuplicationException;
	
	/**
	 * Remove the instance from the InstanceSpace cache. 
	 * It only remove the instance from the cache. 
	 * so, when you invoke the getInstance() method for existent resource, the target instance could be instanced a new one from the resource. 
	 * 
	 * If the target not found in the path, the ResourceNotFoundException will be throws.
	 * @param path
	 * @throws ResourceNotFoundException
	 */
	public void removeInstance(Path path) throws ResourceNotFoundException;
	
	/**
	 * Get instance by path.
	 * The corresponding instance will be cached after first invoke this method by corresponding path. 
	 * Equals to invoke method: getTarget(path, true, false)
	 */
	public Object getInstance(Path path);
	
	/**
	 * Get instance by path. If setWorkingDirectory is true, setWorkingDirectory() method will be invoked and <br/>
	 *  the Working Directory will be set as path.getParent().
	 * @param path
	 * @param setWorkingDirectory
	 * @param thisScope get instance only for this scope.  If thisScope is false, it will search instance in parent context. 
	 * @return
	 */
	public Object getInstance(Path path, boolean setWorkingDirectory, boolean thisScope);

	/**
	 * Get the scope that this context object is associated with,
	 */
	public String getScope();
	
	/**
	 * Flush the all match instances which was cached in current context.
	 * And the additional operation is these all match instances will be call destroy method after removed from the cache.
	 * 
	 * If matchPattern is null, all cached instances will be removed and the destroy method of all this instance will be invoked.
	 * 
	 * @param mathPattern
	 */
	public void flush(PathPattern matchPattern);
	

}
