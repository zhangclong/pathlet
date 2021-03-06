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

import com.google.code.pathlet.core.Path;

/**
 * 
 * 
 * 
 * @author Charlie Zhang
 *
 */
public class AdviceConfig {
	
	private String id;
	
	private Path advisorPath;
	
	private String adviceMethod;
	
	private PointcutConfig pointcut;
	
	public AdviceConfig(String id, Path advisorPath, String adviceMethod, PointcutConfig pointcut) {
		this.id = id;
		this.advisorPath = advisorPath;
		this.adviceMethod = adviceMethod;
		this.pointcut = pointcut;
	}
	
	public String getId() {
		return id;
	}
	
	public String getAdviceMethod() {
		return adviceMethod;
	}

	public Path getAdvisorPath() {
		return advisorPath;
	}

	public PointcutConfig getPointcut() {
		return pointcut;
	}

}
