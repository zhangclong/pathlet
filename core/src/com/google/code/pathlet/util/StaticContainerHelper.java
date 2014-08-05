package com.google.code.pathlet.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.code.pathlet.core.ConfigManagerAccessor;
import com.google.code.pathlet.core.PathletConstants;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.impl.DefaultPathletContainer;

/**
 * 
 * Container test helper class. Using to store the container instance from one to another test case.
 * 
 * @author Charlie Zhang
 * @since 2013-04-08
 */
public class StaticContainerHelper {
	
	private final static Map<String, PathletContainer> containerCache;
	
	static {
		containerCache = new HashMap<String, PathletContainer>();
		//Destroy all containers after java VM has being shutdown.
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { destoryContainers(); }
		});
	}
	
	/**
	 * Get a container from static cache by name. Or create a new one, if it hasn't initialized.
	 * @param name
	 * @param configFiles
	 * @param propertyFiles
	 * @param configEncoding
	 * @return
	 * @throws IOException
	 */
	public static PathletContainer getContainer(String name, File[] configFiles, File[] propertyFiles, String configEncoding) throws IOException {
		return getContainer(name, configFiles, propertyFiles, configEncoding, PathletConstants.DEFAULT_SCOPES);
	}
	
	/**
	 * Get a container from static cache by name. Or create a new one, if it hasn't initialized.
	 * @param name
	 * @param configFiles
	 * @param propertyFiles
	 * @param configEncoding
	 * @return
	 * @throws IOException
	 */
	public static PathletContainer getContainer(String name, File[] configFiles, File[] propertyFiles, String configEncoding, String[] scopes) throws IOException {
		
		synchronized(containerCache) {
			PathletContainer container = containerCache.get(name);
			if(container == null) {
				ConfigManagerAccessor configManagerAcc = new ConfigManagerAccessor(configFiles, propertyFiles, configEncoding);
			   	container = new DefaultPathletContainer(configManagerAcc.loadConfigManager(), scopes);

			   	containerCache.put(name, container);
			}
			return container;
		}
		
	}
	
	
	/**
	 * Get a container from static cache by name. 
	 */
	public static PathletContainer getContainer(String name) {
		synchronized(containerCache) {
			return containerCache.get(name);
		}
	}
	
	/**
	 * Destroy a container by name. 
	 */
	public static PathletContainer destoryContainer(String name) {
		synchronized(containerCache) {
			PathletContainer container = containerCache.get(name);
			container.destroy();
			return container;
		}
	}
	
	/**
	 * Destroy all existence containers in containerCache. 
	 */
	public static void destoryContainers() {
		synchronized(containerCache) {
			for(PathletContainer container : containerCache.values()) {
				container.destroy();
			}
		}
	}

}
