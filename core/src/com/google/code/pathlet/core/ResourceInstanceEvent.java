package com.google.code.pathlet.core;

import com.google.code.pathlet.config.ConfigManager;


/**
 * 
 * 
 * 
 * @author Charlie Zhang
 *
 * @param <T>
 */
public interface ResourceInstanceEvent {
	
	/**
	 * The 
	 * @param processor
	 * @param sapce
	 * @param resource
	 * @return
	 */
	Object instanceTarget(ResourceInstanceProcessor processor, InstanceSpace sapce, Resource resource);
	
	/**
	 * Flush the all match instances which was cached in current ResourceInstanceEvent.<br/>
	 * If matchPattern is null, all cached instances will be removed .
	 * 
	 * @param mathPattern
	 */
	public void flush(String scope, PathPattern matchPattern);

}
