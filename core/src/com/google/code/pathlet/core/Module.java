package com.google.code.pathlet.core;

import java.net.URL;


public interface Module {

	public PathPattern getResourcePattern();
	
	public void setResourcePattern(PathPattern resourcePattern);

	public String getId();
	
	public void setId(String id);
	
	/**
	 * get other depend modules.
	 * 
	 * These is dependent relationship definition: <br/>
	 * The dependent module must be added before this one, must be start before this one be started.
	 * 
	 * @return
	 */
	//public String[] getDependentModuleIds();
	//public void setDependentModuleIds(Module[] modules);
	public String getListener();
	
	public void setListener(String listener);
	
	
	public URL[] getClassPaths();
	
	public void setClassPaths(URL[] classPaths);
	
	public boolean isAutoReload();
	
	public void setAutoReload(boolean autoReload);

}