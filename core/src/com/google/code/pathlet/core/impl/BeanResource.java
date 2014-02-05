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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.config.ResourceConfig;
import com.google.code.pathlet.config.anno.SpaceIn;
import com.google.code.pathlet.config.anno.Lifecycle;
import com.google.code.pathlet.config.anno.ResourceIn;
import com.google.code.pathlet.config.anno.InstanceIn;
import com.google.code.pathlet.config.def.BeanResourceConfig;
import com.google.code.pathlet.config.impl.BeanResourceConfigHandler;
import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.exception.ResourceAccessException;
import com.google.code.pathlet.core.exception.ResourceInstanceException;
import com.google.code.pathlet.core.exception.SysRuntimeException;
import com.google.code.pathlet.util.ReflectionUtils;
import com.google.code.pathlet.util.ValueUtils;

/**
 * 
 * @author Charlie Zhang
 *
 */
public class BeanResource extends BaseResource {
	
	private Path path;
	
	private String scope;
	
	private String className;
	
	private Class<?> beanClazz;
	
	//private boolean destroyable;
	
	//用来存储配置信息，如ContextIn, TargetIn, ResourceIn, DestroyMethod, initMehtod 等, 这些信息可以来自配置文件，也可以是Annotation。
    //这样储存主要用于统一配置的程序处理入口，同时也减少多次重复分析的消耗，加速多次处理的速度。
    //属性的设置..., 所有配置的
    //AOP部分的加载和初始化
	private BeanResourceConfigHandler configHandler;
	
	public BeanResource(BeanResourceFactory creator, BeanResourceConfigHandler configHandler) throws ResourceInstanceException {
		super(creator);
		this.configHandler = configHandler;
		
		this.path = configHandler.getPath();
		this.className = configHandler.getClassName();
		this.beanClazz = configHandler.getBeanClazz();
		this.scope = configHandler.getScope();
		//this.destroyable = (configHandler.getDestroyMethod() != null); 

	}
	
	
	public String getScope() {
		return this.scope;
	}

	public Path getPath() {
		return this.path;
	}
	
	public String getClasName() {
		return this.className;
	}
	
	public Class<?> getBeanClazz() {
		return this.beanClazz;
	}
	
	public BeanResourceConfigHandler getConfigHandler() {
		return configHandler;
	}
	
	public boolean destroyable() {
		return (configHandler.getDestroyMethod() != null);
	}

	public void destroyInstance(Object target) {
		if(configHandler.getDestroyMethod() != null) {
			try {  
				configHandler.getDestroyMethod().invoke(target);  
			} 
			catch (Exception e) {
				String message = "Failed execution the destroyMethod " + configHandler.getDestroyMethod() + "() in the class '" + target.getClass().getCanonicalName() + "'";
				if(e.getCause() == null) {
					throw new ConfigException(message, e);
				}
				else {
					throw new ConfigException(message, e.getCause());
				}
			}
		}
	}

	protected Object doInstanceTarget(InstanceSpace context)  {
		try {
			return beanClazz.newInstance();
		} 
		catch (InstantiationException ie) 
		{
			throw new ResourceInstanceException(ie.getMessage(), ie, this);
		} 
		catch (IllegalAccessException iae) {
			throw new ResourceInstanceException(iae, this);
		}
	}

	protected void afterInstanceTarget(InstanceSpace context, Object target) {
		if(configHandler.getInitMethod() != null) {
			try { 
				configHandler.getInitMethod().invoke(target); 
			}
			catch (Exception e) {
				String message = "Failed execution the initMethod " + configHandler.getInitMethod() + "() in the class '" + target.getClass().getCanonicalName() + "'";
				if(e.getCause() == null) {
					throw new ConfigException(message, e);
				}
				else {
					throw new ConfigException(message, e.getCause());
				}
			}
		}
	}


}
