package com.google.code.pathlet.core.impl;

import com.google.code.pathlet.core.InstanceSpace;

public interface TargetCreationInterceptor {
	
	
	public Object instanceTarget(BeanResource resource, InstanceSpace context);
	
}
