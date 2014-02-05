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
package com.google.code.pathlet.web.json;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.code.pathlet.config.convert.BeanInfoWrapper;
import com.google.code.pathlet.config.convert.Property;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.web.RequestProcessException;
import com.google.code.pathlet.web.RequestProcessor;

public class JsonRequestProcessor implements RequestProcessor {
	
	
	//Key is request parameter name, value is actionObj property name.
	private Map<String, String> parameterPropertyMap;
	
	private ObjectMapper objectMapper;
	
	
	public JsonRequestProcessor(Map<String, String> parameterPropertyMap,
				Map<Class<?>, JsonDeserializer<?>> deserializerMap) {
		this.objectMapper = new ObjectMapper();
		
		SimpleModule testModule = new SimpleModule("MyModule");

		if(deserializerMap != null && deserializerMap.size() > 0) {
			for(Map.Entry<Class<?>, JsonDeserializer<?>> entry : deserializerMap.entrySet()) {
				Class type = entry.getKey();
				JsonDeserializer serializer = entry.getValue();
				testModule.addDeserializer(type, serializer);
			}
			objectMapper.registerModule(testModule);
		}
		
		this.parameterPropertyMap = parameterPropertyMap;
	}
	

	public boolean process(Path requestPath, Object actionObj, HttpServletRequest request) 
			throws RequestProcessException 
	{
		
		Set<String> keys = parameterPropertyMap.keySet();
		
		try {
			BeanInfoWrapper beanWrapper = new BeanInfoWrapper(actionObj.getClass());
			for(String key : keys) {
				String parameterValue = request.getParameter(key);
				if(parameterValue != null) {
					String propertyName = parameterPropertyMap.get(key);

					Class<?> propType = beanWrapper.getPropertyType(propertyName);
					Object value = objectMapper.readValue(parameterValue, propType);
					beanWrapper.setProperty(propertyName, actionObj, value);
				}
			}
		} catch (Exception e) {
			throw new RequestProcessException(e);
		}
		
		
		return false;
	}



}
