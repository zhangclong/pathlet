package com.google.code.pathlet.core.instanceroot.interceptor;

import com.google.code.pathlet.config.anno.Advisor;
import com.google.code.pathlet.config.anno.Pointcut;
import com.google.code.pathlet.core.ProceedingJoinPoint;
import com.google.code.pathlet.core.instanceroot.user.User;


@Advisor
public class MonitorInterceptor2 {

	
	@Pointcut(includes = "/**/UserService", methods = "getUser")
	public Object around(ProceedingJoinPoint aj) throws Throwable {
	
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
