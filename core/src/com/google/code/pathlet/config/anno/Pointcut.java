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
package com.google.code.pathlet.config.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Charlie Zhang
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pointcut {
	
	/**
	 * The InstanceSpace scopes to be effect on. It could be one or more string separated by comma.
	 * If it's empty, means all scopes is available.
	 */
	String scopes() default "";
	
	/** Ant like string to match path. could be one or more string, separated by comma. */
	String includes();

	/** Ant like string to exclude matching path. Could be one or more string, separated by comma. */
	String excludes() default "";
	
	/** Ant like string to match method name. could be one or more string, separated by comma. */
	String methods() default "*";

}
