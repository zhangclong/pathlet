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
package com.google.code.pathlet.core;

import java.lang.reflect.Method;



/**
 * This interface represents a generic runtime joinpoint (in the AOP
 * terminology).
 *
 * <p>A runtime joinpoint is an <i>event</i> that occurs on a static
 * joinpoint (i.e. a location in a the program). For instance, an
 * invocation is the runtime joinpoint on a method (static joinpoint).
 * The static part of a given joinpoint can be generically retrieved
 * using the {@link #getStaticPart()} method.
 *
 * <p>In the context of an interception framework, a runtime joinpoint
 * is then the reification of an access to an accessible object (a
 * method, a constructor, a field), i.e. the static part of the
 * joinpoint. It is passed to the interceptors that are installed on
 * the static joinpoint.
 * 
 * @author Charlie Zhang
 * */
public interface ProceedingJoinPoint {
	
	/**
	 * Get the arguments as an array object. It is possible to change element
	 * values within this array to change the arguments.
	 * 
	 * @return the argument of the invocation
	 */
	Object[] getArguments();
	
	/**
	 * Gets the method being called.
	 * 
	 * <p>
	 * This method is a frienly implementation of the
	 * {@link JoinPoint#getStaticPart()} method (same result).
	 * 
	 * @return the method being called.
	 */
	Method getMethod();
	
	/**
	 * Gets intercepted object
	 * @return
	 */
    Object getTarget();
    
	/**
	 * Return the proxy object for target.
	 * @return
	 */
	Object getTargetProxy();
	
	/**
	 * Return the corresponding Resource of target
	 */
	Resource getResource();

	/**
	 * Proceed with the next advice or target method invocation
	 * 
	 * @return the advice or target method return value.
	 * 
	 * @throws Throwable if the joinpoint throws an exception.
	 */
	Object proceed() throws Throwable;
	
	
    /**
     * Proceed with the next advice or target method invocation. The difference from the proceed() is 
     * this method add Object[] arguments which will transfer into target method arguments.
     * Say in other words, the arguments value of target method could be changed in advice method at runtime.
     * 
     * @param args
     * @return
     * @throws Throwable
     */
    public Object proceed(Object[] args) throws Throwable;
	
}
