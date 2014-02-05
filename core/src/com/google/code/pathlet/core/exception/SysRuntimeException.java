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
 * The base pathful exception. All other exception are extended from it.
 * 
 * @author Charlie Zhang
 * @version 2.0
 */
public class SysRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 741199037822882250L;

	public SysRuntimeException() {
		super();
	}

	public SysRuntimeException(String message) {
		super(message);
	}
	
	public SysRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SysRuntimeException(Throwable cause) {
		super(cause);
	}

}