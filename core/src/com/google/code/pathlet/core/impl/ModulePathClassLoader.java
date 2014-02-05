package com.google.code.pathlet.core.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.ModuleManager;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathClassLoader;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.core.exception.ResourceNotFoundException;
import com.google.code.pathlet.util.ClassUtils;

public class ModulePathClassLoader implements PathClassLoader {
	
	private ModuleManager moduleManager;
	
	private Map<String, ClassLoader> classLoaderMap = new LinkedHashMap<String, ClassLoader>();
	
	public ModulePathClassLoader(DefaultModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}
	
	public ClassLoader getModuleLoader(String moduleId) {
		return classLoaderMap.get(moduleId);
	}
	
	public ClassLoader putModuleLoader(String moduleId, ClassLoader classLoader) {
		return classLoaderMap.put(moduleId, classLoader);
	}
	
	public ClassLoader removeModuleLoader(String moduleId) {
		return classLoaderMap.remove(moduleId);
	}

	public Class loadClass(Path resourcePath, String className)
			throws ResourceException {
		
		

		Module module = moduleManager.getModuleByPath(resourcePath);
		
		ClassLoader loader = null;
		if(module != null) {
			loader  = classLoaderMap.get(module.getId());
		}

		try {
			if(loader != null) {
				return loader.loadClass(className);
			}
			else {
				return ClassUtils.getDefaultClassLoader().loadClass(className);
			}
		} 
		catch (Throwable e) {
			throw new ResourceNotFoundException(e, resourcePath);
		}
		
	}
	
	public Class loadClass(String moduleId, String className) throws ResourceException {
		ClassLoader loader  = classLoaderMap.get(moduleId);

		try {
			if(loader != null) {
				return loader.loadClass(className);
			}
			else {
				return ClassUtils.getDefaultClassLoader().loadClass(className);
			}
		} 
		catch (Throwable e) {
			throw new ResourceException(e);
		}
	}

}
