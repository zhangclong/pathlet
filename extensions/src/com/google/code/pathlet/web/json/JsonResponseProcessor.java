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

import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.web.ResponseProcessor;

public class JsonResponseProcessor implements ResponseProcessor {
	
	private ObjectMapper objectMapper = null;
	
	private SimpleModule module = new SimpleModule("MyModule");
	
	public JsonResponseProcessor() { }
	
	public void setSerializers(Map<String, JsonSerializer> serializers) {
		for(Map.Entry<String, JsonSerializer> entry : serializers.entrySet()) {
			Class clazz;
			try {
				clazz = ClassUtils.getDefaultClassLoader().loadClass(entry.getKey());
			} catch (ClassNotFoundException e) {
				throw new ConfigException("Failed to set serializer for type=" + entry.getKey(), e);
			}
			addSerializer(clazz, entry.getValue());
		}
	}
	
	public <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> serializer) {
		module.addSerializer(type, serializer);
	}
	
	public <T> void addSerializer(JsonSerializer<T> serializer) {
		module.addSerializer(serializer);
	}

	public boolean processResult(Path requestPath, Object returnResult, HttpServletResponse response)
			throws JsonResourceIOException 
	{
		response.setContentType("text/plain");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		
		try {
			
			if(returnResult != null) {
				if(String.class.isAssignableFrom(returnResult.getClass())) {
					//If returnResult is String, it will not convert into JSON, but directly write into response writer.
					response.getWriter().write((String)returnResult);
				}
				else {
					writeResult(response.getWriter(), returnResult);
				}
			}
			
			response.flushBuffer();
		} catch (Exception e) {
			throw new JsonResourceIOException(e);
		}
		
		return false;
	}
	
	public String writeResultAsString(Object resultObj) throws JsonResourceIOException {
		
		try {
			return getMapper().writeValueAsString(resultObj);
		} catch (JsonProcessingException e) {
			throw new JsonResourceIOException(e);
		}
	}
	
	public void writeResult(Writer writer, Object resultObj) throws JsonResourceIOException {
		
		try {
			getMapper().writeValue(writer, resultObj);
		} catch (Exception e) {
			throw new JsonResourceIOException(e);
		}
	}
	
	private ObjectMapper getMapper() {
		if(this.objectMapper == null) {
			this.objectMapper = new ObjectMapper();
			this.objectMapper.registerModule(this.module);
		}
		return this.objectMapper;
	}
	
}
