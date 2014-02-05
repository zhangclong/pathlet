package com.google.code.pathlet.core.impl;

import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathClassLoader;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.core.exception.ResourceNotFoundException;
import com.google.code.pathlet.util.ClassUtils;

/**
 * 
 * Provide simple class loading method.
 * It only use current class <code>ClassLoader</code> or current thread classLoader to load class.
 * 
 * @author Charlie Zhang
 * @since 2012-12-22
 *
 */
public class SimplePathClassLoader implements PathClassLoader {
	
	private ClassLoader classLoader = null;

	public Class loadClass(Path resourcePath, String className)
			throws ResourceNotFoundException {
		
		if(classLoader == null) {
			this.classLoader = ClassUtils.getDefaultClassLoader();
		}
		
		try {
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ResourceNotFoundException("Faile to load class by class name!", e, resourcePath);
		}
	}

	public Class loadClass(String moduleId, String className)
			throws ResourceException {
		if(classLoader == null) {
			this.classLoader = ClassUtils.getDefaultClassLoader();
		}
		
		try {
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ResourceException("Faile to load class by class name!", e);
		}
	}
	
	

}
