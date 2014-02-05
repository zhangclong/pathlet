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

import com.google.code.pathlet.core.Path;
/**
 * 
 * When duplication resouce in same <code>ResourceFactory<code>, this exception will be raised.
 * 
 * @author Charlie Zhang
 *
 */
public class ResourceDuplicationException extends ResourceException {

	private static final long serialVersionUID = 8244992293820955400L;
	
	private Path resourcePath;

	public ResourceDuplicationException(Path resourcePath) {
		super();
		this.resourcePath = resourcePath;
	}

	public ResourceDuplicationException(String message, Throwable cause, Path resourcePath) {
		super(message, cause);
		this.resourcePath = resourcePath;
	}

	public ResourceDuplicationException(String message, Path resourcePath) {
		super(message);
		this.resourcePath = resourcePath;
	}

	public ResourceDuplicationException(Throwable cause, Path resourcePath) {
		super(cause);
		this.resourcePath = resourcePath;
	}

	public Path getResourcePath() {
		return resourcePath;
	}
	

}
