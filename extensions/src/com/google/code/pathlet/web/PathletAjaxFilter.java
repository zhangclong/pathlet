/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.pathlet.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.pathlet.config.ConfigManager;
import com.google.code.pathlet.core.PathletConstants;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.impl.DefaultPathletContainer;
import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.util.ValueUtils;
import static com.google.code.pathlet.web.WebPathletConstants.*;
				

public class PathletAjaxFilter implements Filter {
	
	public final static String INIT_PARAM_CONFIGS = "configs";
	
	public final static String INIT_PARAM_PROPERTIES = "properties";
	
	public final static String INIT_PARAM_ENCODING = "configEncoding";
	
	public final static String DEFAULT_ENCODING = "UTF-8";
	
	public final static String CLASSPATH_PREFIX = "classpath:";
	
	public final static String[] PATHLET_SCOPES = {PathletConstants.SETTING_SCOPE, PathletConstants.CONTAINER_SCOPE, REQUEST_SCOPE};
	
    private static PathletContainer container;

    private PathletAjaxProcessor processor;
    
    
    /**
     * Initializes the filter by creating a default dispatcher
     * and setting the default packages for static resources.
     *
     * @param filterConfig The filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException {
    	
    	try {
    	
	    	String encoding = filterConfig.getInitParameter(INIT_PARAM_ENCODING);
	    	if(encoding == null) {
	    		encoding = DEFAULT_ENCODING;
	    	}
	    	
	    	ConfigManager configManager;		
	    	//try {
	        	
	    		String[] configParameters = filterConfig.getInitParameter(INIT_PARAM_CONFIGS).split(",");
	    		File[] configFiles = new File[configParameters.length];
	    		for(int i=0 ; i<configParameters.length ; i++) {
	    			String configParam = configParameters[i].trim();
	    			if(ValueUtils.notEmpty(configParam)) {
	    				configFiles[i] = getServletParamFile(configParam, filterConfig.getServletContext());
	    			}
	    		}
	    		
	    		File[] propFiles = null;
	    		String propParamsString = filterConfig.getInitParameter(INIT_PARAM_PROPERTIES);
	    		if(ValueUtils.notEmpty(propParamsString)) {
		    		String[] propParams = propParamsString.split(",");
		    		if(propParams != null && propParams.length > 0) {
			    		propFiles = new File[propParams.length];
			    		for(int i=0 ; i<propParams.length ; i++) {
			    			String propParam = propParams[i].trim();
			    			if(ValueUtils.notEmpty(propParam)) {
			    				propFiles[i] = getServletParamFile(propParam, filterConfig.getServletContext());
			    			}
			    		}
		    		}
	    		}
	    		
	
	        	WebConfigManagerAccessor configManagerAccessor = WebConfigManagerAccessor.getInstance(configFiles, propFiles, encoding, filterConfig.getServletContext());
	
	    		configManager = configManagerAccessor.loadConfigManager();
			//} catch (IOException e) {
			//	throw new ServletException(e);
			//}
	    	
	    	container = new DefaultPathletContainer(configManager, PATHLET_SCOPES);
	    	
	    	WebPathletHelper.setWebContainer(filterConfig.getServletContext(), container);
	    	
			this.processor = (PathletAjaxProcessor)container.getInstance("/ajaxProcessor");
			
	    }
    	catch(Throwable t) {
    		t.printStackTrace();
    	}
		
    }

    /**
     * Process action or handle request .
     * <p/>
     * The filter tries to match the request to an action mapping.
     * If mapping is found, the action processes is delegated to the dispatcher's serviceAction method.
     * If action processing fails, doFilter will try to create an error page via the dispatcher.
     * <p/>
     * Otherwise, if the request is for a static resource,
     * the resource is copied directly to the response, with the appropriate caching headers set.
     * <p/>
     * If the request does not match an action mapping, or a static resource page, 
     * then it passes through.
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        processor.process((HttpServletRequest) req, (HttpServletResponse) res);
    }


	public void destroy() {
		container.destroy();
	}

    
    private static File getServletParamFile(String paramValue, ServletContext context) throws IOException {
    	File ret;
    	if(paramValue.startsWith(CLASSPATH_PREFIX)) {
    		String classpath = paramValue.substring(CLASSPATH_PREFIX.length());
    		ClassPathResource cpResource = new ClassPathResource(classpath, ClassUtils.getDefaultClassLoader());
    		ret = cpResource.getFile();
    	}
    	else {
    		if(paramValue.charAt(0) != '/') {
    			ret = new File(context.getRealPath('/' + paramValue));
    		}
    		else {
    			ret = new File(context.getRealPath(paramValue));
    		}
    	}
    	
    	return ret;
    }

}
