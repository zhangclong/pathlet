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
 * 
 * Exception in web widgets.
 * <code>com.google.code.newpath.web.widget</code>
 * 
 * @author Charlie Zhang
 * 
 */
public class WebWidgetException extends SysRuntimeException {
	
	private static final long serialVersionUID = 771334877377902873L;
	
	public WebWidgetException() {
		super();
	}

	public WebWidgetException(String message) {
		super(message);
	}
	
	public WebWidgetException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public WebWidgetException(Throwable cause) {
		super(cause);
	}

}