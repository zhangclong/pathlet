package com.google.code.pathlet.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.ModuleManager;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathClassLoader;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.ResourceInstanceProcessor;
import com.google.code.pathlet.core.SpaceInstanceEvent;
import com.google.code.pathlet.core.exception.ModuleHandleException;

public class DefaultModuleManager implements ModuleManager {
	
	public static Log log = LogFactory.getLog(DefaultModuleManager.class);
	
	public final static String URL_FILE_PROTOCOL = "file";
	
	public final static String NOT_MATCH_MODULE = "NOT_MATCH_MODULE";
	
	private ModulePathClassLoader loader;
	
	private ModuleSpaceInstanceEvent event;
	
	private Map<String, ModuleHandler> moduleHandlerMap = new LinkedHashMap<String, ModuleHandler>();
	
	private Map<Path, String> resourcePathModuleIdMap = new HashMap<Path, String>();
	
	private PathletContainer container;

	public DefaultModuleManager(Collection<Module> modules, PathletContainer container) {
		this.loader = new ModulePathClassLoader(this);
		this.event = new ModuleSpaceInstanceEvent();
		this.container = container;
		for(Module module : modules) {
			addModule(module);
		}
	}
	

	public void setContainer(PathletContainer container) {
		this.container = container;
	}

	public PathClassLoader getPathClassLoader() {
		return this.loader;
	}

	public void addModule(Module module) throws ConfigException {
		if(moduleHandlerMap.containsKey(module.getId())) {
			throw new ConfigException("Already exists same module. moduleId=" + module.getId());
		}
		
		ModuleHandler moduleHandler = new ModuleHandler(module, this.loader);

		moduleHandlerMap.put(module.getId(), moduleHandler);
		
		loader.putModuleLoader(module.getId(), moduleHandler.rebuildClassLoader());
	}
	
	public void removeModule(String moduleId) throws ConfigException {
		if(isModuleStarted(moduleId)) {
			throw new ConfigException("Can not remove the started module! moduleId=" + moduleId);
		}
		
		moduleHandlerMap.remove(moduleId);
	}

	public void startModule(String moduleId) throws ModuleHandleException {
		
		ModuleHandler handler = moduleHandlerMap.get(moduleId);
		
		//1. Add module Classloader into the ModuleClassLoader, if could not found one.
		if(loader.getModuleLoader(moduleId) == null) {
			loader.putModuleLoader(moduleId, handler.rebuildClassLoader());
		}
		
		handler.startScanner();

		handler.setStarted(true); //Set the started flag
		handler.setChanged(false); //Reset the changed flag to false.
		
		handler.listenerInit(container);
	}

	public void stopModule(String moduleId) throws ModuleHandleException {
		
		ModuleHandler handler = moduleHandlerMap.get(moduleId);
		
		handler.listenerDestory(container);
		
		//1. stop scanner
		handler.stopScanner();
		
		//2. Remove the module classloader from the ModuleClassLoader
		loader.removeModuleLoader(moduleId);
		
		//3. clean the resource cache and context's target cache in the container.
		//   FIXME must add more synchronized mechamism to lock the resource and target access, until the ClassLoader repalced.
		container.flush(handler.getModule().getResourcePattern());
		
		//4. Set the started flag
		handler.setStarted(false);
	}

	public boolean isModuleStarted(String moduleId) {
		return moduleHandlerMap.get(moduleId).isStarted();
	}
	

	public void reloadModule(String moduleId) throws ModuleHandleException {
		stopModule(moduleId);
		startModule(moduleId);
		log.info("The module: \"" + moduleId + "\" has been completely reloaded!");
	}

	public Module getModule(String moduleId) {
		return moduleHandlerMap.get(moduleId).getModule();
	}
	
	public Collection<Module> getModules() {
		Collection<ModuleHandler> moduleSettings = moduleHandlerMap.values();
		List<Module> modules = new ArrayList<Module>(moduleSettings.size());
		for(ModuleHandler setting : moduleSettings) {
			modules.add(setting.getModule());
		}
		
		return modules;
	}

	public Module getModuleByPath(Path resourcePath) {
		String moduleId = resourcePathModuleIdMap.get(resourcePath);
		if(moduleId == null) {
			Collection<ModuleHandler> modulesetings = moduleHandlerMap.values();
			for(ModuleHandler handler : modulesetings) {
				if(handler.getModule().getResourcePattern().isMatch(resourcePath)) {
					resourcePathModuleIdMap.put(resourcePath, handler.getModule().getId());
					moduleId = handler.getModule().getId();
				}
			}
			
			//No found match module
			if(moduleId == null) {
				resourcePathModuleIdMap.put(resourcePath, NOT_MATCH_MODULE);
				moduleId = NOT_MATCH_MODULE;
			}
		}

		if(NOT_MATCH_MODULE.equals(moduleId)) {
			return null;
		}
		else {
			return moduleHandlerMap.get(moduleId).getModule();
		}
	
	}
	
	public SpaceInstanceEvent getSpaceInstanceEvent() {
		return this.event;
	}

	private class ModuleSpaceInstanceEvent implements SpaceInstanceEvent {

		public void beforeGet(InstanceSpace space, Path resourcePath) {
			//get the match module.
			Module module = getModuleByPath(resourcePath);
			
			//reload the module, if it need to be reload. 
			if(module != null && module.isAutoReload() == true) {
				if(moduleHandlerMap.get(module.getId()).isChanged()) {
					moduleHandlerMap.get(module.getId()).setChanged(false);
					reloadModule(module.getId());
				}
			}
		}

		public void afterGet(InstanceSpace space, Path resourcePath,
				Object instance) {
		}
	
	}

	
}
