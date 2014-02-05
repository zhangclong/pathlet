package com.google.code.pathlet.config;

import com.google.code.pathlet.core.Path;

/**
 * 
 * A ResourceConfig describes a resource configurations, which has path, scope, and further information supplied by
 * concrete implementations.
 * 
 * @author Charlie Zhang
 *
 */
public interface ResourceConfig {
	
	public Path getPath();
	
	public String getScope();
	
	/**
	 * If it is true, this resource will be instanced on when its InstanceSpace being initialized.
	 * Default value is false, if the property was ignored.
	 * @return
	 */
	public boolean isInitInstance();

}
