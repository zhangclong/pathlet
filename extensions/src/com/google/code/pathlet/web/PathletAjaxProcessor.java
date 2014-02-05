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

import static com.google.code.pathlet.web.WebPathletConstants.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;

import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.util.ValueUtils;



public class PathletAjaxProcessor {
	
    private static final Log log = LogFactory.getLog(PathletAjaxProcessor.class);
	
	// Determine whether the Servlet 2.4 HttpServletResponse.setCharacterEncoding(String)
	// method is available, for use in the "doFilterInternal" implementation.
	private final static boolean responseSetCharacterEncodingAvailable = ClassUtils.hasMethod(
			HttpServletResponse.class, "setCharacterEncoding", new Class[] {String.class});

	private PathletContainer container;
	
	private String requestCharset;
	
	private String responseCharset;
	
	private Path prefixPath = null;

	private String suffix;
	
	private List<RequestProcessor> requestProcessors;
	
	private List<ResponseProcessor>  resultResponseProcessors;
	
	public PathletAjaxProcessor() { }
	
	public List<RequestProcessor> getRequestProcessors() {
		return requestProcessors;
	}

	public void setRequestProcessors(List<RequestProcessor> requestProcessors) {
		this.requestProcessors = requestProcessors;
	}

	public void setContainer(PathletContainer container) {
		this.container = container;
	}

	public void setRequestCharset(String requestCharset) {
		this.requestCharset = requestCharset;
	}

	public void setResponseCharset(String responseCharset) {
		this.responseCharset = responseCharset;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setPrefixPath(Path prefixPath) {
		this.prefixPath = prefixPath;
	}

	public List<ResponseProcessor> getResultResponseProcessors() {
		return resultResponseProcessors;
	}

	public void setResultResponseProcessors(
			List<ResponseProcessor> resultResponseProcessors) {
		this.resultResponseProcessors = resultResponseProcessors;
	}

	
	public void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		setCharset(request, response);
        try {
    		InstanceSpace requestSpace = container.getSpace(REQUEST_SCOPE, request, true);
    		
    		//Add the request and response instance into this InstanceSpace on default path 
    		requestSpace.addInstance(RESPONSE_PATH, response);
    		requestSpace.addInstance(REQUEST_PATH, request);
    		
    		String servletPath = getServletPath(request);
    		
    		Path requestPath = new Path(servletPath);

    		//1. Get method name
    		if(!suffix.equals(requestPath.getSuffix())) {
    			throw new ResourceException("Error path suffix! The path must be end with '." + suffix + "'" );
    		}
    		String methodName = requestPath.getNameWithoutSuffix();
    		
    		//2. Get action path
    		String actionPathPart = requestPath.getParent().getFullPath();
    		Path actionPath;
    		if(prefixPath != null) {
    			actionPath = new Path(prefixPath, actionPathPart.substring(1, actionPathPart.length() - 1));  //remove the first and last char '/'
    		}
    		else {
    			actionPath = new Path(actionPathPart.substring(0, actionPathPart.length() - 1));  //remove the last char '/'
    		}
    		
    		//3. Get action target object
    		Object actionObj = requestSpace.getInstance(actionPath);
    		if(actionObj == null) {
    			throw new ResourceException("Get NULL resource target from the path:" + requestPath.toString() );
    		}
    		
    		//4. Get execution method
    		Method method = actionObj.getClass().getMethod(methodName);
    		if(method == null) {
    			throw new ResourceException("Failed to get action method: " + methodName);
    		}
    		
    		//5. Process request
    		//   Set action target object properties value
    		for(RequestProcessor requestProcessor : requestProcessors) {
    			if(requestProcessor.process(requestPath, actionObj, request)) {
    				break;
    			}
    		}
    		
    		//6. Execute action method
    		Object returnResult = method.invoke(actionObj);
    		
    		//7. Proceed return result object and HttpServletResponse object. 
    		if(returnResult != null) {
	    		for(ResponseProcessor processor : resultResponseProcessors) {
	    			if(processor.processResult(requestPath, returnResult, response)) {
	    				break;
	    			}
	    		}
    		}
        } 
        catch(InvocationTargetException ite) {
        	//If exception wrapped by InvocationTargetException, find the target exception and throw it.
        	Throwable targetEx = ite.getTargetException();
        	log.fatal(targetEx.getMessage(), targetEx);
        	throw new ServletException(targetEx);
        }
        catch(Exception re) {
        	log.fatal(re.getMessage(), re);
        	throw new ServletException(re);
        }
        finally {
        	container.destroySpace(REQUEST_SCOPE, request);
        }
        
	}
	
	
	
	
	
	private void setCharset(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding(requestCharset);
		
		if (responseSetCharacterEncodingAvailable) {
			response.setCharacterEncoding(responseCharset);
		}
	}
	

    /**
     * Retrieves the current request servlet path.
     * Deals with differences between servlet specs (2.2 vs 2.3+)
     *
     * @param request the request
     * @return the servlet path
     */
    private String getServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        if (ValueUtils.notEmpty(servletPath)) {
            return servletPath;
        }

        String requestUri = request.getRequestURI();
        int startIndex = request.getContextPath().equals("") ? 0 : request.getContextPath().length();
        int endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri.lastIndexOf(request.getPathInfo());

        if (startIndex > endIndex) { // this should not happen
            endIndex = startIndex;
        }

        return requestUri.substring(startIndex, endIndex);
    }
}
