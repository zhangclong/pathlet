package com.google.code.pathlet.exampleservice.interceptor;

import com.google.code.pathlet.config.anno.Advisor;
import com.google.code.pathlet.config.anno.Pointcut;
import com.google.code.pathlet.core.ProceedingJoinPoint;
import com.google.code.pathlet.exampleservice.user.User;


@Advisor
public class MonitorInterceptor1 {

	@Pointcut(includes = "/**/UserService", methods = "getUser")
	public Object around(ProceedingJoinPoint aj) throws Throwable {
	
		String username = (String)(aj.getArguments()[0]);
		
		Object[] newArgs = null;
		if("ch".equals(username)) {
			newArgs = new String[]{username + "arl"};  //Change by creating a new arguments.
		}
		
		User user =  (User)aj.proceed(newArgs);
		
		user.setName(user.getName() +  "inspiration!");
		
		
		return user;
	}
}
