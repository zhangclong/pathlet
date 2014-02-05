package com.google.code.pathlet.web;

import javax.servlet.ServletContext;

import com.google.code.pathlet.core.PathletContainer;

public class WebPathletHelper {
	
	public final static String WEB_PATHLET_CONTAINER_KEY = "WEB_PATHLET_CONTAINER";
	
	public static void setWebContainer(ServletContext context, PathletContainer container) {
		context.setAttribute(WEB_PATHLET_CONTAINER_KEY, container);
	}
	
	public static PathletContainer getWebContainer(ServletContext context) {
		return (PathletContainer)context.getAttribute(WEB_PATHLET_CONTAINER_KEY);
	}
}
