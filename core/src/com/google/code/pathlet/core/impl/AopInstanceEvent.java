package com.google.code.pathlet.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.config.ConfigManager;
import com.google.code.pathlet.config.anno.Advisor;
import com.google.code.pathlet.config.anno.Pointcut;
import com.google.code.pathlet.config.def.AdviceConfig;
import com.google.code.pathlet.config.def.AdviceConfigHandler;
import com.google.code.pathlet.config.def.AdviceConfigHandler.AdviceExecutor;
import com.google.code.pathlet.config.def.PointcutConfig;
import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletConstants;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.ResourceInstanceProcessor;
import com.google.code.pathlet.core.impl.ProceedingJoinPointImpl.Processor;

public class AopInstanceEvent implements ResourceInstanceEvent {
	
	//store all aspect configurations.  Each AspectConfig represent one advice(method) and pointcut pair.
	private  List<AdviceConfigHandler> configHandlerList = new ArrayList<AdviceConfigHandler>();
	
	//The catch Map type parameters: 
	//    scope          path      JoinPoint-Method     JoinPoint-AdviceExecutor
	//Map<String,    Map<Path, Map<Method,              List<AdviceExecutor>>>>
	private Map<String, Map<Path, Map<Method, List<AdviceExecutor>>>> scopesPathJoinPointsCache 
			= new HashMap<String, Map<Path, Map<Method, List<AdviceExecutor>>>>();
	
	private ConfigManager configManager;
	
