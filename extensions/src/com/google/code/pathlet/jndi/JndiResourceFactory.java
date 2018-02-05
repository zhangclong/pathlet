package com.google.code.pathlet.jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.code.pathlet.config.ResourceConfig;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.exception.ResourceAccessException;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.core.impl.BaseResourceFactory;

public class JndiResourceFactory extends BaseResourceFactory<Resource> {

	private String baseJndiName;
	
	private Path startPath;
	
	private Context baseJndiContext;
	
	public JndiResourceFactory() {   }
	
	public String getBaseJndiName() {
		return baseJndiName;
	}

	public void setBaseJndiName(String baseJndiName) {
		this.baseJndiName = baseJndiName;
	}
	
	public Path getStartPath() {
		return startPath;
	}

	public void setStartPath(Path startPath) {
		this.startPath = startPath;
	}

	public Resource createResource(ResourceConfig resourceConfig) throws ResourceException { 
		Path resourcePath = resourceConfig.getPath();
		return new JndiResource(this, getDefaultScope(), resourcePath, convertToJndiName(resourcePath, startPath));
	}

	public Resource createResource(Path resourcePath) throws ResourceException {
		return new JndiResource(this, getDefaultScope(), resourcePath, convertToJndiName(resourcePath, startPath));
	}
	
	private String convertToJndiName(Path resourcePath, Path startPath) throws ResourceAccessException {
		String prePathStr = startPath.getFullPath();
		if(prePathStr.endsWith("/") == false) {
			prePathStr = prePathStr + "/";
		}
		
		//Validate the resourcePath is start with prePath
		if(resourcePath.getDirectory().indexOf(prePathStr) != 0) {
			throw new ResourceAccessException("Failed to find resource path=" 
						+ resourcePath + ", The path must be start with '" + prePathStr + "'", resourcePath);
		}
		
		String dir = resourcePath.getDirectory();
		String trimmedPrePath = dir.substring(prePathStr.length());
		
		return trimmedPrePath + resourcePath.getNameWithoutSuffix();
	}
	
	
	protected Context getBaseJndiContext() throws ResourceException {
		if(baseJndiContext == null) {
			try {
				Context initContext = new InitialContext();
				baseJndiContext = (Context)initContext.lookup(baseJndiName);
			} catch (NamingException e) {
				throw new ResourceException("Failed to initialize the JNDI context from the baseJndiName=" + this.baseJndiName, e);
			}
		}
		return baseJndiContext;
	}
	
}
