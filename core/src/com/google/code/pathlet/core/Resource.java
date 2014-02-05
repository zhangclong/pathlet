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

import com.google.code.pathlet.core.exception.ResourceInstanceException;

/**
 * 
 * Resource class is the definitions and configurations for one resource which mapped in one unique path.   
 * 
 * @author Charlie Zhang
 *
 */
public interface Resource {
	
	public ResourceFactory getFactory();
	
	public String getScope();
	
	public Path getPath();
	
	/**
	 * Create new Instance of this Resource, and invoke the initialize method if it exists.
	 * @param context
	 * @return
	 * @throws ResourceInstanceException
	 */
    public Object newInstance(InstanceSpace context) throws ResourceInstanceException;
	
	/**
	 * If the resource has predefined the destroy method, it will be invoke on target.
	 * @param target
	 */
	public void destroyInstance(Object target);
	
	/**
	 * Indicate whether this resource has destroy method and required to invoke the destroy method when the InstanceSpace flushed! 
	 * @return true the destroy method will be invoked, false no be invoked.
	 */
	public boolean destroyable();
	
}
