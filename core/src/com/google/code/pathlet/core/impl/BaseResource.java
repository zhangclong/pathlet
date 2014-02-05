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

import java.util.Iterator;
import java.util.List;

import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.ResourceFactory;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.ResourceInstanceProcessor;
import com.google.code.pathlet.core.exception.ResourceInstanceException;

/**
 * 
 * @author Charlie Zhang
 *
 */
public abstract class BaseResource implements Resource {
	
	private ResourceFactory<?> factory;
	
	protected BaseResource(final ResourceFactory<?> factory) {
		this.factory = factory;
	}

	public ResourceFactory<?> getFactory() {
		return factory;
	}

	public Object newInstance(InstanceSpace space) throws ResourceInstanceException {
		Object target;
		List<ResourceInstanceEvent> events = factory.getContainer().getInstanceEvents();
		if(events != null) {
			final Iterator<ResourceInstanceEvent> it = events.iterator();
			final Resource thisResource = this;
			final ResourceInstanceProcessor processor = new ResourceInstanceProcessor() {
				public Object process(InstanceSpace space) {
					if(it.hasNext()) {
						ResourceInstanceEvent event = it.next();
						return event.instanceTarget(this, space, thisResource);
					}
					else {
						return doInstanceTarget(space);
					}
				}
			};
			
			
			target = processor.process(space);
		}
		else {
			target = doInstanceTarget(space);
		}
		
		afterInstanceTarget(space, target);
		
		return target;
	}

	/**
	 * 
	 * Do actually target instance action.
	 * 
	 * @param context
	 * @return
	 */
	protected abstract Object doInstanceTarget(InstanceSpace context);
	
	/**
	 * Will invoke just after the target has been instanced.
	 * @param context
	 * @return
	 */
	protected abstract void afterInstanceTarget(InstanceSpace context, Object target);
	
}
