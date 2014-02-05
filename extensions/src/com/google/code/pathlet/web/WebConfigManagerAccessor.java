package com.google.code.pathlet.web;

import static com.google.code.pathlet.core.PathletConstants.SYS_ATTR_CLASSES_BASEDIR;
import static com.google.code.pathlet.web.WebPathletConstants.SYS_ATTR_WEBAPP_BASEDIR;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.code.pathlet.core.ConfigManagerAccessor;

public class WebConfigManagerAccessor extends ConfigManagerAccessor {
	
	private static Log log = LogFactory.getLog(WebConfigManagerAccessor.class);
	
	private final static String CONTEXT_KEY = "PATHLET_CONFIG_MANAGER_ACCESSOR";
	
	private ServletContext context;
	
	public static WebConfigManagerAccessor getInstance(ServletContext context) {
		return (WebConfigManagerAccessor)context.getAttribute(CONTEXT_KEY);
	}
	
	public static WebConfigManagerAccessor getInstance(File[] configFiles, File[] propertyFiles, String encoding, ServletContext context) {
		WebConfigManagerAccessor accessor = (WebConfigManagerAccessor)context.getAttribute(CONTEXT_KEY);
		if(accessor == null) {
			accessor = new WebConfigManagerAccessor(configFiles, propertyFiles, encoding, context);
			context.setAttribute(CONTEXT_KEY, accessor);
		}
		return accessor;
	}
	
	
	private WebConfigManagerAccessor(File[] configFiles, File[] propertyFiles, String encoding, ServletContext context) {
		super(configFiles, propertyFiles, encoding);
		this.context = context;
		
	}

	/**
	 * Append web environment properties: webapp.basedir=web application directory in file system.
	 * 
	 */
	protected Properties loadPlaceholder() throws IOException {	
		Properties props = super.loadPlaceholder();
		
		String webappBasedir = context.getRealPath("/");
		if(webappBasedir.endsWith(File.separator) == false) {
			webappBasedir += File.separator;
		}
		webappBasedir = webappBasedir.replace("\\", "/");
		
		props.setProperty(SYS_ATTR_WEBAPP_BASEDIR,webappBasedir);
		log.info("Pathlet placeholder set property:  " + SYS_ATTR_WEBAPP_BASEDIR + "=" + webappBasedir);
		log.info("Pathlet placeholder set property:  " + SYS_ATTR_CLASSES_BASEDIR + "=" + props.getProperty(SYS_ATTR_CLASSES_BASEDIR));
		
		return props;
	}
	
	
	
	
}
