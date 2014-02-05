package com.google.code.pathlet.core;

public interface SpaceInstanceEvent {
	/**
	 * Callback method. It will be invoked before the InstanceSpace about to getInstance().
	 * @param space
	 * @param instance
	 */
	void beforeGet(InstanceSpace space, Path resourcePath);
	
	/**
	 * Callback method. It will be invoked after the InstanceSpace just getInstance().
	 * @param space
	 * @param instance
	 */
	void afterGet(InstanceSpace space, Path resourcePath, Object instance);
	
}
