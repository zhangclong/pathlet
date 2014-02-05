package com.google.code.pathlet.core;

import java.util.Collection;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.core.exception.ModuleHandleException;

public interface ModuleManager {
	
	/**
	 * 专用于和PathletContainer的集成，初始时设定container.
	 * @return
	 */
	void setContainer(PathletContainer container);
	
	/**
	 * 专用于和PathletContainer的集成，是每个resource加载class时的ClassLoader
	 * @return
	 */
	PathClassLoader getPathClassLoader();
	
	/**
	 * 专用于和PathletContainer的集成，SpaceInstanceEvent是InstanceSpace中每次取实例时的事件回调接口。
	 * @return
	 */
	SpaceInstanceEvent getSpaceInstanceEvent();
	
	Module getModule(String moduleId);
	
	Collection<Module> getModules();
	
	void addModule(Module module) throws ConfigException;
	
	void removeModule(String moduleId) throws ConfigException;
	
	Module getModuleByPath(Path resourcePath);
	
	void startModule(String moduleId) throws ModuleHandleException;
	
	void stopModule(String moduleId) throws ModuleHandleException;
	
	boolean isModuleStarted(String moduleId);
	
	void reloadModule(String moduleId) throws ModuleHandleException;
	
}
