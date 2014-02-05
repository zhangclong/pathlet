package com.google.code.pathlet.util;

import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.Resource;

public class PathletUtils {
	
	public static Resource getResource(String pathStr, Resource currentWorkingResource, InstanceSpace currentWorkingSpace) {

		Path resPropPath = new Path(pathStr);
		if(resPropPath.isAbsolute() == false) {
			//Convert propPath to absolute path:  Target ResoucePath + propPath = absolute path
			resPropPath = new Path(currentWorkingResource.getPath().getParent(), pathStr);
		}
		
		return currentWorkingSpace.getContainer().getResource(resPropPath);
	}
	
	public static Object getInstance(String pathStr, Resource currentWorkingResource, InstanceSpace currentWorkingSpace) {
		Path propPath = new Path(pathStr);
		if(propPath.isAbsolute() == false) {
			//Convert propPath to absolute path:  Target ResoucePath + propPath = absolute path
			propPath = new Path(currentWorkingResource.getPath().getParent(), pathStr);
		}
		
		return currentWorkingSpace.getInstance(propPath, false, false);
	}
	
	public static InstanceSpace getSpace(String designatedScope, InstanceSpace currentWorkingSpace) {

		InstanceSpace spaceValue = currentWorkingSpace;
		boolean foundContext = false;
		while(spaceValue != null) {
			if(designatedScope.equals(spaceValue.getScope())) {
				foundContext = true;
				break;
			}
			spaceValue = spaceValue.getParent();
		}
		
		if(foundContext == true) {
			return spaceValue;
		}
		else {
			return null;
		}
	}
}
