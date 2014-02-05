package com.google.code.pathlet.jndi;

import javax.naming.NamingException;

import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.exception.ResourceInstanceException;
import com.google.code.pathlet.core.impl.BaseResource;

public class JndiResource extends BaseResource {
	
	private String scope;
	
	private Path path;
	
	private String jndiName;

	public JndiResource(JndiResourceFactory factory, String scope, Path path, String jndiName) {
		super(factory);
		this.scope = scope;
		this.path = path;
		this.jndiName = jndiName;
	}

	public String getScope() {
		return scope;
	}

	public Path getPath() {
		return path;
	}

	public void destroyInstance(Object target) { }
	
	public boolean destroyable() { return false; }

	protected void afterInstanceTarget(InstanceSpace context, Object target) { }
	
	protected Object doInstanceTarget(InstanceSpace context) {
		try {
			return ((JndiResourceFactory)getFactory()).getBaseJndiContext().lookup(this.jndiName);
		} 
		catch (NamingException e) {
			throw new ResourceInstanceException("Failed get JNDI resource!", e, this);
		}
	}

}

