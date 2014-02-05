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

import java.lang.reflect.Method;

import com.google.code.pathlet.core.ProceedingJoinPoint;
import com.google.code.pathlet.core.Resource;

/**
 * 
 * @author Charlie Zhang
 *
 */
public class ProceedingJoinPointImpl  implements ProceedingJoinPoint {

	private final Resource resource;
	
	private final Object target;
	
	private final Object targetProxy;
	
	private Object[] arguments;

	private final Method method;
	
	private Processor processor;
	
	public ProceedingJoinPointImpl(Resource resource, Object target, Object targetProxy, Method method, Object[] arguments) {
		this.target = target;
		this.targetProxy = targetProxy;
		this.method = method;
		this.arguments = arguments;
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public Object getTargetProxy() {
		return targetProxy;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public Object proceed() throws Throwable {
		return processor.proceed(this, this.arguments);
	}
	
	public Object proceed(Object[] args) throws Throwable {
		this.arguments = args;
		return proceed();
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public static interface Processor {
		Object proceed(ProceedingJoinPointImpl thisJoinpoint, Object[] args) throws Throwable ;
	}
	

}
