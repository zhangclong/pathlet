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
package com.google.code.pathlet.web.exception;


import com.google.code.pathlet.core.exception.SysRuntimeException;


/**
 * Throws when the web context or related components initialized failed.
 * 
 * @author Charlie Zhang
 * 
 */
public class WebContextInitialException extends SysRuntimeException {
	
	private static final long serialVersionUID = 771334877377902873L;
	
	public WebContextInitialException() {
		super();
	}

	public WebContextInitialException(String message) {
		super(message);
	}
	
	public WebContextInitialException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public WebContextInitialException(Throwable cause) {
		super(cause);
	}

}