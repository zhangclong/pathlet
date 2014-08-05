package com.google.code.pathlet.core.instanceroot.interceptor;

import com.google.code.pathlet.core.ProceedingJoinPoint;
import com.google.code.pathlet.core.instanceroot.user.User;


/**
 * 
 * set the settings by JSON configuration.
 * 
 * @author Charlie
 *
 */
public class ConfigInterceptor {


	public Object aroundOuter(ProceedingJoinPoint aj) throws Throwable {
	
		String username = (String)(aj.getArguments()[0]);
		
		Object[] newArgs = null;
		if("ch".equals(username)) {
			newArgs = new String[]{username + "arl"};  //Change by creating a new arguments.
		}
		
		User user =  (User)aj.proceed(newArgs);
		
		user.setName(user.getName() +  "inspiration!");
		
		
		return user;
	}
	
	
	public Object aroundInner(ProceedingJoinPoint aj) throws Throwable {
		String username = (String)(aj.getArguments()[0]);
		
		Object[] newArgs = null;
		if("charl".equals(username)) {
			newArgs = new String[]{username + "ie"};  //Change by creating a new arguments.
		}
		
		User user =  (User)aj.proceed(newArgs);
		
		user.setName(user.getName() +  " got a ");
		
		
		return user;
	}
}
