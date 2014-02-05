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
package com.google.code.pathlet.web.ognl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ognl.DefaultClassResolver;
import ognl.OgnlException;

import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.web.RequestProcessException;
import com.google.code.pathlet.web.RequestProcessor;
import com.google.code.pathlet.web.ognl.impl.ExtensibleConverter;
import com.google.code.pathlet.web.ognl.impl.OgnlUtil;

public class OgnlRequestProcessor implements RequestProcessor {
	
	private OgnlUtil ognlUtil;
	
	public OgnlRequestProcessor() {
		this(null);
	}
	
	public OgnlRequestProcessor(Map<Class, OgnlRequestConverter> requestConverters ) {
		this.ognlUtil = new OgnlUtil(new ExtensibleConverter(requestConverters), new DefaultClassResolver());
	}
	

	public boolean process(Path requestPath, Object actionObj, HttpServletRequest request)
			throws RequestProcessException {
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			map.put(name, request.getParameterValues(name));
		}

		try {
			ognlUtil.setProperties(map, actionObj);
		} catch (OgnlException e) {
			throw new RequestProcessException(e);
		}
		
		return false;
	}



}
