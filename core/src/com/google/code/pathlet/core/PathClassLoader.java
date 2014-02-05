package com.google.code.pathlet.core;

import com.google.code.pathlet.core.exception.ResourceException;

/**
 * To provide the flexible and dynamic class loading mechanism, this interface give a unique and decouple way to load class by path.
 * 
 * @author Charlie Zhang
 *
 */
public interface PathClassLoader {
	
	/**
	 * Load class against resource path.
	 * 
	 * @param resourcePath
	 * @param className
	 * @return
	 */
	public Class loadClass(Path resourcePath, String className) throws ResourceException;
	
	/**
	 * Load class against moduleId.
	 * 
	 * @param resourcePath
	 * @param className
	 * @return
	 */
	public Class loadClass(String moduleId, String className) throws ResourceException;

}
