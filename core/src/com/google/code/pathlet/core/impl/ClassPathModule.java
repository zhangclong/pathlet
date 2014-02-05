package com.google.code.pathlet.core.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.PathPattern;

public class ClassPathModule implements Module {
	
	/**
	 * Identifier for one module.
	 */
	private String id;
	
	
	/**
	 * Folder contains classes, or jar/zip file path.
	 */
	private URL[] classPaths;
	
	
	/**
	 * Match resources pattern for this module. 
	 */
	private PathPattern resourcePattern;
	
	/**
	 * If it is true, the module will automatically detect changing and reload the changed module.
	 */
	private boolean autoReload;
	
	private String listener;
	

	public URL[] getClassPaths() {
		return classPaths;
	}

	public PathPattern getResourcePattern() {
		return resourcePattern;
	}

	public String getId() {
		return id;
	}


	public void setResourcePattern(PathPattern resourcePattern) {
		this.resourcePattern = resourcePattern;
	}


	public void setId(String id) {
		this.id = id;
	}


	public void setClassPaths(URL[] classPaths) {
		this.classPaths = classPaths;
	}


	public boolean isAutoReload() {
		return autoReload;
	}


	public void setAutoReload(boolean autoReload) {
		this.autoReload = autoReload;
	}

	public String getListener() {
		return listener;
	}

	public void setListener(String listener) {
		this.listener = listener;
	}
	
}