	public AopInstanceEvent(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public Object instanceTarget(ResourceInstanceProcessor processor,
			InstanceSpace space, Resource resource) {

		Object target = processor.process(space);
		
		return aspectProcess(space.getScope(), resource, target);
	}
	
	
	public void flush(String scope, PathPattern matchPattern) {
		
		//clean configHandlerList
		if(PathletConstants.CONTAINER_SCOPE.equals(scope)) {
			if(matchPattern != null) {
				Iterator<AdviceConfigHandler> it = configHandlerList.iterator();
				while(it.hasNext()) {
					AdviceConfigHandler handler = it.next();
					if(matchPattern.isMatch(handler.getAdvisorPath())) {
						it.remove();
					}
				}
			}
			else {
				configHandlerList.clear();
			}
		}
		
		//clean the cache scopesPathJoinPointCache
		Map<Path, Map<Method, List<AdviceExecutor>>> pathJoinPointsMap = getPathJointPointsMap(scope, false);
		if(pathJoinPointsMap != null) {
			if(matchPattern != null) {
				Iterator<Path> it = pathJoinPointsMap.keySet().iterator();
				while(it.hasNext()) {
					Path path = it.next();
					if(matchPattern.isMatch(path)) {
						it.remove();
					}
				}
			}
			else {
				pathJoinPointsMap.clear();
			}
		}
		
	}

	private Object aspectProcess(String instanceSpaceScope, Resource resource, Object target) {
		
		if(target != null) {
			
			//Find the aspect configuration for this target
			boolean isAspectTarget = parseAspectConfig(instanceSpaceScope, resource, target);
			
			//If not found the aspect configuration, this target will found the matching point cut and create the dynamic proxy.
			//(Aspect target could not be join point and be process by advicor method.)
			if(isAspectTarget == false) { 

				Map<Path, Map<Method, List<AdviceExecutor>>> pathJoinPointsMap = getPathJointPointsMap(instanceSpaceScope, true);
				
				Map<Method, List<AdviceExecutor>> pathJoinPoints = pathJoinPointsMap.get(resource.getPath());
				if(pathJoinPoints == null) {
					pathJoinPoints = new HashMap<Method, List<AdviceExecutor>>();
					for(AdviceConfigHandler aspectConfig : configHandlerList) {
						if(aspectConfig.isMatchByPath(instanceSpaceScope, resource.getPath())) {
							
							Method[] methods = target.getClass().getMethods();
							for(Method method : methods) {
								if(aspectConfig.isMatchByMethod(method)) {
									List<AdviceExecutor> advices = pathJoinPoints.get(method);
									if(advices == null) {
										advices = new ArrayList<AdviceExecutor>();
										pathJoinPoints.put(method, advices);
									}
									advices.add(aspectConfig.getAdvice());
								}
							}
						}
					}
					pathJoinPointsMap.put(resource.getPath(), pathJoinPoints);
				}
				
				if(pathJoinPoints.size() > 0) {
					return getProxy(pathJoinPoints, resource, target);
				}
			}
		}	
		
		return target;
	}
	
	private Map<Path, Map<Method, List<AdviceExecutor>>> getPathJointPointsMap(String scope, boolean create) {
		Map<Path, Map<Method, List<AdviceExecutor>>> pathJoinPointsMap = scopesPathJoinPointsCache.get(scope);
		if(create == true && pathJoinPointsMap == null) {
			pathJoinPointsMap = new HashMap<Path, Map<Method, List<AdviceExecutor>>>();
			scopesPathJoinPointsCache.put(scope, pathJoinPointsMap);
		}
		
		return pathJoinPointsMap;
	}
	
	/**
	 * <p>Load aspect configurations, if the target has been found corresponding aspect configurations. </p>
	 * <p>The advice method must be public method. Any private and protected method will be ignored.</p
	 * @param target
	 * @return  return true as aspect target, false as none aspect target
	 */
	private boolean parseAspectConfig(String scope, Resource resource, Object target) {
		boolean isAspectTarget = false;
		
		//Parse advices from configurations
		Collection<AdviceConfig> adviceConfigs = configManager.getAdices(resource.getPath());
		if(adviceConfigs != null) {
			if(PathletConstants.CONTAINER_SCOPE.equals(scope) == false) {
				throw new ConfigException("The resource(path='" + resource.getPath() + "') is AOP advisor. It could not be instanced in InstatnceSpace which scope is other than '" + PathletConstants.CONTAINER_SCOPE + "'");
			}
			
			for(AdviceConfig config : adviceConfigs) {
				configHandlerList.add(new AdviceConfigHandler(resource.getPath(), target, config));
			}
		}
		
		//Parse advices from class annotation. If it contains aspect configurations, they will be loaded and stored in 
		if (target.getClass().getAnnotation(Advisor.class) != null) {
			if(PathletConstants.CONTAINER_SCOPE.equals(scope) == false) {
				throw new ConfigException("The resource(path='" + resource.getPath() + "') is AOP advisor. It could not be instanced in InstatnceSpace which scope is other than '" + PathletConstants.CONTAINER_SCOPE + "'");
			}
			
			isAspectTarget = true;
			Method[] adviceMethods = target.getClass().getMethods();
			for (Method adviceMethod : adviceMethods) {
		
				Annotation[] anns = adviceMethod.getAnnotations();
				for (Annotation ann : anns) {
					if(ann instanceof Pointcut){	
						PointcutConfig pointcutCfg = new PointcutConfig((Pointcut)ann);
						configHandlerList.add(new AdviceConfigHandler(resource.getPath(), target, adviceMethod, pointcutCfg));
					}
				}
			}
		}
		
		return isAspectTarget;
	}
	
	
	
	private Object getProxy(Map<Method, List<AdviceExecutor>> adviceMap, Resource resource, Object target) {

		Class<?> targetClass = target.getClass();
		ClassLoader classLoader = targetClass.getClassLoader();
		
		// Configure CGLIB Enhancer...
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(classLoader);
		enhancer.setSuperclass(targetClass);

		enhancer.setCallbacks(new Callback[]{new CGlibMethodInterceptor(adviceMap, resource, target)});

		Object proxy = enhancer.create();
		return proxy;

	}

	
	
	/**
	 * 
	 * General purpose AOP callback. 
	 */
	private class CGlibMethodInterceptor implements MethodInterceptor {

		private Map<Method, List<AdviceExecutor>> adviceMap;
		
		private Resource resource;
		
		private Object target;
		
		public CGlibMethodInterceptor(Map<Method, List<AdviceExecutor>> adviceMap, Resource resource, Object target) {
			this.adviceMap = adviceMap;
			this.resource = resource;
			this.target = target;
		}
		
		
		public Object intercept(Object enhancedObject, final Method targetMethod, final Object[] targetArgs,
				MethodProxy targetMethodProxy) throws Throwable {

			Object returnValue = null;
			List<AdviceExecutor> advices = adviceMap.get(targetMethod);
			try {
				if(advices != null && advices.size() > 0) { //If found one or more advice on this method
					final Iterator<AdviceExecutor> adivceIt = advices.iterator();
					AdviceExecutor topAdvice = adivceIt.next();
					
					ProceedingJoinPointImpl joinPoint = new ProceedingJoinPointImpl(resource, target, enhancedObject, targetMethod, targetArgs);
					joinPoint.setProcessor(new Processor() { 
						public Object proceed(ProceedingJoinPointImpl thisJoinpoint, Object[] args) throws Throwable {
							if(adivceIt.hasNext()) {
								AdviceExecutor nextAdvice = adivceIt.next();
								return nextAdvice.execute(thisJoinpoint);
							}
							else {
								try {
									return targetMethod.invoke(target, args);
								}
								catch(InvocationTargetException ite) {
									throw ite.getTargetException(); //catch the targetMethod throwing exception, and throw the target exception.
								}
							}
						}
					});
					
					returnValue = topAdvice.execute(joinPoint);	
				}
				else {
					returnValue = targetMethodProxy.invoke(target, targetArgs);
				}
			}
			catch(InvocationTargetException ite) {
				throw ite.getTargetException(); //catch the targetMethod throwing exception, and throw the target exception.
			}

			return returnValue;
		}
		
	}
	

}
