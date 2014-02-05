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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.SpaceInstanceEvent;
import com.google.code.pathlet.core.exception.PathException;
import com.google.code.pathlet.core.exception.ResourceDuplicationException;
import com.google.code.pathlet.core.exception.ResourceInstanceException;
import com.google.code.pathlet.core.exception.ResourceNotFoundException;
import com.google.code.pathlet.util.ValueUtils;

/**
 * Default implementation of the interface InstanceSpace.
 * It will cache each target instance which initialized in getTarget(...) method. 
 * 
 * @author Charlie Zhang
 *
 */
public class DefaultInstanceSpace implements InstanceSpace {
	
	private InstanceSpace parentSpace;
	
	private String scope;
	
	private PathletContainer container;
	
	private List<SpaceInstanceEvent> events;
	
	private Path workingDirectory;
	
	private Map<Path, Object> targetCache;
	
	private Map<Path, Resource> destroableResource; //Store the resource can destoried!

	public DefaultInstanceSpace(InstanceSpace parentSpace, String lifecycleScope, PathletContainer container, List<SpaceInstanceEvent> events) {
		this.parentSpace = parentSpace;
		this.scope = lifecycleScope;
		this.container = container;
		this.events = events;
		this.targetCache = new HashMap<Path, Object>();
		this.destroableResource =  new HashMap<Path, Resource>();
	}
	
	public InstanceSpace getParent() {
		return parentSpace;
	}

	public PathletContainer getContainer() {
		return container;
	}


	public String getScope() {
		return scope;
	}

	public synchronized void addInstance(Path path, Object instance) throws ResourceDuplicationException {
		if(this.targetCache.containsKey(path)) {
			throw new ResourceDuplicationException("Already exists same path target in InstanceSpace!", path);
		}
		
		this.targetCache.put(path, instance);
	}
	
	public synchronized void removeInstance(Path path) throws ResourceNotFoundException {
		if(this.targetCache.containsKey(path) == false) {
			throw new ResourceNotFoundException("Could not found the target to be removed in the InstanceSpace!", path);
		}
		
		this.targetCache.remove(path); //Remove of the target instance
		
		//Remove the destroyable resource cache, if the target is intance from the resource and the resource is destroyable.
		if(this.destroableResource.containsKey(path)) {
			this.destroableResource.remove(path);
		}
		
	}
	
	public Object getInstance(Path path) {
		return getInstance(path, true, false);
	}
	
	public synchronized final Object getInstance(Path path, boolean setWorkingDirectory, boolean thisScopeOnly) {	
		
		if(events != null) {
			for(SpaceInstanceEvent event : events) {
				event.beforeGet(this, path);
			}
		}
		
		//Try get the match path from aliases definition
		Map<Path, Path> aliases = container.getAliases();
		Path targetPath = null;
		if(aliases!= null) {
			targetPath = container.getAliases().get(path);
		}
		if(targetPath == null) {
			targetPath = path;
		}
		
		Object target = this.targetCache.get(targetPath);
		if(target == null) {

			Path fullPath;
			if(targetPath.isAbsolute() == false) {
				fullPath = new Path(workingDirectory, targetPath.getFullPath());
			}
			else {
				fullPath = targetPath;
			}
			
			Resource resource = container.getResource(fullPath);
			
			if(resource != null) { 
				if(scope.equals(resource.getScope())) {
					target = resource.newInstance(this);
					
					//Only cache this scope target
					this.targetCache.put(targetPath, target);
					
					//If the resource is destroyable, put it into the destroableResource cache
					if(resource.destroyable()) {
						this.destroableResource.put(targetPath, resource);
					}
				}
				else if(thisScopeOnly == false){
					if(parentSpace != null) {
						target = parentSpace.getInstance(fullPath, setWorkingDirectory, thisScopeOnly);
					}
					else {
						throw new ResourceInstanceException("Failed to find the match InstanceSapce with scope=" + scope + ")", resource);
					}
				}
			}
		}
		
		if(setWorkingDirectory) {
			Path parent = targetPath.getParent();
			if(parent != null && (!parent.equals(workingDirectory))) {
				this.workingDirectory = parent;
			}
		}
		
		if(events != null) {
			for(SpaceInstanceEvent event : events) {
				event.afterGet(this, path, target);
			}
		}
		
		return target;
	}

	public synchronized Path getWorkingDirectory() {
		return workingDirectory;
	}

	public synchronized void setWorkingDirectory(Path path) throws PathException {
		this.workingDirectory = path;
	}
	
	/**
	 * Flush the matching resource target in this context.
	 * If mathPattern is null, all target will be flushed. 
	 * Flush operation will remove all cached target in this context. 
	 * But the remove target will not be created and initialized, until the <code>getTarget</code> method invoked. 
	 * 
	 */
	public synchronized void flush(PathPattern mathPattern) {
		
		if(mathPattern != null) {
			Iterator<Map.Entry<Path, Object>> it = targetCache.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Path, Object> entry = it.next();
				Path path = entry.getKey();
				if(mathPattern.isMatch(path)) {
					
					//If the resource is destroyable, the destroy method will be invoked in here.
					Resource resource = destroableResource.get(path);
					if(resource != null) {
						resource.destroyInstance(entry.getValue());
						destroableResource.remove(path);
					}
					
					//Remove the cached target.
					//To avoid the ConcurrentModificationException, must use Iterator.remove(), not the Map.remove(key).
					it.remove();
				}
			}
		}
		else {
			//Invoke the destroy method one by one.
			Iterator<Map.Entry<Path, Resource>> destroyIt = destroableResource.entrySet().iterator();
			while(destroyIt.hasNext()) {
				Map.Entry<Path, Resource> entry = destroyIt.next();
				Resource resource = entry.getValue();
				Path path = entry.getKey();
				resource.destroyInstance(targetCache.get(path));
			}
			
			//Clear all cache
			destroableResource.clear();
			targetCache.clear();
		}

	}

	protected Resource getResource(Path path) {
		Path fullPath;
		if(path.isAbsolute() == false) {
			fullPath = new Path(workingDirectory, path.getFullPath());
		}
		else {
			fullPath = path;
		}
		
		return container.getResource(fullPath);
	}

}
