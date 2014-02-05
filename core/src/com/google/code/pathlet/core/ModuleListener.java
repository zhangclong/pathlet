package com.google.code.pathlet.core;

/**
 * Module listener for starting and stopping event.
 * The <code>init()</code> method will be invoked, after the module was just started.
 * The <code>destory()</code> method will be invoked, before the module begin to be stopped.
 * 
 * @author Charlie Zhang
 *
 */
public interface ModuleListener {
	
	/**
	 * Event method, it will be invoked after the module was just started.
	 * @param container
	 * @param module
	 */
	void init(PathletContainer container, Module module);
	
	/**
	 * Event method, it will be invoked before the module begin to be stopped.
	 * @param container
	 * @param module
	 */
	void destroy(PathletContainer container, Module module);
	
}
