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
package com.google.code.pathlet.core.exception;

import com.google.code.pathlet.core.Resource;
/**
 * 
 * @author Charlie Zhang
 *
 */
public class ResourceInstanceException extends ResourceException {

	private static final long serialVersionUID = -3767318767298470411L;

	private Resource resource;
	
	public ResourceInstanceException(Resource resource) {
		super();
		this.resource = resource;
	}

	public ResourceInstanceException(String message, Resource resource) {
		super(message);
		this.resource = resource;
	}

	public ResourceInstanceException(String message, Throwable cause, Resource resource) {
		super(message, cause);
		this.resource = resource;
	}

	public ResourceInstanceException(Throwable cause, Resource resource) {
		super(cause);
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}
	
	

}
