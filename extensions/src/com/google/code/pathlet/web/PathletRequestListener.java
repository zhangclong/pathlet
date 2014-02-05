package com.google.code.pathlet.web;

import static com.google.code.pathlet.web.WebPathletConstants.REQUEST_SCOPE;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.PathletContainer;

public class PathletRequestListener implements ServletRequestListener {

	public void requestDestroyed(ServletRequestEvent reqEvent) {
		//If found bound InstanceSpace with this request, destroy this InstanceSpace from the container.
		PathletContainer container = WebPathletHelper.getWebContainer(reqEvent.getServletContext());
		ServletRequest sr = reqEvent.getServletRequest();
		InstanceSpace space = container.getSpace(REQUEST_SCOPE, sr, false);
		if(space != null) {
			container.destroySpace(REQUEST_SCOPE, sr);
			//TODO just for test
			//System.out.println("Test reqeust space destroied! request=" + sr.toString());
		}
	}

	public void requestInitialized(ServletRequestEvent reqEvent) {
		//TODO just for test
		//System.out.println("PathletRequestListener initializing! request=" + reqEvent.getServletRequest().toString());
	}

}
