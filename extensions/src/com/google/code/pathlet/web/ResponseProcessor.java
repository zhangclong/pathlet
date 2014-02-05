/*
 * Copyright 2010-2011 the original author or authors.
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
package com.google.code.pathlet.web;

import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.web.json.JsonResourceIOException;

/**
 * Proceed return result object and HttpServletResponse object.
 * @author Charlie Zhang
 *
 */
public interface ResponseProcessor {
	
	/**
	 * 
	 * @param requestPath
	 * @param returnResult
	 * @param response
	 * @return true: Stop the next ResponseProcessor processResult method invocation.
	 * @throws JsonResourceIOException
	 */
	boolean processResult(Path requestPath, Object returnResult, HttpServletResponse response) throws ResourceException;
	
	/**
	 * Convert the resultObj as String value.
	 * @param resultObj
	 * @return
	 * @throws JsonResourceIOException
	 */
	String writeResultAsString(Object resultObj) throws JsonResourceIOException;
	
	/**
	 * Convert resultObj as Writer stream.
	 * @param writer
	 * @param resultObj
	 * @throws JsonResourceIOException
	 */
	void writeResult(Writer writer, Object resultObj) throws JsonResourceIOException;
	
}
