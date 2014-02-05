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
package com.google.code.pathlet.config.def;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.ProceedingJoinPoint;
import com.google.code.pathlet.core.impl.ProceedingJoinPointImpl;
import com.google.code.pathlet.util.AntPathMatcher;
import com.google.code.pathlet.util.PathMatcher;
import com.google.code.pathlet.util.ValueUtils;

/**
 * 
 * Represent advice(method) and pointcut pair.
 * 
 * @author Charlie Zhang
 *
 */
public class AdviceConfigHandler {
	
	private final AdviceExecutor advice;
	
	private final PointcutConfig pointcut;

	private final PathMatcher methodMatcher;
	
	private final Path advisorPath;
	
	public AdviceConfigHandler(Path advisorPath, Object advisorObject, AdviceConfig config) {
		this.advisorPath = advisorPath;
		this.methodMatcher = new AntPathMatcher();
		this.pointcut = config.getPointcut();
		
		Class<?> clazz = advisorObject.getClass();
		Method adviceMethod;
		try {
			adviceMethod = clazz.getMethod(config.getAdviceMethod(), ProceedingJoinPoint.class);
		} catch (Exception e) {
			throw new ConfigException("Failed to get advice method='" + config.getAdviceMethod() + "' in class='" + clazz.getCanonicalName() + "'" , e);
		} 
		
		this.advice = new AdviceExecutor(advisorObject, adviceMethod);
		
	}

	public AdviceConfigHandler(Path advisorPath, Object advisorObject, Method adviceMethod, PointcutConfig pointcut) {
		this.advisorPath = advisorPath;
		this.methodMatcher = new AntPathMatcher();
		this.pointcut = pointcut;
		this.advice = new AdviceExecutor(advisorObject, adviceMethod);
	}
	


	public AdviceExecutor getAdvice() {
		return advice;
	}

	public PointcutConfig getPointcut() {
		return pointcut;
	}
	
	public boolean isMatchByPath(String scope, Path path) {
		return (pointcut.isMatchByScope(scope) && pointcut.isMatchByPath(path));
	}
	
	public boolean isMatchByMethod(Method method) {
		boolean match = false;
    	//find include match 
    	for(String methodName : pointcut.getMethods()) {
    		match = methodMatcher.match(methodName, method.getName());
    		if(match == true) { break; }
    	}
    	
    	return match;
	}
	
	
	
	public Path getAdvisorPath() {
		return advisorPath;
	}

	/**
	 * 
	 * Runtime advice instance, it will store references for advice class instance and method
	 * 
	 * @author Charlie Zhang
	 *
	 */
	public static class AdviceExecutor {
		
		//Aspect class instance which contains the advice method
		private Object advisorObject;
		
		//Advice method
		private Method adviceMethod;
		
		public AdviceExecutor(Object advisorObject, Method adviceMethod) {
			this.advisorObject = advisorObject;
			this.adviceMethod = adviceMethod;
		}

		
		public Object execute(ProceedingJoinPoint joinPoint) 
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return adviceMethod.invoke(advisorObject, joinPoint);
		}

	}
	
}
